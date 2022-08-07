package com.github.yankee42.owbpvcalc

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Call with openWB address as argument")
        return
    }
    println("start,end,total,pv")
    PvChargeCalculator(LogRepository(args[0]))
        .analyze()
        .forEach { println("${it.start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)},${it.end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)},${it.chargedTotal},${it.chargedPv}") }
}

data class ChargeStats(
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val chargedTotal: Double = 0.0,
    val chargedPv: Double = 0.0
)

class PvChargeCalculator(val repository: LogRepository) {
    fun analyze(): Sequence<ChargeStats> {
        return repository
            .listChargeLogs()
            .flatMap { repository.readChargeLog(it) }
            .map { parseChargeLogLine(it) }
            .map { analyzeChargeLogLine(it) }
    }

    private fun analyzeChargeLogLine(logLine: ChargeLogLine): ChargeStats {
        val start = logLine.start
        val end = logLine.end
        val beforeStart = start.withMinute(start.minute / 5 * 5)
        val afterEnd = if (end.minute % 5 == 0) end else end.withMinute(end.minute / 5 * 5).plusMinutes(5)

        val energyPv = generateSequence(beforeStart.toLocalDate()) { it.plusDays(1) }
            .flatMap { date -> repository.readDailyLog(date).asSequence().map { parseDailyLogLine(it, date) } }
            .dropWhile { it.dateTime < beforeStart }
            .takeWhile { it.dateTime <= afterEnd }
            .windowed(2)
            .sumOf {
                val charged = it[1].totalCharged - it[0].totalCharged
                val imported = it[1].totalImport - it[0].totalImport
                val pvCharged = (charged - imported).coerceAtLeast(0.0) / 1000
                pvCharged
            }
        return ChargeStats(start, end, logLine.energy, energyPv)
    }
}
