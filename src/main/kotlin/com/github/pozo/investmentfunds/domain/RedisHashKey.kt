package com.github.pozo.investmentfunds.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object RedisHashKey {

    fun calculateScore(date: String): Double {
        return calculateScore(
            DataFlowConstants.RATES_KEY_DATE_FORMAT.field,
            DataFlowConstants.START_YEAR.field.toInt(),
            date
        )
    }

    private fun calculateScore(dateFormat: String, startYear: Int, date: String): Double {
        val formatter = DateTimeFormatter.ofPattern(dateFormat)
        val localDate = LocalDate.parse(date, formatter)

        val factor = localDate.year - startYear
        // Using 366 in order to avoid issues with leap years
        return ((factor * 366) + localDate.dayOfYear).toDouble()
    }
}