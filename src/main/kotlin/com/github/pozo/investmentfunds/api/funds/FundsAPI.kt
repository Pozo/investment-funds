package com.github.pozo.investmentfunds.api.funds

typealias Fund = HashMap<String, String>

interface FundsAPI {

    fun findAllFunds(): List<Fund>

    fun filterFunds(parameters: Map<String, String>): List<Fund>

}