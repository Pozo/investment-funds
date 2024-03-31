package com.github.pozo.investmentfunds

enum class RateHeaders(val field: String) {
    DATE("Dátum"),
    NET_VALUE("Nettó eszközérték"),
    RATE("Árfolyam"),
    YIELD_PAID("Kifizetett hozamok"),
    DAILY_TURNOVER("Napi befjegy. forgalom"),
    DAILY_TURNOVER_PERCENTAGE("Napi befjegy. forgalom (%)"),
    REFERENCE_INDEX("Referenciaindex"),
    NET_VALUE_IN_HUF("Forintosított nettó eszközérték"),
    DAILY_TURNOVER_IN_HUF("Forintosított Napi bef.jegy forgalom"),
    YIELD_THREE_MONTHS("3 hónapos hozam"),
    YIELD_SIX_MONTHS("6 hónapos hozam"),
    YIELD_ONE_YEAR("1 éves hozam"),
    YIELD_THREE_YEARS("3 éves hozam"),
    YIELD_FIVE_YEARS("5 éves hozam"),
    YIELD_TEN_YEARS("10 éves hozam"),
    YIELD_FROM_BEGINNING("Hozam indulástól")
}
