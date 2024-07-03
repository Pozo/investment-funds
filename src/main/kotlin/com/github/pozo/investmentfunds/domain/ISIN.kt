package com.github.pozo.investmentfunds.domain

object ISIN {

    const val ISIN_REGEX_PATTERN: String = "\\b[a-zA-Z]{2}\\s*[0-9a-zA-Z]{9}[0-9](?![0-9a-zA-Z-])"
}