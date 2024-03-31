package com.github.pozo.investmentfunds.api.funds

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FundsController @Autowired constructor(private val fundsApi: FundsAPI) {

    @GetMapping("/funds")
    fun funds(): Iterable<Fund> {
        return fundsApi.findAllFunds()
    }

    @GetMapping("/funds/{field}")
    fun funds(@PathVariable field: String, @RequestParam value: String): Iterable<Fund> {
        return fundsApi.findAllFunds(field, value)
    }

}