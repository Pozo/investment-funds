package com.github.pozo.investmentfunds.api.sheets

interface SheetsAPI {

    fun fundsData(isin: String, filter: SheetsController.FundsFilter): List<Map<String, String>>

}