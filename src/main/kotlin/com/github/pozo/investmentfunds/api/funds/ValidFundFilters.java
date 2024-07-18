package com.github.pozo.investmentfunds.api.funds;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FundFilterValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFundFilters {
    String message() default "Invalid filter parameters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
