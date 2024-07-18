package com.github.pozo.investmentfunds.api.funds

import com.github.pozo.investmentfunds.api.SecurityConfiguration
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@WebMvcTest(FundsController::class)
@Import(SecurityConfiguration::class) // https://stackoverflow.com/questions/45116833/springboot-webmvctest-security-issue
internal class FundsControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var fundsService: FundsAPI

    @Test
    @Throws(Exception::class)
    fun `should return all funds`() {
        every { fundsService.findAllFunds() }.returns(
            listOf(
                mapOf(
                    "a" to "1",
                    "b" to "2"
                ) as Fund,
                mapOf(
                    "c" to "3",
                    "d" to "4"
                ) as Fund
            )
        )

        mockMvc.perform(get("/funds"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """[
                      {
                        "a": "1",
                        "b": "2"
                      },
                      {
                        "c": "3",
                        "d": "4"
                      }
                    ]""".trimIndent()
                )
            )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "ISIN",
            "NAME",
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
            "currencyExposure",
            "Geographical_Exposure",
            "otherExposure",
            "esg",
            "curren?cy",
            "status-",
            "startdate"
        ]
    )
    fun `should return bad request when keys in funds filter are invalid`(attribute: String) {
        every { fundsService.filterFunds(any()) }.returns(
            listOf(
                mapOf(
                    "a" to "1",
                    "b" to "2"
                ) as Fund,
                mapOf(
                    "c" to "3",
                    "d" to "4"
                ) as Fund
            )
        )

        mockMvc.perform(get("/funds?$attribute=test"))
            .andExpect(status().isBadRequest())
    }

}