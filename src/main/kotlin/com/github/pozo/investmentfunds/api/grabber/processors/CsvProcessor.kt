package com.github.pozo.investmentfunds.api.grabber.processors

import com.github.pozo.investmentfunds.api.grabber.InvestmentFundsRoutes.Companion.FUND_DATA_ROUTE_NAME
import com.github.pozo.investmentfunds.api.grabber.InvestmentFundsRoutes.Companion.RATE_DATA_ROUTE_NAME
import com.github.pozo.investmentfunds.domain.FundHeaders
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import java.util.stream.Collectors
import java.util.stream.IntStream

typealias VerticalPiece = MutableList<CsvProcessor.DataRow>
typealias CSVLine = List<String>

object CsvProcessor {

    private val logger = LoggerFactory.getLogger(CsvProcessor::class.java)

    const val ISIN_HEADER_NAME = "isin"

    private const val DATE_FIELD_NAME = "Dátum"

    data class DataRow(
        val headerColumn: String,
        val dataColumns: List<String>
    )

    fun splitVertically(): (exchange: Exchange) -> Unit = { exchange ->
        val csvLinesWithMetaRow: List<CSVLine> = exchange.getIn().getBody(List::class.java) as List<CSVLine>
        val csvLines = csvLinesWithMetaRow.drop(1) // skipping meta data row
        val firstHeaderLine = csvLines.first()
        val entries = mutableMapOf<Pair<Int, Int>, VerticalPiece>()

        logger.info("The CSV contains '${csvLines.size}' number of entries, and '${firstHeaderLine.size}' header columns")

        val verticalIndexes: MutableList<Int> =
            IntStream.range(1, firstHeaderLine.size) // skipping first "label" column
                .filter { i: Int -> firstHeaderLine[i].trim().isNotEmpty() || i == firstHeaderLine.size }
                .boxed()
                .collect(Collectors.toList())

        for (list in csvLines) {
            verticalIndexes.windowed(2, 1, true) { window ->
                val key: Pair<Int, Int> = if (window.size == 1) {
                    window.first() to firstHeaderLine.size
                } else {
                    window.first() to window.last()
                }
                entries.getOrElse(key) { mutableListOf<DataRow>().also { entries[key] = it } }
                    .add(DataRow(list.first(), list.subList(key.first, key.second).toList()))
            }
        }
        exchange.getIn().body = entries.values
    }

    fun splitHorizontallyAndSend(): (exchange: Exchange) -> Unit = { exchange ->
        val csvRows = exchange.getIn().body as VerticalPiece

        val fundHeaders = mutableListOf<String>()
        val fundData = mutableListOf<String>()

        val rateHeaders = mutableListOf<String>()
        val rateData = mutableListOf<List<String>>()

        var rateDataSection = false
        for (row in csvRows) {
            if (row.headerColumn == DATE_FIELD_NAME) {
                rateDataSection = true
                rateHeaders.add(row.headerColumn)
                rateHeaders.addAll(row.dataColumns)
                continue
            }
            if (rateDataSection) {
                val element = mutableListOf(row.headerColumn)
                element.addAll(row.dataColumns)
                rateData.add(element)
            } else {
                fundHeaders.add(row.headerColumn)
                fundData.add(row.dataColumns.first())
            }
        }
        val isin = fundData[fundHeaders.indexOf(FundHeaders.ISIN.field)]
        val sanitizedIsin = Regex("\\HU[0-9]{9,10}\\b").find(isin)?.value ?: isin

        exchange.context.createProducerTemplate()
            .sendBodyAndHeader(
                FUND_DATA_ROUTE_NAME,
                Pair(fundHeaders, fundData),
                ISIN_HEADER_NAME,
                sanitizedIsin
            )
        exchange.context.createProducerTemplate()
            .sendBodyAndHeader(
                RATE_DATA_ROUTE_NAME,
                Pair(rateHeaders, rateData),
                ISIN_HEADER_NAME,
                sanitizedIsin
            )
    }
}