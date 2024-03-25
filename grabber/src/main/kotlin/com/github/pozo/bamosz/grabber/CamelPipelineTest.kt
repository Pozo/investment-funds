package com.github.pozo.bamosz.grabber

import com.github.pozo.bamosz.FundHeaders
import com.github.pozo.bamosz.RateHeaders
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.dataformat.csv.CsvDataFormat
import org.apache.camel.impl.DefaultCamelContext
import redis.clients.jedis.JedisPooled
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Collectors
import java.util.stream.IntStream

data class DataRow(val headerColumn: String, val dataColumns: List<String>)

@Throws(Exception::class)
fun main() {
    val context = DefaultCamelContext()
    val jedis = JedisPooled("localhost", 6379)
    // Create an ExecutorService for parallel processing
    val executorService: ExecutorService = Executors.newFixedThreadPool(20)
    var sharedProperties: MutableList<Int> = mutableListOf()

    context.addRoutes(object : RouteBuilder() {
        override fun configure() {
            from("file:///Users/zoltanpolgar/workspace/private/bamosz/grabber/camel-raw-files?charset=ISO-8859-2")
                .convertBodyTo(String::class.java, "UTF-8") // Convert the body to UTF-8
                .unmarshal(csv())
                .process { exchange -> // vertical chunks
                    // Step 1: Process input
                    val body: List<List<String>> = exchange.getIn().getBody(List::class.java) as List<List<String>>
                    val firstLine = body[1]
                    val entries = mutableMapOf<Pair<Int, Int>, MutableList<DataRow>>()
                    sharedProperties = IntStream.range(1, firstLine.size)
                        .filter { i: Int -> firstLine[i].trim().isNotEmpty() || i == firstLine.size }
                        .boxed()
                        .collect(Collectors.toList())

                    for (list in body.drop(1)) {
                        sharedProperties.windowed(2, 1, true) { window ->
                            val key: Pair<Int, Int> = if (window.size == 1) {
                                window.first() to firstLine.size
                            } else {
                                window.first() to window.last()
                            }
                            entries.getOrElse(key) { mutableListOf<DataRow>().also { entries[key] = it } }
                                .add(DataRow(list.first(), list.subList(key.first, key.second).toList()))
                        }.toList()
                    }
                    exchange.getIn().body = entries.values

                }.split().body().parallelProcessing().executorService(executorService)
                .process { exchange -> // vertical chunks)
                    val csvRows = exchange.getIn().body as List<DataRow>

                    val fundHeaders = mutableListOf<String>()
                    val fundData = mutableListOf<String>()

                    val rateHeaders = mutableListOf<String>()
                    val rateData = mutableListOf<List<String>>()

                    var rateDataSection = false
                    for (row in csvRows) {
                        if (row.headerColumn == "Dátum") {
                            rateDataSection = true
                            rateHeaders.add(row.headerColumn)
                            rateHeaders.addAll(row.dataColumns)
                            continue
                        }
                        if (rateDataSection) {
                            val element = mutableListOf(row.headerColumn)
                            element.addAll(row.dataColumns)
                            rateData.add(element)
                        } else {
                            fundHeaders.add(row.headerColumn)
                            fundData.add(row.dataColumns.first())
                        }
                    }
                    val isin = fundData[fundHeaders.indexOf("ISIN kód")]
                    val sanitizedIsin = Regex("\\HU[0-9]{9,10}\\b").find(isin)?.value ?: isin

                    getContext().createProducerTemplate()
                        .sendBodyAndHeader("direct:meta", Pair(fundHeaders, fundData), "isin", sanitizedIsin)
                    getContext().createProducerTemplate()
                        .sendBodyAndHeader("direct:data", Pair(rateHeaders, rateData), "isin", sanitizedIsin)
                }

            from("direct:meta").doTry()
                .log(LoggingLevel.INFO, "[fund-processor] Processing fund data for '\${header.isin}'")
                .process { exchange ->
                    val body = exchange.getIn().getBody(Pair::class.java) as Pair<List<String>, List<String>>

                    val isin = exchange.message.getHeader("isin", String::class.java)
                    val header = body.first
                    val data = body.second

                    val keyValuePairs = FundHeaders.entries
                        .filter { header.indexOf(it.field) != -1 }
                        .associate { it.name.lowercase() to data[header.indexOf(it.field)] }

                    jedis.hset("fund#$isin", keyValuePairs)
                }.doCatch(Exception::class.java)
                .log(
                    LoggingLevel.ERROR,
                    "[fund-processor] An error occurred during the processing : \${exception.message}"
                )
                .transform().simple("\${exception.message}")
                .end()

            from("direct:data").doTry()
                .log(LoggingLevel.INFO, "[rate-processor] Processing rate data for '\${header.isin}'")
                .process { exchange ->
                    val body = exchange.`in`.getBody(Pair::class.java) as Pair<List<String>, List<List<String>>>

                    val isin = exchange.message.getHeader("isin", String::class.java)
                    val header = body.first
                    val data = body.second

                    for (entry in data) {
                        val keyValuePairs = RateHeaders.entries
                            .filter { header.indexOf(it.field) != -1 }
                            .associate { it.name.lowercase() to entry[header.indexOf(it.field)] }

                        jedis.hset("rate:$isin#${entry[header.indexOf(RateHeaders.DATE.field)]}", keyValuePairs)
                    }
                }.doCatch(Exception::class.java)
                .log(
                    LoggingLevel.ERROR,
                    "[rate-processor] An error occurred during the processing : \${exception.message}"
                )
                .transform().simple("\${exception.message}")
                .end()

        }

        private fun csv(): CsvDataFormat {
            return CsvDataFormat().apply {
                setDelimiter(',')
                setQuote('"')
                setEscape('\\')
            }
        }
    })
    context.start()
    Thread.sleep(10_0000)
    context.stop()
}
