package com.github.pozo.investmentfunds.api.funds

import com.github.pozo.investmentfunds.api.funds.FundFilterValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [FundFilterValidator::class])
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidFundFilters(
    val message: String = "Invalid filter parameters",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
