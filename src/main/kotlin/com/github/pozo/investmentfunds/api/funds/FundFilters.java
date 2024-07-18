package com.github.pozo.investmentfunds.api.funds;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FundFilterValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FundFilters {
    String message() default "Invalid parameters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
