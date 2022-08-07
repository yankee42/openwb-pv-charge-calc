package com.github.yankee42.owbpvcalc

import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val CHARGE_LOG_LISTING_REGEX = "<a href=\"(\\d{6}\\.csv)\">".toRegex()
val DAILY_LOG_FILENAME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'.csv'")

class LogRepository(baseUrl: String) {
    private val baseUrl = if (baseUrl.matches("^\\w+://".toRegex())) baseUrl else "http://$baseUrl"

    fun listChargeLogs(): Sequence<String> = CHARGE_LOG_LISTING_REGEX
        .findAll(URL("$baseUrl/openWB/web/logging/data/ladelog/").readText()).map { it.groupValues[1] }

    fun readChargeLog(file: String): List<String> =
        URL("$baseUrl/openWB/web/logging/data/ladelog/$file").openStream().bufferedReader().readLines()
            .run { subList(0, size - 1) } // last line is always a blank line. We discard that.
            .asReversed()

    fun readDailyLog(date: LocalDate): List<String> =
        URL("$baseUrl/openWB/web/logging/data/daily/${date.format(DAILY_LOG_FILENAME_FORMATTER)}").openStream()
            .bufferedReader().readLines()
}
