package com.github.pozo.investmentfunds.domain

enum class DataFlowConstants(val field: String) {
    START_YEAR("1992"),// there is no data before this date
    START_YEAR_DATE("1992.01.01"),

    DOMAIN_DATE_FORMAT("yyyy.MM.dd"),
    RATES_KEY_DATE_FORMAT("yyyy/MM/dd")
}