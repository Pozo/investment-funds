package com.github.pozo.investmentfunds.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class InvestmentFundsApplication

fun main(args: Array<String>) {
    runApplication<InvestmentFundsApplication>(*args)
}
