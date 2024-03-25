package com.github.pozo.bamosz

data class Rate(
    //"Dátum"
    val date: String,
    //"Nett� eszk�z�rt�k"
    val netValue: String,
    //"�rfolyam"
    val rate: String,
    //"Kifizetett hozamok"
    val yieldPaid: String,
    //"Napi befjegy. forgalom"
    val dailyTurnover: String,
    //"Napi befjegy. forgalom (%)"
    val dailyTurnoverPercentage: String,
    //"Referenciaindex"
    val referenceIndex: String,
    //"Forintos�tott nett� eszk�z�rt�k"
    val netValueInHuf: String,
    //"Forintos�tott Napi bef.jegy forgalom"
    val dailyTurnoverInHuf: String,
    //"3 h�napos hozam"
    val yieldThreeMonths: String,
    //"6 h�napos hozam"
    val yieldSixMonths: String,
    //"1 �ves hozam"
    val yieldOneYear: String,
    //"3 �ves hozam"
    val yieldThreeYears: String,
    //"5 �ves hozam"
    val yieldFiveYears: String,
    //"10 �ves hozam"
    val yieldTenYears: String,
    //"Hozam indul�st�l"
    val yieldFromBeginning: String
)

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



