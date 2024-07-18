package com.github.pozo.investmentfunds.api.funds

import com.github.pozo.investmentfunds.api.SecurityConfiguration
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_CATEGORY
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_CLASSIFICATION_ACCORDING_TO_INVESTMENT_POLICY
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_CURRENCY
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_CURRENCY_EXPOSURE
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_CUSTODIAN
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_ESG_CLASSIFICATION
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_GEOGRAPHICAL_EXPOSURE
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_MANAGER
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_OTHER_EXPOSURE
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_STATUS
import com.github.pozo.investmentfunds.api.funds.FundFilterValidator.Companion.VALID_TYPE
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.Stream


@WebMvcTest(FundsController::class)
@Import(SecurityConfiguration::class) // https://stackoverflow.com/questions/45116833/springboot-webmvctest-security-issue
internal class FundsFilterControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var fundsService: FundsAPI

    @ParameterizedTest
    @MethodSource("isinProvider")
    fun `should return 200 when valid isin passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("nameProvider")
    fun `should return 200 when valid name passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("managerProvider")
    fun `should return 200 when valid manager passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("custodianProvider")
    fun `should return 200 when valid custodian passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("typeProvider")
    fun `should return 200 when valid type passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("categoryProvider")
    fun `should return 200 when valid category passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("caipProvider")
    fun `should return 200 when valid caip passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("currencyExposureProvider")
    fun `should return 200 when valid currency_exposure passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("geographicalExposureProvider")
    fun `should return 200 when valid geographical_exposure passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("otherExposureProvider")
    fun `should return 200 when valid other_exposure passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("esgClassificationProvider")
    fun `should return 200 when valid esg_classification passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("currencyProvider")
    fun `should return 200 when valid currency passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    fun `should return 200 when valid status passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @ParameterizedTest
    @MethodSource("startDateProvider")
    fun `should return 200 when valid date passed`(attribute: Pair<String, Set<String>>) {
        every { fundsService.filterFunds(any()) }.returns(emptyList())
        mockMvc.perform(
            post("/funds")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""{ "${attribute.first}": "${attribute.second}" }""".trimIndent())
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    companion object {
        @JvmStatic
        fun isinProvider(): Stream<Pair<String, String>> =
            setOf("HU0000729702").map { value -> "isin" to value }.stream()

        @JvmStatic
        fun nameProvider(): Stream<Pair<String, String>> = setOf("asd").map { value -> "name" to value }.stream()
        @JvmStatic
        fun managerProvider(): Stream<Pair<String, String>> = VALID_MANAGER.map { value -> "manager" to value }.stream()
        @JvmStatic
        fun custodianProvider(): Stream<Pair<String, String>> =
            VALID_CUSTODIAN.map { value -> "custodian" to value }.stream()

        @JvmStatic
        fun typeProvider(): Stream<Pair<String, String>> = VALID_TYPE.map { value -> "type" to value }.stream()
        @JvmStatic
        fun categoryProvider(): Stream<Pair<String, String>> =
            VALID_CATEGORY.map { value -> "category" to value }.stream()

        @JvmStatic
        fun caipProvider(): Stream<Pair<String, String>> =
            VALID_CLASSIFICATION_ACCORDING_TO_INVESTMENT_POLICY.map { value -> "classification_according_to_investment_policy" to value }
                .stream()

        @JvmStatic
        fun currencyExposureProvider(): Stream<Pair<String, String>> =
            VALID_CURRENCY_EXPOSURE.map { value -> "currency_exposure" to value }.stream()

        @JvmStatic
        fun geographicalExposureProvider(): Stream<Pair<String, String>> =
            VALID_GEOGRAPHICAL_EXPOSURE.map { value -> "geographical_exposure" to value }.stream()

        @JvmStatic
        fun otherExposureProvider(): Stream<Pair<String, String>> =
            VALID_OTHER_EXPOSURE.map { value -> "other_exposure" to value }.stream()

        @JvmStatic
        fun esgClassificationProvider(): Stream<Pair<String, String>> =
            VALID_ESG_CLASSIFICATION.map { value -> "esg_classification" to value }.stream()

        @JvmStatic
        fun currencyProvider(): Stream<Pair<String, String>> =
            VALID_CURRENCY.map { value -> "currency" to value }.stream()

        @JvmStatic
        fun statusProvider(): Stream<Pair<String, String>> = VALID_STATUS.map { value -> "status" to value }.stream()
        @JvmStatic
        fun startDateProvider(): Stream<Pair<String, String>> =
            setOf("2022/12/23").map { value -> "start_date" to value }.stream()
    }

}