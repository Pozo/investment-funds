package com.github.pozo.investmentfunds.api.funds

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class FundsController @Autowired constructor(private val fundsApi: FundsAPI) {

    private val logger = LoggerFactory.getLogger(FundsController::class.java)

    @GetMapping("/funds")
    fun funds(): Iterable<Fund> {
        logger.info("GET /funds")

        return fundsApi.findAllFunds()
    }

    @PostMapping("/funds")
    fun filterFunds(@Valid @RequestBody @ValidFundFilters filters: Map<String, String>): Iterable<Fund> {
        logger.info("POST /funds, filters='$filters'")

        return fundsApi.filterFunds(filters)
    }

}