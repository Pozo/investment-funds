package com.github.pozo.investmentfunds.api.grabber.processors

import com.github.pozo.investmentfunds.api.redis.RedisService
import com.github.pozo.investmentfunds.domain.DataFlowConstants
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import redis.clients.jedis.resps.Tuple
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object ISINProcessor {

    private val logger = LoggerFactory.getLogger(ISINProcessor::class.java)

    private const val ISIN_PATTERN = "HU[0-9]{10}"

    const val ISIN_LIST_HEADER_NAME = "isin"
    const val START_DATE_HEADER_NAME = "start-date"
    const val END_DATE_HEADER_NAME = "end-date"

    private val format = SimpleDateFormat(DataFlowConstants.DOMAIN_DATE_FORMAT.field)

    private val redisEntryFormat = SimpleDateFormat(DataFlowConstants.RATES_KEY_DATE_FORMAT.field)

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

    fun isValidIntervalValues(): (exchange: Exchange) -> Boolean {
        return { exchange ->
            try {
                val startDate = format.parse(exchange.message.headers[START_DATE_HEADER_NAME] as String)
                val endDate = format.parse(exchange.message.headers[END_DATE_HEADER_NAME] as String)
                logger.info("'${exchange.`in`.headers[ISIN_LIST_HEADER_NAME]}' The extracted start date is '$startDate' and end date is '$endDate'")

                !startDate.before(format.parse(DataFlowConstants.START_YEAR_DATE.field))
                        && !startDate.after(endDate)
                        && !endDate.after(Date())
                        && !isSameDay(startDate, endDate)
            } catch (e: Exception) {
                logger.error("'${exchange.`in`.headers[ISIN_LIST_HEADER_NAME]}' The given interval values are not correct '${exchange.message}'", e)
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
        val isin: String = exchange.message.body as String
        val mostRecentEntry = getMostRecentEntryFor(isin)

        if(mostRecentEntry == null) {
            logger.info("'$isin' There is no entry for this ISIN in redis")
            exchange.message.setHeader(ISIN_LIST_HEADER_NAME, isin)
            exchange.message.setHeader(START_DATE_HEADER_NAME, DataFlowConstants.START_YEAR_DATE.field)
            exchange.message.setHeader(END_DATE_HEADER_NAME, format.format(Date()))
        } else {
            val mostRecentEntryDate = redisEntryFormat.parse(extractDate(mostRecentEntry.element))
            logger.info("'$isin' The last entry date for this ISIN is '$mostRecentEntryDate'")
            exchange.message.setHeader(ISIN_LIST_HEADER_NAME, isin)
            exchange.message.setHeader(START_DATE_HEADER_NAME, format.format(addOneDay(mostRecentEntryDate)))
            exchange.message.setHeader(END_DATE_HEADER_NAME, format.format(Date()))
        }
    }

    private fun addOneDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.time
    }

    private fun getMostRecentEntryFor(isin: String): Tuple? {
        return RedisService.jedis.zrevrangeByScoreWithScores("rate:keys#$isin", "+inf", "-inf", 0, 1)
            .firstOrNull()
    }

    private fun extractDate(input: String): String? {
        return input.split("#").takeIf { it.size > 1 }?.get(1)
    }

}