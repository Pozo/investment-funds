package com.github.pozo.investmentfunds.api.funds

typealias Fund = HashMap<String, String>

interface FundsAPI {

    fun findAllFunds(): List<Fund>

    fun findAllFunds(field: String, value: String): List<Fund>

}