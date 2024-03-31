package com.github.pozo.investmentfunds.api.rates

import com.github.pozo.investmentfunds.RedisHashKey
import com.github.pozo.investmentfunds.api.redis.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RatesService() : RatesAPI {

    private val START_YEAR = 1990

    private val DATE_FORMAT = "yyyy/MM/dd"

    override fun findAllRatesByISIN(isin: String): List<Rate> {
        RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.zrange("rate:keys#$isin", 0, -1).toList()
                .map { pipeline.hgetAll(it) }
            pipeline.sync()
            return results.map { it.get() as Rate }
        }
    }

    override fun findAllRatesByISINBetween(isin: String, filter: RatesController.RatesFilter): List<Rate> {
        val fromKey = RedisHashKey.calculateScore(
            DATE_FORMAT,
            START_YEAR,
            filter.from
        )
        val toKey = RedisHashKey.calculateScore(
            DATE_FORMAT,
            START_YEAR,
            filter.to
        )

        RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.zrangeByScore("rate:keys#$isin", fromKey, toKey).toList()
                .map { pipeline.hgetAll(it) }
            pipeline.sync()
            return results.map { it.get() as Rate }
        }
    }

}