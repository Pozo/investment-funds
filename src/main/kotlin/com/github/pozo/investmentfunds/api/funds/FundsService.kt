package com.github.pozo.investmentfunds.api.funds

import com.github.pozo.investmentfunds.api.redis.RedisService
import org.springframework.stereotype.Service
import redis.clients.jedis.Response

@Service
class FundsService : FundsAPI {

    override fun findAllFunds(): List<Fund> {
        RedisService.jedis.pipelined().use { pipeline ->
            val results = RedisService.jedis.keys("fund#*").map { pipeline.hgetAll(it) }

            pipeline.sync()
            return results.map { it.get() as Fund }
        }
    }

    override fun filterFunds(parameters: Map<String, String>): List<Fund> {
        val fundKeys: MutableList<Pair<String, Response<List<String>>>> = RedisService.jedis.pipelined().use { pipeline ->
            val fundKeys = mutableListOf<Pair<String, Response<List<String>>>>()

            RedisService.jedis.keys("fund#*")
                .map { fundKeys.add(it to pipeline.hmget(it, *parameters.keys.toTypedArray())) }

            pipeline.sync()
            return@use fundKeys
        }

        RedisService.jedis.pipelined().use { pipeline ->
            val results = fundKeys
                .filter { parameters.toList() == parameters.keys.zip(it.second.get()) }
                .map { pipeline.hgetAll(it.first) }

            pipeline.sync()
            return results.map { it.get() as Fund }
        }
    }

}