package com.github.pozo.investmentfunds.api.grabber


import com.github.pozo.investmentfunds.api.grabber.processors.CsvProcessor
import com.github.pozo.investmentfunds.api.grabber.processors.ISINProcessor
import com.github.pozo.investmentfunds.api.grabber.processors.ISINProcessor.END_DATE_HEADER_NAME
import com.github.pozo.investmentfunds.api.grabber.processors.ISINProcessor.ISIN_LIST_HEADER_NAME
import com.github.pozo.investmentfunds.api.grabber.processors.ISINProcessor.START_DATE_HEADER_NAME
import com.github.pozo.investmentfunds.api.grabber.processors.RedisProcessor
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.CsvDataFormat
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InvestmentFundsRoutes : RouteBuilder() {

    private val logger = LoggerFactory.getLogger(InvestmentFundsRoutes::class.java)

    companion object {
        const val FUND_DATA_ROUTE_NAME = "direct:fund-data"
        const val RATE_DATA_ROUTE_NAME = "direct:rate-data"
    }

    override fun configure() {
        from("direct:grab-data").routeId("grab-data")
            .onCompletion().to("direct:end-csv-processing").end()
        .to("https://www.bamosz.hu/egyes-alapok-kivalasztasa")
            .convertBodyTo(String::class.java, "UTF-8")
        .process(ISINProcessor.extractISINList())
            .split().tokenize("\n")
            .convertBodyTo(String::class.java, "UTF-8")
            .filter { it.message.body as String != "HU0000346663" }
        .process(ISINProcessor.setISINIntervalHeaderValues())
            .filter(ISINProcessor.isValidIntervalValues())
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
            .log(LoggingLevel.INFO, logger, "'\${header.$ISIN_LIST_HEADER_NAME}' Downloading CSV file for the interval '\${header.$START_DATE_HEADER_NAME}'-'\${header.$END_DATE_HEADER_NAME}'")
        .to("https://www.bamosz.hu/bamosz-public-letoltes-portlet/data.download")
            .convertBodyTo(String::class.java, "ISO-8859-2")
            .convertBodyTo(String::class.java, "UTF-8")
            .unmarshal(CsvDataFormat().apply {
                delimiter = ","
                quote = "\""
                escape = "\\"
            })
            //.log(LoggingLevel.INFO, "Vertically split CSV files according to ISIN numbers.")
        .process(CsvProcessor.splitVertically())
            .split().body()
            //.log(LoggingLevel.INFO, "Horizontally segment CSV chunks based on fund and rates data.")
        .process(CsvProcessor.splitHorizontallyAndSend())
            .end()

        from(FUND_DATA_ROUTE_NAME).routeId("fund-data").doTry()
            //.log(LoggingLevel.INFO, "Processing fund data for '\${header.$ISIN_HEADER_NAME}'")
        .process(RedisProcessor.saveFundData())
            //.log(LoggingLevel.INFO, "Fund data processed for '\${header.$ISIN_HEADER_NAME}'")
            .doCatch(Exception::class.java)
            .log(LoggingLevel.ERROR, logger, "An error occurred during the processing : \${exception.message}")
            .transform().simple("\${exception.message}")
            .end()

        from(RATE_DATA_ROUTE_NAME).routeId("rate-data").doTry()
            //.log(LoggingLevel.INFO, "Processing rate data for '\${header.$ISIN_HEADER_NAME}'")
        .process(RedisProcessor.saveRateData())
            //.log(LoggingLevel.INFO, "Rate data processed for '\${header.$ISIN_HEADER_NAME}'")
            .doCatch(Exception::class.java)
            .log(LoggingLevel.ERROR, logger, "An error occurred during the processing : \${exception.message}")
            .transform().simple("\${exception.message}")
            .end()

        from("direct:end-csv-processing").routeId("end-csv-processing")
            .filter { exchange -> exchange.message.headers[START_DATE_HEADER_NAME] != null && exchange.message.headers[END_DATE_HEADER_NAME] != null }
            .log(LoggingLevel.INFO, logger, "All CSV processed for (\${header.$START_DATE_HEADER_NAME}-\${header.$END_DATE_HEADER_NAME})")
    }
}
