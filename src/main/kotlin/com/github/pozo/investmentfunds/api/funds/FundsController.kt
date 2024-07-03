package com.github.pozo.investmentfunds.api.funds

import jakarta.validation.constraints.Pattern
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FundsController @Autowired constructor(private val fundsApi: FundsAPI) {

    private val logger = LoggerFactory.getLogger(FundsController::class.java)

    @GetMapping("/funds")
    fun funds(): Iterable<Fund> {
        logger.info("GET /funds")
        return fundsApi.findAllFunds()
    }

    @GetMapping("/funds/{field}")
    fun fundsByField(
        @Pattern(
            regexp = "^(isin|name|manager|custodian|type|category|classification_according_to_investment_policy|currency_exposure|geographical_exposure|other_exposure|esg_classification|currency|status|start_date)$",
            message = "Invalid 'field' value"
        )
        @PathVariable field: String,
        @Pattern(
            regexp = "^[a-zA-Z0-9_]*$",
            message = "Invalid 'value' value"
        )
        @RequestParam value: String
    ): Iterable<Fund> {
        logger.info("GET /funds/$field, value='$value'")
        return fundsApi.findAllFunds(field, value)
    }

}