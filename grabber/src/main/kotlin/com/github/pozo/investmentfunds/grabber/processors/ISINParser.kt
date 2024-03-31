package com.github.pozo.investmentfunds.grabber.processors

import com.github.pozo.investmentfunds.grabber.InvestmentFundsRoutes.Companion.ISIN_LIST_HEADER_NAME
import org.apache.camel.Exchange

object ISINParser {

    fun setISINListHeaderValue(): (exchange: Exchange) -> Unit = { exchange ->
        val message = exchange.getIn().body as String
        exchange.message.setHeader(ISIN_LIST_HEADER_NAME, message.replace('\n', ','))
    }

}