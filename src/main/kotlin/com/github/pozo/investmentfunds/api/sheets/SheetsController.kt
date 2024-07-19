package com.github.pozo.investmentfunds.api.sheets

import com.github.pozo.investmentfunds.api.funds.Fund
import com.github.pozo.investmentfunds.api.funds.FundsAPI
import com.github.pozo.investmentfunds.api.funds.ValidFundFilters
import com.github.pozo.investmentfunds.api.validation.ValidDate
import com.github.pozo.investmentfunds.domain.ISIN.ISIN_REGEX_PATTERN
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SheetsController @Autowired constructor(
    private val fundsApi: FundsAPI,
    private val sheetsAPI: SheetsAPI
) {

    private val logger = LoggerFactory.getLogger(SheetsController::class.java)

    data class RatesFilter(
        @field:Pattern(
            regexp = "^(date|net_value|rate|yield_paid|daily_turnover|daily_turnover_percentage|reference_index|net_value_in_huf|daily_turnover_in_huf|yield_three_months|yield_six_months|yield_one_year|yield_three_years|yield_five_years|yield_ten_years|yield_from_beginning)$",
            message = "Invalid attribute"
        )
        val attribute: String?,

        @field:ValidDate(dateFormat = "yyyy/MM/dd", mandatory = false)
        val startDate: String?,

        @field:ValidDate(dateFormat = "yyyy/MM/dd", mandatory = false)
        val endDate: String?
    )

    @PostMapping("/sheets/rates/{isin}")
    fun sheetsRates(
        @PathVariable @NotBlank @Pattern(regexp = ISIN_REGEX_PATTERN) isin: String,
        @Valid @RequestBody filter: RatesFilter
    ): Iterable<Map<String, String>> {
        logger.info("POST /sheets/rates/$isin, filter=$filter")
        return sheetsAPI.getRatesByIsinAndFilter(isin, filter)
    }

    @PostMapping("/sheets/funds")
    fun filterFunds(@Valid @RequestBody @ValidFundFilters filters: Map<String, String>): Iterable<Fund> {
        logger.info("POST /sheets/funds, filters='$filters'")

        return fundsApi.filterFunds(filters)
    }

}