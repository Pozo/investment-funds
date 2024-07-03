package com.github.pozo.investmentfunds.api.redis

import redis.clients.jedis.ConnectionPoolConfig
import redis.clients.jedis.JedisPooled

object RedisService {

    private val poolConfig: ConnectionPoolConfig = ConnectionPoolConfig().apply {
        maxTotal = 100
        maxIdle = 50
        minIdle = 10
    }

    val jedis = JedisPooled(poolConfig, "investmentfunds-redis", 6379)
}