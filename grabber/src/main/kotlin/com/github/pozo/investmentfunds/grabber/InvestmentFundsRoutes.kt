package com.github.pozo.investmentfunds.grabber

import com.github.pozo.investmentfunds.DataFlowConstants
import com.github.pozo.investmentfunds.grabber.processors.CsvProcessor
import com.github.pozo.investmentfunds.grabber.processors.CsvProcessor.ISIN_HEADER_NAME
import com.github.pozo.investmentfunds.grabber.processors.ISINProcessor
import com.github.pozo.investmentfunds.grabber.processors.ISINProcessor.END_DATE_HEADER_NAME
import com.github.pozo.investmentfunds.grabber.processors.ISINProcessor.ISIN_LIST_HEADER_NAME
import com.github.pozo.investmentfunds.grabber.processors.ISINProcessor.START_DATE_HEADER_NAME
import com.github.pozo.investmentfunds.grabber.processors.RedisProcessor

import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder

import org.apache.camel.dataformat.csv.CsvDataFormat
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.util.*

class InvestmentFundsRoutes : RouteBuilder() {

    companion object {
        const val FUND_DATA_ROUTE_NAME = "direct:fund-data"
        const val RATE_DATA_ROUTE_NAME = "direct:rate-data"
    }

    override fun configure() {
        val isinGroupSize: Int = resolveProperty("processors.isin-group-size") { it.toInt() }.orElseThrow()

        val redisHost: String = resolveProperty("redis.host").orElseThrow()
        val redisPort: Int = resolveProperty("redis.port") { it.toInt() }.orElseThrow()

        context.registry.bind("stringRedisSerializer", StringRedisSerializer())
        from("spring-redis://$redisHost:$redisPort?command=SUBSCRIBE&channels=${DataFlowConstants.GRAB_DATA_COMMAND_CHANNEL_NAME.field}&serializer=#stringRedisSerializer")
            .onCompletion().onCompleteOnly().to("direct:end-csv-processing").end()
            .filter(ISINProcessor.isValidIntervalHeaderValues())
        .process(ISINProcessor.setISINIntervalHeaderValues())
        .to("https://www.bamosz.hu/egyes-alapok-kivalasztasa")
            .convertBodyTo(String::class.java, "UTF-8")
        .process(ISINProcessor.extractISINList())
            .split().tokenize("\n", isinGroupSize)
            .convertBodyTo(String::class.java, "UTF-8")
        .process(ISINProcessor.setISINListHeaderValue())
            .setHeader("Content-Type", constant("application/x-www-form-urlencoded"))
            .setBody()
            .simple(
                "selectedAlap=\${header.$ISIN_LIST_HEADER_NAME}" +
                        "&selectedData=arfolyam,nettoEszkozertek,kifizetettHozamok,napiBefJegyForgalom,napiBefJegyForgalomSzazalek,referenciaIndex,hufNee,hufCf" +
                        "&selectedHozams=3honapos,6honapos,1eves,3eves,5eves,10eves,evElejetol,indulastol" +
                        "&selectedOption=idointervallumraVonatkozoStatisztika" +
                        "&intervallumraDateStart=\${header.$START_DATE_HEADER_NAME}" +
                        "&intervallumraDateEnd=\${header.$END_DATE_HEADER_NAME}" +
                        "&sortDirection=dec" +
                        "&separator=vesszo"
            )
            .log(LoggingLevel.INFO, "Downloading CSV files for the interval ('\${header.$START_DATE_HEADER_NAME}'-'\${header.$END_DATE_HEADER_NAME}') and ISIN numbers: '\${header.$ISIN_LIST_HEADER_NAME}'")
        .to("https://www.bamosz.hu/bamosz-public-letoltes-portlet/data.download")
            .convertBodyTo(String::class.java, "ISO-8859-2")
            .convertBodyTo(String::class.java, "UTF-8")
            .unmarshal(CsvDataFormat().apply {
                delimiter = ','
                quote = '"'
                escape = '\\'
            })
            .log(LoggingLevel.INFO, "Vertically split CSV files according to ISIN numbers.")

        .process(CsvProcessor.splitVertically())
            .split().body()
            .log(LoggingLevel.INFO, "Horizontally segment CSV chunks based on fund and rates data.")
        .process(CsvProcessor.splitHorizontallyAndSend())
            .end()

        from(FUND_DATA_ROUTE_NAME).doTry()
            .log(LoggingLevel.INFO, "Processing fund data for '\${header.$ISIN_HEADER_NAME}'")
        .process(RedisProcessor.saveFundData())
            .log(LoggingLevel.INFO, "Fund data processed for '\${header.$ISIN_HEADER_NAME}'")
            .doCatch(Exception::class.java)
            .log(LoggingLevel.ERROR, "An error occurred during the processing : \${exception.message}")
            .transform().simple("\${exception.message}")
            .end()

        from(RATE_DATA_ROUTE_NAME).doTry()
            .log(LoggingLevel.INFO, "Processing rate data for '\${header.$ISIN_HEADER_NAME}'")
        .process(RedisProcessor.saveRateData())
            .log(LoggingLevel.INFO, "Rate data processed for '\${header.$ISIN_HEADER_NAME}'")
            .doCatch(Exception::class.java)
            .log(LoggingLevel.ERROR, "An error occurred during the processing : \${exception.message}")
            .transform().simple("\${exception.message}")
            .end()

        from("direct:end-csv-processing")
            .filter(ISINProcessor.isValidIntervalHeaderValues())
            .log("All CSV processed for (\${header.$START_DATE_HEADER_NAME}-\${header.$END_DATE_HEADER_NAME})")
            .process(RedisProcessor.saveMetaData())
    }

    private fun resolveProperty(propertyName: String): Optional<String> =
        camelContext.propertiesComponent.resolveProperty(propertyName)

    private fun <T> resolveProperty(propertyName: String, operation: (String) -> T): Optional<T> =
        resolveProperty(propertyName)
            .map { operation(it) }

}
