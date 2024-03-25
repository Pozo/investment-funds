package com.github.pozo.bamosz.grabber

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Collectors
import java.util.stream.IntStream


@Throws(Exception::class)
fun main() {
    val context = DefaultCamelContext()

    // Create an ExecutorService for parallel processing
    val executorService: ExecutorService = Executors.newFixedThreadPool(5)

//    context.addRoutes(object : RouteBuilder() {
//        override fun configure() {
//            from("file:///Users/zoltanpolgar/workspace/private/bamosz/grabber/camel-raw-files")
//                    .split().body().parallelProcessing().executorService(executorService)
//                    .to("direct:processValue")
//        }
//    })

    var sharedProperties: MutableList<Int> = mutableListOf()

    context.addRoutes(object : RouteBuilder() {
        override fun configure() {
            from("file:///Users/zoltanpolgar/workspace/private/bamosz/grabber/camel-raw-files?charset=ISO-8859-2")
                    .convertBodyTo(String::class.java, "UTF-8") // Convert the body to UTF-8
                    .split().tokenize("\n")
                    .filter(simple("\${exchangeProperty.CamelSplitIndex} > 0"))
                    .choice()
                    //.pipeline()
                    //  .choice()
                    .`when`(simple("\${exchangeProperty.CamelSplitIndex} == 1"))
                    .process { exchange -> // vertical chunks
                        // Step 1: Process input
                        val body = exchange.getIn().getBody(String::class.java)
                        println("+++++++++++++++++++ $body")
                        val values = body.split(",")
                        println("+++++++++++++++++++ ${values.size}")

                        // skipping first column
                        sharedProperties = IntStream.range(1, values.size)
                                .filter { i: Int -> values[i].trim().isNotEmpty() || i == values.size - 1 }
                                .boxed()
                                .collect(Collectors.toList())
                    }
                    //.otherwise().stop()
                    .endChoice()
                    .end()
//                    .process { exchange ->
//                        // Step 2: Process further
//                        println(sharedProperties)
//                        println(exchange.getProperty("CamelSplitIndex"))
//                        val body = exchange.getIn().getBody(String::class.java)
//                        val substring = body.substring(0, sharedProperties[2])
//                        println("Substring value: $substring")
//
//                        exchange.getIn().body = "$substring\n"
//                    }
                    .split().body(String::class.java) { body ->
                        val data = body.split(",")
                        sharedProperties.windowed(2) { window ->
                            data.subList(window.first(),window.last()).toList()
                        }.toList()
                    }
                    .process { exchange ->
                        // Step 2: Process further
                        val value = exchange.getIn().getBody(List::class.java)
                        println("Processing value: $value")
                    }
                    //.to("file:///Users/zoltanpolgar/workspace/private/bamosz/grabber/camel-data-files?fileExist=Append&charset=UTF-8&fileName=\${header.isin}") // Output processed data to a file
        }
    })
    context.start()
    Thread.sleep(10_0000)
    context.stop()
}
