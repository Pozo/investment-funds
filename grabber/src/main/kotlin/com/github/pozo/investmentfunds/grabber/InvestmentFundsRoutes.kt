package com.github.pozo.investmentfunds.grabber

import com.github.pozo.investmentfunds.grabber.processors.CsvSplitter
import com.github.pozo.investmentfunds.grabber.processors.ISINParser
import com.github.pozo.investmentfunds.grabber.processors.Redis
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.dataformat.csv.CsvDataFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object InvestmentFunds {
    const val START_YEAR = 1992
    const val DATE_FORMAT = "yyyy/MM/dd"
    const val DATE_FIELD_NAME = "Dátum"
}

class InvestmentFundsRoutes : RouteBuilder() {

    companion object {
        const val META_ROUTE_NAME = "direct:meta"
        const val DATA_ROUTE_NAME = "direct:data"
        const val ISIN_HEADER_NAME = "isin"
        const val ISIN_LIST_HEADER_NAME = "isin-list"
    }

    private val executorService: ExecutorService = Executors.newFixedThreadPool(20)

    override fun configure() {
        from("file:///Users/zoltanpolgar/workspace/private/bamosz/isin-list")
            .split().tokenize("\n", 10)
            .parallelProcessing()
            .convertBodyTo(String::class.java, "UTF-8")
            .process(ISINParser.setISINListHeaderValue())
            .setHeader("Content-Type", constant("application/x-www-form-urlencoded"))
            .setBody()
            .simple(
                "selectedAlap=\${header.$ISIN_LIST_HEADER_NAME}" +
                        "&selectedData=arfolyam,nettoEszkozertek,kifizetettHozamok,napiBefJegyForgalom,napiBefJegyForgalomSzazalek,referenciaIndex,hufNee,hufCf" +
                        "&selectedHozams=3honapos,6honapos,1eves,3eves,5eves,10eves,evElejetol,indulastol" +
                        "&selectedOption=idointervallumraVonatkozoStatisztika" +
                        "&intervallumraDateStart=${InvestmentFunds.START_YEAR}.01.01" +
                        "&intervallumraDateEnd=2024.03.29" +
                        "&sortDirection=dec" +
                        "&separator=vesszo"
            )
            .log(LoggingLevel.INFO, "Downloading CSV files for the following ISIN numbers: '\${header.$ISIN_LIST_HEADER_NAME}'")
            .to("https://www.bamosz.hu/bamosz-public-letoltes-portlet/data.download")
            .convertBodyTo(String::class.java, "ISO-8859-2")
            .convertBodyTo(String::class.java, "UTF-8")
            .unmarshal(CsvDataFormat().apply {
                setDelimiter(',')
                setQuote('"')
                setEscape('\\')
            })
            .log(LoggingLevel.INFO, "Vertically split CSV files according to ISIN numbers.")
            .process(CsvSplitter.splitVertically())
                .split().body()
                .parallelProcessing()
                .executorService(executorService)
            .log(LoggingLevel.INFO, "Horizontally segment CSV chunks based on fund and rates data.")
            .process(CsvSplitter.splitHorizontallyAndSend())

        from(META_ROUTE_NAME).doTry()
            .log(LoggingLevel.INFO, "Processing fund data for '\${header.$ISIN_HEADER_NAME}'")
            .process(Redis.saveMeta())
            .doCatch(Exception::class.java)
            .log(LoggingLevel.ERROR, "An error occurred during the processing : \${exception.message}")
            .transform().simple("\${exception.message}")
            .end()

        from(DATA_ROUTE_NAME).doTry()
            .log(LoggingLevel.INFO, "Processing rate data for '\${header.$ISIN_HEADER_NAME}'")
            .process(Redis.saveData())
            .doCatch(Exception::class.java)
            .log(LoggingLevel.ERROR, "An error occurred during the processing : \${exception.message}")
            .transform().simple("\${exception.message}")
            .end()
    }

}
