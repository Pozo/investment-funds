package com.github.pozo.investmentfunds.api.rates

import com.github.pozo.investmentfunds.ISIN.ISIN_REGEX_PATTERN
import com.github.pozo.investmentfunds.api.ValidDate
import jakarta.validation.constraints.Pattern
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class RatesController @Autowired constructor(private val ratesApi: RatesAPI) {

    data class RatesFilter(
        @field:ValidDate(dateFormat = "yyyy/MM/dd")
        val startDate: String,
        @field:ValidDate(dateFormat = "yyyy/MM/dd")
        val endDate: String
    )

    @GetMapping("/rates/{isin}")
    fun ratesByIsin(@PathVariable @Pattern(regexp = ISIN_REGEX_PATTERN) isin: String): Iterable<Rate> {
        return ratesApi.findAllRatesByISIN(isin)
    }

    @PostMapping("/rates/{isin}")
    fun ratesByIsinAndFilter(
        @PathVariable @Pattern(regexp = ISIN_REGEX_PATTERN) isin: String,
        @RequestBody filter: RatesFilter
    ): Iterable<Rate> {
        return ratesApi.findAllRatesByISINBetween(isin, filter)
    }

}