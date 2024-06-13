package com.github.pozo.investmentfunds.api.funds

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@WebMvcTest(FundsController::class)
internal class FundsControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var fundsService: FundsAPI

    @Test
    @Throws(Exception::class)
    fun shouldReturnAllFunds() {
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

    @Test
    @Throws(Exception::class)
    fun shouldReturnBadRequest() {
        every { fundsService.findAllFunds(any(), any()) }.returns(
            listOf(
                mapOf(
                    "e" to "5",
                    "f" to "6"
                ) as Fund,
                mapOf(
                    "g" to "7",
                    "h" to "8"
                ) as Fund
            )
        )

        mockMvc.perform(get("/funds/nonexisting?value=1"))
            .andExpect(status().isBadRequest())
    }

}