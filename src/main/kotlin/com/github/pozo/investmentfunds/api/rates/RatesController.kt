package com.github.pozo.investmentfunds.api.rates

import com.github.pozo.investmentfunds.api.ValidDate
import com.github.pozo.investmentfunds.domain.ISIN.ISIN_REGEX_PATTERN
import jakarta.validation.constraints.Pattern
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class RatesController @Autowired constructor(private val ratesApi: RatesAPI) {

    private val logger = LoggerFactory.getLogger(RatesController::class.java)

    data class RatesFilter(
        @field:ValidDate(dateFormat = "yyyy/MM/dd")
        val startDate: String,
        @field:ValidDate(dateFormat = "yyyy/MM/dd")
        val endDate: String
    )

    @GetMapping("/rates/{isin}")
    fun ratesByIsin(@PathVariable @Pattern(regexp = ISIN_REGEX_PATTERN) isin: String): Iterable<Rate> {
        logger.info("GET /rates/$isin")
        return ratesApi.findAllRatesByISIN(isin)
    }

    @PostMapping("/rates/{isin}")
    fun ratesByIsinAndFilter(
        @PathVariable @Pattern(regexp = ISIN_REGEX_PATTERN) isin: String,
        @RequestBody filter: RatesFilter
    ): Iterable<Rate> {
        logger.info("POST /rates/$isin, filter=$filter")
        return ratesApi.findAllRatesByISINBetween(isin, filter)
    }

}