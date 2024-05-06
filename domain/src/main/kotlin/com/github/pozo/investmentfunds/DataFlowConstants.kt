package com.github.pozo.investmentfunds

enum class DataFlowConstants(val field: String) {
    START_YEAR("1992"),// there is no data before this date
    START_YEAR_DATE("1992.01.01"),

    GRAB_DATA_COMMAND_CHANNEL_NAME("grab-data"),
    GRAB_DATA_COMMAND_DATE_FORMAT("yyyy.MM.dd"),
    GRAB_DATA_COMMAND_SEPARATOR(","),

    GRAB_DATA_LATEST_DATE_KEY("meta#last-successful-grabbing-ending-date"),

}