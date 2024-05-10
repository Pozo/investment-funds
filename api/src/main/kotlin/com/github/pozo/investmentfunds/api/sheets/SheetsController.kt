package com.github.pozo.investmentfunds.api.sheets

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SheetsController @Autowired constructor(private val sheetsAPI: SheetsAPI) {

    data class FundsFilter(
        val attribute: String?,  // default "price"
        val startDate: String?, // default "today"
        val endDate: String?,
        //val interval: String
    )

    @PostMapping("/sheets/funds/{isin}")
    fun funds(@PathVariable isin: String, @RequestBody filter: FundsFilter): Iterable<Map<String, String>> {
        return sheetsAPI.fundsData(isin, filter)
    }

}