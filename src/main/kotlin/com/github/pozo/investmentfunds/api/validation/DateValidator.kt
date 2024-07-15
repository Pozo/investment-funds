package com.github.pozo.investmentfunds.api.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.text.ParseException
import java.text.SimpleDateFormat


class DateValidator : ConstraintValidator<ValidDate, String> {

    private var mandatory: Boolean = false

    private lateinit var dateFormat: String

    override fun initialize(constraintAnnotation: ValidDate) {
        this.mandatory = constraintAnnotation.mandatory
        this.dateFormat = constraintAnnotation.dateFormat
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return !mandatory

        }
        val simpleDateFormat = SimpleDateFormat(dateFormat)
        simpleDateFormat.isLenient = false // Set lenient to false to enforce strict date parsing

        try {
            simpleDateFormat.parse(value)
            return true
        } catch (e: ParseException) {
            return false
        }
    }
}