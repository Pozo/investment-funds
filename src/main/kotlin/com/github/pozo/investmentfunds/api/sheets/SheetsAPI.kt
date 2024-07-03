package com.github.pozo.investmentfunds.api.sheets

interface SheetsAPI {

    fun getRatesByIsinAndFilter(isin: String, filter: SheetsController.RatesFilter): List<Map<String, String>>

}