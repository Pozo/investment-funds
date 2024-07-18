package com.github.pozo.investmentfunds.api.funds

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.util.regex.Pattern

open class FundFilterValidator : ConstraintValidator<FundFilters, Map<String, String>> {


    override fun isValid(parameters: Map<String, String>, context: ConstraintValidatorContext): Boolean {
        val regex = "^[\\p{L}\\p{N}_.\\s]*\$"
        val pattern: Pattern = Pattern.compile(regex)

        for ((key, value) in parameters) {
            if (!VALID_KEYS.contains(key) || !pattern.matcher(value).matches()) {
                return false
            }
        }
        return true
    }

    companion object {
        private val VALID_KEYS = setOf(
            "isin",
            "name",
            "manager",
            "custodian",
            "type",
            "category",
            "classification_according_to_investment_policy",
            "currency_exposure",
            "geographical_exposure",
            "other_exposure",
            "esg_classification",
            "currency",
            "status",
            "start_date"
        )
    }
}