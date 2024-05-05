//package com.github.pozo.investmentfunds.grabber
//
//import java.util.regex.Matcher
//import java.util.regex.Pattern
//
//fun main() {
//    val regexPattern = "[A-Z]{2}[A-Z0-9]{9}" // Matches two letters followed by 9 alphanumeric characters
//    val pattern = Pattern.compile(regexPattern)
//    val matcher: Matcher = pattern.matcher("href=\"/alapoldal?isin=HU0000716402\" target=\"_blank\">Accorde Abacus Alap </a> </td> </tr> <tr")
//
//    while (matcher.find()) {
//        println(matcher.group())
//    }
//}