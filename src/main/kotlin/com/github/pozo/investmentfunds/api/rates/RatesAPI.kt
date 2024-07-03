package com.github.pozo.investmentfunds.api.rates

typealias Rate = HashMap<String, String>

interface RatesAPI {

    fun findAllRatesByISIN(isin: String): List<Rate>

    fun findAllRatesByISINBetween(isin: String, filter: RatesController.RatesFilter): List<Rate>
}