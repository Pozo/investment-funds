package com.github.pozo.investmentfunds.api.grabber.processors

import com.github.pozo.investmentfunds.api.grabber.processors.CsvProcessor.ISIN_HEADER_NAME
import com.github.pozo.investmentfunds.api.grabber.processors.ISINProcessor.END_DATE_HEADER_NAME
import com.github.pozo.investmentfunds.domain.DataFlowConstants
import com.github.pozo.investmentfunds.domain.FundHeaders
import com.github.pozo.investmentfunds.domain.RateHeaders
import com.github.pozo.investmentfunds.domain.RedisHashKey
import org.apache.camel.Exchange
import redis.clients.jedis.JedisPooled

object RedisProcessor {

    private val jedis = JedisPooled("investmentfunds-redis", 6379)

    fun saveMetaData(): (exchange: Exchange) -> Unit = { exchange ->
        val periodEnd = exchange.message.getHeader(END_DATE_HEADER_NAME, String::class.java)
        jedis.set(DataFlowConstants.GRAB_DATA_LATEST_DATE_KEY.field, periodEnd)
    }

    fun saveFundData(): (exchange: Exchange) -> Unit = { exchange ->
        val body = exchange.getIn().getBody(Pair::class.java) as Pair<List<String>, List<String>>

        val isin = exchange.message.getHeader(ISIN_HEADER_NAME, String::class.java)
        val header = body.first
        val data = body.second

        val keyValuePairs = FundHeaders.entries
            .filter { header.indexOf(it.field) != -1 }
            .associate { it.name.lowercase() to data[header.indexOf(it.field)] }

        jedis.hset("fund#$isin", keyValuePairs)
    }

    fun saveRateData(): (exchange: Exchange) -> Unit = { exchange ->
        val body = exchange.`in`.getBody(Pair::class.java) as Pair<List<String>, List<List<String>>>

        val isin = exchange.message.getHeader(ISIN_HEADER_NAME, String::class.java)
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
                        RedisHashKey.calculateScore(entry[header.indexOf(RateHeaders.DATE.field)]),
                        rateKey
                    )
                }
            pipeline.sync()
        }
    }
}