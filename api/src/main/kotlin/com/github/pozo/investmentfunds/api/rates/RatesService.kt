package com.github.pozo.investmentfunds.api.rates

import com.github.pozo.investmentfunds.RedisHashKey
import com.github.pozo.investmentfunds.api.redis.RedisService
import org.springframework.stereotype.Service

@Service
class RatesService() : RatesAPI {

    override fun findAllRatesByISIN(isin: String): List<Rate> {
        RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.zrange("rate:keys#$isin", 0, -1).toList()
                .map { pipeline.hgetAll(it) }
            pipeline.sync()
            return results.map { it.get() as Rate }
        }
    }

    override fun findAllRatesByISINBetween(isin: String, filter: RatesController.RatesFilter): List<Rate> {
        val fromKey = RedisHashKey.calculateScore(filter.startDate)
        val toKey = RedisHashKey.calculateScore(filter.endDate)

        RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.zrangeByScore("rate:keys#$isin", fromKey, toKey).toList()
                .map { pipeline.hgetAll(it) }
            pipeline.sync()
            return results.map { it.get() as Rate }
        }
    }

}