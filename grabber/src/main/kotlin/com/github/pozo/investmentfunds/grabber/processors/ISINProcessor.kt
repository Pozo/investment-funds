package com.github.pozo.investmentfunds.grabber.processors

import org.apache.camel.Exchange
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object ISINProcessor {

    private const val ISIN_PATTERN = "HU[0-9]{10}"
    private const val DATE_FORMAT = "yyyy.MM.dd"

    const val START_YEAR = 1992 // there is no data before this date

    const val ISIN_LIST_HEADER_NAME = "isin-list"
    const val START_DATE_HEADER_NAME = "start-date"
    const val END_DATE_HEADER_NAME = "end-date"

    private val format = SimpleDateFormat(DATE_FORMAT)

    fun extractISINList(): (exchange: Exchange) -> Unit = { exchange ->
        val htmlContent: String = exchange.message.body as String

        val pattern = Pattern.compile(ISIN_PATTERN)
        val matcher: Matcher = pattern.matcher(htmlContent)

        val isinSet = HashSet<String>()

        while (matcher.find()) {
            isinSet.add(matcher.group())
        }
        exchange.message.body = isinSet.joinToString(separator = "\n")
    }

    fun setISINListHeaderValue(): (exchange: Exchange) -> Unit = { exchange ->
        val message = exchange.getIn().body as String
        exchange.message.setHeader(ISIN_LIST_HEADER_NAME, message.replace('\n', ','))
    }

    fun isValidIntervalHeaderValues(): (exchange: Exchange) -> Boolean {
        return { exchange ->
            val message: String = exchange.message.body as String

            try {
                val (start, end) = message.trim().split(",")
                val startDate = format.parse(start)
                val endDate = format.parse(end)

                !startDate.before(format.parse("$START_YEAR.01.01"))
                        && !startDate.after(endDate)
                        && !endDate.after(Date())
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setISINIntervalHeaderValues(): (exchange: Exchange) -> Unit = { exchange ->
        val message: String = exchange.message.body as String
        val (startDate, endDate) = message.trim().split(",")

        exchange.message.setHeader(START_DATE_HEADER_NAME, startDate)
        exchange.message.setHeader(END_DATE_HEADER_NAME, endDate)
    }

}