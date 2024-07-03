package com.github.pozo.investmentfunds.domain

enum class FundHeaders(val field: String) {
    ISIN("ISIN kód"),
    NAME("Alap neve"),
    MANAGER("Alapkezelő"),
    CUSTODIAN("Letétkezelő"),
    TYPE("Alaptípus"),
    CATEGORY("Alapfajta"),
    CLASSIFICATION_ACCORDING_TO_INVESTMENT_POLICY("Bef.pol szerinti kategória"),
    CURRENCY_EXPOSURE("Devizális kitettség"),
    GEOGRAPHICAL_EXPOSURE("Földrajzi kitettség"),
    OTHER_EXPOSURE("Egyéb kitettség"),
    ESG_CLASSIFICATION("ESG besorolás"),
    CURRENCY("Devizanem"),
    STATUS("Státusz"),
    START_DATE("Indulás dátuma"),
}