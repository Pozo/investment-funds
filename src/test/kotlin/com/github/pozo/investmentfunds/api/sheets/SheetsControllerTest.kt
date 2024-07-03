package com.github.pozo.investmentfunds.api.sheets

import com.github.pozo.investmentfunds.api.rates.Rate
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SheetsController::class)
class SheetsControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var sheetsService: SheetsAPI

    private val exampleFundsResponse = listOf(
        mapOf(
            "net_value" to "1",
            "rate" to "2"
        ) as Rate,
        mapOf(
            "net_value" to "3",
            "rate" to "4"
        ) as Rate
    )

    @Throws(Exception::class)
    @ParameterizedTest
    @ValueSource(
        strings = [
            " ",
            "test",
            "nope",
            "--",
            "null",
            "OR 1=1",
            "HU000070291",
            "HU",
            "HU-0000702915",
            "HU702915",
            "00000070291"
        ]
    )
    fun `should return bad request when 'isin' path variable is incorrect`(isin: String) {
        every { sheetsService.getRatesByIsinAndFilter(any(), any()) }.returns(exampleFundsResponse)

        mockMvc.perform(
            post("/sheets/rates/$isin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{}""".trimIndent())
        ).andExpect(status().isBadRequest())
    }

    @Throws(Exception::class)
    @ParameterizedTest
    @ValueSource(
        strings = [
            " ",
            "test",
            "nope",
            "--",
            "",
            "null",
            "OR 1=1",
            "HU000070291",
            "HU",
            "HU-0000702915",
            "HU702915",
            "00000070291"
        ]
    )
    fun `should return bad request when 'attribute' field value is incorrect in post body`(attribute: String) {
        every { sheetsService.getRatesByIsinAndFilter(any(), any()) }.returns(exampleFundsResponse)

        mockMvc.perform(
            post("/sheets/rates/HU0000702915")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "attribute": "$attribute" }""".trimIndent())
        ).andExpect(status().isBadRequest())
    }

    @Throws(Exception::class)
    @ParameterizedTest
    @ValueSource(
        strings = [
            "date",
            "net_value",
            "rate",
            "yield_paid",
            "daily_turnover",
            "daily_turnover_percentage",
            "reference_index",
            "net_value_in_huf",
            "daily_turnover_in_huf",
            "yield_three_months",
            "yield_six_months",
            "yield_one_year",
            "yield_three_years",
            "yield_five_years",
            "yield_ten_years",
            "yield_from_beginning"
        ]
    )
    fun `should return ok when 'attribute' field value is correct`(attribute: String) {
        every { sheetsService.getRatesByIsinAndFilter(any(), any()) }.returns(exampleFundsResponse)

        mockMvc.perform(
            post("/sheets/rates/HU0000702915")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "attribute": "$attribute" }""".trimIndent())
        ).andExpect(status().isOk())
    }

    @Throws(Exception::class)
    @ParameterizedTest
    @ValueSource(
        strings = [
            " ",
            "test",
            "nope",
            "--",
            "",
            "null",
            "OR 1=1",
            "HU000070291",
            "HU",
            "HU-0000702915",
            "HU702915",
            "00000070291",
            "2020/20/20",
        ]
    )
    fun `should return bad request when 'startDate' field value is incorrect in post body`(startDate: String) {
        every { sheetsService.getRatesByIsinAndFilter(any(), any()) }.returns(exampleFundsResponse)

        mockMvc.perform(
            post("/sheets/rates/HU0000702915")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "startDate": "$startDate" }""".trimIndent())
        ).andExpect(status().isBadRequest())
    }


    @Throws(Exception::class)
    @ParameterizedTest
    @ValueSource(
        strings = [
            " ",
            "test",
            "nope",
            "--",
            "",
            "null",
            "OR 1=1",
            "HU000070291",
            "HU",
            "HU-0000702915",
            "HU702915",
            "00000070291",
            "2020/20/20"
        ]
    )
    fun `should return bad request when 'endDate' field value is incorrect in post body`(endDate: String) {
        every { sheetsService.getRatesByIsinAndFilter(any(), any()) }.returns(exampleFundsResponse)

        mockMvc.perform(
            post("/sheets/rates/HU0000702915")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "endDate": "$endDate" }""".trimIndent())
        ).andExpect(status().isBadRequest())
    }

    @Throws(Exception::class)
    @ParameterizedTest
    @ValueSource(
        strings = [
            "2020/10/10",
            "2020/12/31",
            "2020/01/01",
            "2020/1/1",
            "2020/2/28",
        ]
    )
    fun `should return ok when 'startDate' field value is correct`(startDate: String) {
        every { sheetsService.getRatesByIsinAndFilter(any(), any()) }.returns(exampleFundsResponse)

        mockMvc.perform(
            post("/sheets/rates/HU0000702915")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "startDate": "$startDate" }""".trimIndent())
        ).andExpect(status().isOk())
    }

    @Throws(Exception::class)
    @ParameterizedTest
    @ValueSource(
        strings = [
            "2020/10/10",
            "2020/12/31",
            "2020/01/01",
            "2020/1/1",
            "2020/2/28",
        ]
    )
    fun `should return ok when 'endDate' field value is correct`(endDate: String) {
        every { sheetsService.getRatesByIsinAndFilter(any(), any()) }.returns(exampleFundsResponse)

        mockMvc.perform(
            post("/sheets/rates/HU0000702915")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "endDate": "$endDate" }""".trimIndent())
        ).andExpect(status().isOk())
    }
}