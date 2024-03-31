package com.github.pozo.investmentfunds

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object RedisHashKey {

    fun calculateScore(dateFormat: String, startYear: Int, message: String): Double {
        val formatter = DateTimeFormatter.ofPattern(dateFormat)
        val date = LocalDate.parse(message, formatter)

        val factor = date.year - startYear
        // Using 366 in order to avoid issues with leap years
        return ((factor * 366) + date.dayOfYear).toDouble()
    }
}