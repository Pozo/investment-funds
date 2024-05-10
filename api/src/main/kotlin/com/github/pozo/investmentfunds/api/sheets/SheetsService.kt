package com.github.pozo.investmentfunds.api.sheets

import com.github.pozo.investmentfunds.DataFlowConstants
import com.github.pozo.investmentfunds.RedisHashKey
import com.github.pozo.investmentfunds.api.redis.RedisService
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

@Service
class SheetsService() : SheetsAPI {

    private val format = SimpleDateFormat(DataFlowConstants.RATES_KEY_DATE_FORMAT.field)

    // - **attribute** - [ OPTIONAL - "price" by default ] - The attribute to fetch about ticker from Google Finance and is required if a date is specified.
    // - **start_date** - [ OPTIONAL ] - The start date when fetching historical data.
    // - **end_date|num_days** - [ OPTIONAL ] - The end date when fetching historical data, or the number of days from start_date for which to return data.
    // - **interval** - [ OPTIONAL ] - The frequency of returned data; either "DAILY" or "WEEKLY".
    //
    // =GoogleFinance("WCLD")
    // =GoogleFinance("WCLD", "price")
    // =GoogleFinance("WCLD", "price", TODAY()-3)
    // =GoogleFinance("WCLD", "price", TODAY()-100, 100)
    // =GoogleFinance("WCLD", "price", TODAY()-100, TODAY()-50)

    override fun fundsData(isin: String, filter: SheetsController.FundsFilter): List<Map<String, String>> {
        val attribute = filter.attribute ?: "rate"
        val startDate = filter.startDate ?: format.format(Date())
        val endDate = filter.endDate ?: format.format(Date())

        val fromKey = RedisHashKey.calculateScore(startDate)
        val toKey = RedisHashKey.calculateScore(endDate)

        RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.zrangeByScore("rate:keys#$isin", fromKey, toKey).toList()
                .map { pipeline.hget(it, "date") to pipeline.hget(it, attribute) }
            pipeline.sync()

            return results.stream()
                .map { mapOf<String, String>(
                        "date" to it.first.get(),
                        attribute to it.second.get()
                    ) }
                .toList()
        }
    }


}