package com.github.pozo.investmentfunds.api.sheets

import com.github.pozo.investmentfunds.api.redis.RedisService
import com.github.pozo.investmentfunds.domain.DataFlowConstants
import com.github.pozo.investmentfunds.domain.RateHeaders
import com.github.pozo.investmentfunds.domain.RedisHashKey
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

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

    override fun getRatesByIsinAndFilter(
        isin: String,
        filter: SheetsController.RatesFilter
    ): List<Map<String, String>> {
        val attribute = filter.attribute ?: RateHeaders.RATE.name.lowercase()

        if (filter.startDate != null) {
            val fromKey = RedisHashKey.calculateScore(filter.startDate)
            val toKey = if(filter.endDate != null) {
                RedisHashKey.calculateScore(filter.endDate)
            } else {
                Double.MAX_VALUE
            }

            RedisService.jedis.pipelined().use { pipeline ->
                val results = RedisService.jedis.zrangeByScore("rate:keys#$isin", fromKey, toKey).toList()
                    .map { pipeline.hget(it, RateHeaders.DATE.name.lowercase()) to pipeline.hget(it, attribute) }
                pipeline.sync()

                return results.stream()
                    .map {
                        mapOf<String, String>(
                            RateHeaders.DATE.name.lowercase() to it.first.get(),
                            attribute to it.second.get()
                        )
                    }
                    .toList()
            }
        }
        if (filter.endDate == null) {
            // return only latest entry
            RedisService.jedis.pipelined().use { pipeline ->
                val results =
                    RedisService.jedis.zrevrangeByScore("rate:keys#$isin", Double.MAX_VALUE, Double.MIN_VALUE, 0, 1)
                        .toList()
                        .map { pipeline.hget(it, RateHeaders.DATE.name.lowercase()) to pipeline.hget(it, attribute) }
                pipeline.sync()

                return results.stream()
                    .map {
                        mapOf<String, String>(
                            RateHeaders.DATE.name.lowercase() to it.first.get(),
                            attribute to it.second.get()
                        )
                    }
                    .toList()
            }
        }
        return emptyList()
    }


}