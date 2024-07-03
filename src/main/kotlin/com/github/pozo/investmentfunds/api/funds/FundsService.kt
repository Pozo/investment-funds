package com.github.pozo.investmentfunds.api.funds

import com.github.pozo.investmentfunds.api.redis.RedisService
import org.springframework.stereotype.Service
import redis.clients.jedis.Response

@Service
class FundsService() : FundsAPI {

    override fun findAllFunds(): List<Fund> {
        RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.keys("fund#*").map { pipeline.hgetAll(it) }

            pipeline.sync()
            return results.map { it.get() as Fund }
        }
    }

    override fun findAllFunds(field: String, value: String): List<Fund> {
        val fundKeys: MutableList<Pair<String, Response<String>>> = RedisService.jedis.pipelined().use { pipeline ->
            val hashKeys = RedisService.jedis.keys("fund#*")
            val fundKeys = mutableListOf<Pair<String, Response<String>>>()

            hashKeys.map { fundKeys.add(it to pipeline.hget(it, field)) }

            pipeline.sync()
            return@use fundKeys
        }

        RedisService.jedis.pipelined().use { pipeline ->
            val results = fundKeys
                .filter { it.second.get().contains(value, true) }
                .map { pipeline.hgetAll(it.first) }

            pipeline.sync()
            return results.map { it.get() as Fund }
        }
    }

}