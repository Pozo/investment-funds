package com.github.pozo.investmentfunds.api.funds

import com.github.pozo.investmentfunds.domain.ISIN.ISIN_REGEX_PATTERN
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

open class FundFilterValidator : ConstraintValidator<ValidFundFilters, Map<String, String>> {

    override fun isValid(parameters: Map<String, String>, context: ConstraintValidatorContext): Boolean {
        for ((key, value) in parameters) {
            if (!VALID_KEYS.contains(key)) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate("The field name '$key' is invalid").addConstraintViolation()
                return false
            }

            val valueIsValid = when (key) {
                "isin" -> ISIN_REGEX.matches(value)
                "name" -> NAME_REGEX.matches(value)
                "manager" -> VALID_MANAGER.contains(value)
                "custodian" -> VALID_CUSTODIAN.contains(value)
                "type" -> VALID_TYPE.contains(value)
                "category" -> VALID_CATEGORY.contains(value)
                "classification_according_to_investment_policy" -> VALID_CLASSIFICATION_ACCORDING_TO_INVESTMENT_POLICY.contains(value)
                "currency_exposure" -> VALID_CURRENCY_EXPOSURE.contains(value)
                "geographical_exposure" -> VALID_GEOGRAPHICAL_EXPOSURE.contains(value)
                "other_exposure" -> VALID_OTHER_EXPOSURE.contains(value)
                "esg_classification" -> VALID_ESG_CLASSIFICATION.contains(value)
                "currency" -> VALID_CURRENCY.contains(value)
                "status" -> VALID_STATUS.contains(value)
                "start_date" -> DATE_REGEX.matches(value)
                else -> false
            }
            if (!valueIsValid) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate("The given value for '$key' field is invalid").addConstraintViolation()
                return false
            }
        }
        return true
    }

    companion object {
        private val NAME_REGEX = Regex("^[A-Za-zÁÉÍÓÖŐÚÜŰáéíóöőúüű0-9.()/\\-\"?&\\s]+\$")
        private val ISIN_REGEX = Regex(ISIN_REGEX_PATTERN)
        private val DATE_REGEX = Regex("\\d{4}/\\d{2}/\\d{2}")

        val VALID_KEYS = setOf(
            "isin",
            "name",
            "manager",
            "custodian",
            "type",
            "category",
            "classification_according_to_investment_policy",
            "currency_exposure",
            "geographical_exposure",
            "other_exposure",
            "esg_classification",
            "currency",
            "status",
            "start_date"
        )
        val VALID_GEOGRAPHICAL_EXPOSURE = setOf(
            "",
            "Egyéb feltörekvő",
            "Egyéb feltörekvő / Afrikai",
            "Egyéb feltörekvő / EMEA",
            "Egyéb feltörekvő / Latin-Amerika",
            "Egyéb feltörekvő / Távol-Kelet",
            "Egyéb feltörekvő / globális",
            "Egyéb feltörekvő / Ázsiai",
            "Fejlett piaci",
            "Fejlett piaci / Európa",
            "Fejlett piaci / Globális",
            "Fejlett piaci / OECD",
            "Fejlett piaci / Észak Amerika",
            "Feltörekvő Európa",
            "Feltörekvő Európa/ Oroszország",
            "Feltörekvő Európa/ Törökország",
            "Globális",
            "Hazai",
            "Közép-Kelet-Európa"
        )
        val VALID_MANAGER = setOf(
            "APELSO CAPITAL Befektetési Alapkezelő Zrt.",
            "Accorde Alapkezelő Zrt",
            "Allianz Alapkezelő Zrt.",
            "Amundi Alapkezelő Zrt.",
            "Axiom Alapkezelő Zrt.",
            "Biggeorge Alapkezelő Zrt.",
            "Equilor Alapkezelő Zrt.",
            "Erste Alapkezelő Zrt.",
            "Eurizon Asset Management Hungary Zrt.",
            "Európa Alapkezelő Zrt.",
            "Generali Alapkezelő Zrt.",
            "Gránit Alapkezelő Zrt.",
            "Hold Alapkezelő Zrt.",
            "KBC AM",
            "MARKETPROG Asset Management Befektetési Alapkezelő Zrt.",
            "MBH Alapkezelő Zrt.",
            "OTP Alapkezelő Zrt.",
            "OTP Ingatlan Befektetési Alapkezelő Zrt.",
            "Raiffeisen Befektetési Alapkezelő Zrt.",
            "SIGNAL IDUNA Fund Invest Alapkezelő Zrt.",
            "VIG Befektetési Alapkezelő Magyarország Zrt."
        )
        val VALID_CUSTODIAN = setOf(
            "CIB Bank Zrt.",
            "Citibank Europe plc Magyarországi Fióktelepe",
            "Erste Bank Hungary Zrt.",
            "K&H Bank Rt.",
            "MBH Bank Nyrt.",
            "MBH Befektetési Bank Zrt.",
            "OTP Bank Rt.",
            "Raiffeisen Bank Zrt.",
            "UniCredit Bank Hungary Zrt."
        )
        val VALID_CLASSIFICATION_ACCORDING_TO_INVESTMENT_POLICY = setOf(
            "Abszolút hozamú",
            "Ingatlan/Közvetett",
            "Ingatlan/Közvetlen",
            "Kötvény/Hosszú kötvény",
            "Kötvény/Rövid kötvény",
            "Kötvény/Szabad futamidejű",
            "Pénzpiaci/Egyéb pénzpiaci",
            "Pénzpiaci/Likviditási",
            "Részvény",
            "Származtatott",
            "Tőkevédett",
            "Vegyes/Dinamikus",
            "Vegyes/Kiegyensúlyozott",
            "Vegyes/Óvatos",
            "Árupiaci"
        )
        val VALID_OTHER_EXPOSURE = setOf(
            "",
            "Alapok alapja",
            "Arany",
            "BUX",
            "CPPI",
            "IPO papírok",
            "Ingatlanpiac",
            "Kötvényjellegű",
            "MSCI World Free",
            "Részvény-alapú",
            "Tőkeáttétel",
            "Árupiac",
            "Ökológiai"
        )
        val VALID_CURRENCY = setOf(
            "CZK",
            "EUR",
            "HUF",
            "PLN",
            "USD"
        )
        val VALID_TYPE = setOf(
            "Nyilvános nyíltvégű",
            "Nyilvános nyíltvégű intézményi",
            "Nyilvános zártvégű",
            "Zártkörű nyíltvégű"
        )
        val VALID_CATEGORY = setOf(
            "Alapok alapja",
            "Indexkövető befektetési alap",
            "Ingatlanalap",
            "Származtatott ügyletekbe fektető alap",
            "UCITS (európai) alap",
            "Értékpapíralap"
        )
        val VALID_CURRENCY_EXPOSURE = setOf(
            "",
            "CZK",
            "CZK / Egyéb",
            "EUR",
            "EUR, HUF",
            "EUR,USD",
            "Egyéb",
            "HKD / INR",
            "HUF",
            "HUF/EUR/USD",
            "HUF/PLN/CZK",
            "PLN",
            "USD",
            "USD, HKD"
        )
        val VALID_ESG_CLASSIFICATION = setOf(
            "",
            "0",
            "ESG-Impact",
            "ESG-minősített"
        )
        val VALID_STATUS = setOf(
            "Aktív"
        )
    }
}
