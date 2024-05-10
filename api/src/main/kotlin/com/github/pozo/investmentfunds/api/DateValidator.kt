package com.github.pozo.investmentfunds.api

import java.text.ParseException
import java.text.SimpleDateFormat


object DateValidator {

    fun isValidDateFormat(dateStr: String, dateFormat: String): Boolean {
        val simpleDateFormat = SimpleDateFormat(dateFormat)
        simpleDateFormat.isLenient = false // Set lenient to false to enforce strict date parsing

        try {
            simpleDateFormat.parse(dateStr) // Try parsing the date string
            return true // If parsing succeeds, return true
        } catch (e: ParseException) {
            return false // If parsing fails, return false
        }
    }
}