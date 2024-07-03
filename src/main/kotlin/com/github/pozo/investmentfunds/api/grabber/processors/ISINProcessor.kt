package com.github.pozo.investmentfunds.api.grabber.processors

import com.github.pozo.investmentfunds.domain.DataFlowConstants
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object ISINProcessor {

    private val logger = LoggerFactory.getLogger(ISINProcessor::class.java)

    private const val ISIN_PATTERN = "HU[0-9]{10}"

    const val ISIN_LIST_HEADER_NAME = "isin-list"
    const val START_DATE_HEADER_NAME = "start-date"
    const val END_DATE_HEADER_NAME = "end-date"

    private val format = SimpleDateFormat(DataFlowConstants.GRAB_DATA_COMMAND_DATE_FORMAT.field)

    fun extractISINList(): (exchange: Exchange) -> Unit = { exchange ->
        val htmlContent: String = exchange.message.body as String

        val pattern = Pattern.compile(ISIN_PATTERN)
        val matcher: Matcher = pattern.matcher(htmlContent)

        val isinSet = HashSet<String>()

        while (matcher.find()) {
            isinSet.add(matcher.group())
        }
        logger.info("The extracted ISIN set is '$isinSet'")
        exchange.message.body = isinSet.joinToString(separator = "\n")
    }

    fun setISINListHeaderValue(): (exchange: Exchange) -> Unit = { exchange ->
        val message = exchange.getIn().body as String
        exchange.message.setHeader(ISIN_LIST_HEADER_NAME, message.replace('\n', ','))
    }

    fun isValidIntervalValues(): (exchange: Exchange) -> Boolean {
        return { exchange ->
            val message: String = exchange.message.body as String

            try {
                val (start, end) = message.trim().split(DataFlowConstants.GRAB_DATA_COMMAND_SEPARATOR.field)
                val startDate = format.parse(start)
                val endDate = format.parse(end)
                logger.info("The extracted start date is '$startDate' and end date is '$endDate'")

                !startDate.before(format.parse(DataFlowConstants.START_YEAR_DATE.field))
                        && !startDate.after(endDate)
                        && !endDate.after(Date())
                        && !isSameDay(startDate, endDate)
            } catch (e: Exception) {
                logger.error("The given interval values are not correct '$message'", e)
                false
            }
        }
    }

    private fun isSameDay(startDate: Date, endDate: Date): Boolean {
        val startDateCalendar = Calendar.getInstance()
        startDateCalendar.time = startDate
        val endDateCalendar = Calendar.getInstance()
        endDateCalendar.time = endDate

        return startDateCalendar[Calendar.YEAR] == endDateCalendar[Calendar.YEAR]
                && startDateCalendar[Calendar.MONTH] == endDateCalendar[Calendar.MONTH]
                && startDateCalendar[Calendar.DAY_OF_MONTH] == endDateCalendar[Calendar.DAY_OF_MONTH]
    }

    fun setISINIntervalHeaderValues(): (exchange: Exchange) -> Unit = { exchange ->
        val message: String = exchange.message.body as String
        val (startDate, endDate) = message.trim().split(DataFlowConstants.GRAB_DATA_COMMAND_SEPARATOR.field)

        logger.info("The extracted start date is '$startDate' and end date is '$endDate'")
        exchange.message.setHeader(START_DATE_HEADER_NAME, startDate)
        exchange.message.setHeader(END_DATE_HEADER_NAME, endDate)
    }

}