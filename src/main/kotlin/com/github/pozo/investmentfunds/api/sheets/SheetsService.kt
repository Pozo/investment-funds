package com.github.pozo.investmentfunds.api.sheets

import com.github.pozo.investmentfunds.api.redis.RedisService
import com.github.pozo.investmentfunds.domain.RateHeaders
import com.github.pozo.investmentfunds.domain.RedisHashKey
import org.springframework.stereotype.Service

@Service
class SheetsService : SheetsAPI {

    override fun getRatesByIsinAndFilter(
        isin: String,
        filter: SheetsController.RatesFilter
    ): List<Map<String, String>> {
        val attribute = filter.attribute ?: RateHeaders.RATE.name.lowercase()

        if(filter.endDate == null && filter.startDate == null && filter.attribute == null) {
            RedisService.jedis.pipelined().use { pipeline ->
                val results = RedisService.jedis.zrange("rate:keys#$isin", 0, -1).toList()
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