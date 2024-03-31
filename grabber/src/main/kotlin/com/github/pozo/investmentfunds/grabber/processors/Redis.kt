package com.github.pozo.investmentfunds.grabber.processors

import com.github.pozo.investmentfunds.FundHeaders
import com.github.pozo.investmentfunds.RateHeaders
import com.github.pozo.investmentfunds.RedisHashKey
import com.github.pozo.investmentfunds.grabber.InvestmentFunds
import com.github.pozo.investmentfunds.grabber.InvestmentFundsRoutes
import org.apache.camel.Exchange
import redis.clients.jedis.JedisPooled

object Redis {

    private val jedis = JedisPooled("localhost", 6379)

    fun saveMeta(): (exchange: Exchange) -> Unit = { exchange ->
        val body = exchange.getIn().getBody(Pair::class.java) as Pair<List<String>, List<String>>

        val isin = exchange.message.getHeader(InvestmentFundsRoutes.ISIN_HEADER_NAME, String::class.java)
        val header = body.first
        val data = body.second

        val keyValuePairs = FundHeaders.entries
            .filter { header.indexOf(it.field) != -1 }
            .associate { it.name.lowercase() to data[header.indexOf(it.field)] }

        jedis.hset("fund#$isin", keyValuePairs)
    }

    fun saveData(): (exchange: Exchange) -> Unit = { exchange ->
        val body = exchange.`in`.getBody(Pair::class.java) as Pair<List<String>, List<List<String>>>

        val isin = exchange.message.getHeader(InvestmentFundsRoutes.ISIN_HEADER_NAME, String::class.java)
        val header = body.first
        val data = body.second

        // filter non empty fields
        jedis.pipelined().use { pipeline ->
            data.filter { it[header.indexOf(RateHeaders.DATE.field)].isNotEmpty() }
                .filter { it.count { field -> field.isNotEmpty() } > 1 }
                .forEach { entry ->
                    val keyValuePairs = RateHeaders.entries
                        .filter { header.indexOf(it.field) != -1 }
                        .associate { it.name.lowercase() to entry[header.indexOf(it.field)] }

                    val rateKey = "rate:$isin#${entry[header.indexOf(RateHeaders.DATE.field)]}"
                    pipeline.hset(rateKey, keyValuePairs)
                    pipeline.zadd(
                        "rate:keys#$isin",
                        RedisHashKey.calculateScore(
                            InvestmentFunds.DATE_FORMAT,
                            InvestmentFunds.START_YEAR,
                            entry[header.indexOf(RateHeaders.DATE.field)]
                        ),
                        rateKey
                    )
                }
            pipeline.sync()
        }
    }
}