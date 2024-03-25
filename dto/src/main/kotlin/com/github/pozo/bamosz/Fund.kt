package com.github.pozo.bamosz

data class Fund(
    // "ISIN k�d"
    val isin: String,
    // "Alap neve"
    val name: String,
    // "Alapkezel�"
    val manager: String,
    // "Let�tkezel�"
    val custodian: String,
    // "Alapt�pus"
    val capitalVariability: String,
    // "Alapfajta"
    val type: String,
    // "Bef.pol szerinti kateg�ria"
    val A: String,
    // "Deviz�lis kitetts�g"
    val B: String,
    // "F�ldrajzi kitetts�g"
    val C: String,
    // "Egy�b kitetts�g"
    val D: String,
    // "ESG besorol�s"
    val esg: String,
    // "Devizanem"
    val currency: String,
    // "St�tusz"
    val status: String,
    // "Indul�s d�tuma"
    val startDate: String,
)

enum class FundHeaders(val field: String) {
    ISIN("ISIN kód"),
    NAME("Alap neve"),
    MANAGER("Alapkezelő"),
    CUSTODIAN("Letétkezelő")
}