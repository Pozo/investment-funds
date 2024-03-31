package com.github.pozo.investmentfunds.api.rates

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class RatesController @Autowired constructor(private val ratesApi: RatesAPI) {

    @GetMapping("/rates/{isin}")
    fun ratesByIsin(@PathVariable isin: String): Iterable<Rate> {
        return ratesApi.findAllRatesByISIN(isin)
    }

    @PostMapping("/rates/{isin}")
    fun ratesByFilter(@PathVariable isin: String, @RequestBody filter: RatesFilter): Iterable<Rate> {
        return ratesApi.findAllRatesByISINBetween(isin, filter)
    }

    data class RatesFilter(val from: String, val to: String)
}