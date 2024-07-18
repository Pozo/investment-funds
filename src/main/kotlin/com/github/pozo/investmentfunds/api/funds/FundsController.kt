package com.github.pozo.investmentfunds.api.funds

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
open class FundsController @Autowired constructor(private val fundsApi: FundsAPI) {

    private val logger = LoggerFactory.getLogger(FundsController::class.java)

    @GetMapping("/funds")
    fun funds(@Valid @RequestParam @FundFilters filters: Map<String, String>): Iterable<Fund> {
        logger.info("GET /funds, filters='$filters'")

        return if (filters.isEmpty()) {
            fundsApi.findAllFunds()
        } else {
            fundsApi.filterFunds(filters)
        }
    }

}