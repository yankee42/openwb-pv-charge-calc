package com.github.yankee42.owbpvcalc

import java.time.ZoneId
import java.time.ZonedDateTime


// $start,$jetzt,$gelr,$bishergeladen,$ladegeschw,$ladedauertext,$chargePointNumber,$lademoduslogvalue,$rfid,$kosten
val chargeLogRegex =
    "^(\\d{2})\\.(\\d{2})\\.(\\d{2})-(\\d{2}):(\\d{2}),(\\d{2})\\.(\\d{2})\\.(\\d{2})-(\\d{2}):(\\d{2}),\\d+,(\\d+(?:\\.\\d+)?),".toRegex()

class ChargeLogLine(val start: ZonedDateTime, val end: ZonedDateTime, val energy: Double)

fun parseChargeLogLine(line: String): ChargeLogLine {
    return (chargeLogRegex.find(line) ?: throw RuntimeException("line <$line> is not a valid charge log line"))
        .groupValues
        .let {
            ChargeLogLine(parseDateTime(it.subList(1, 6)), parseDateTime(it.subList(6, 11)), it[11].toDouble())
        }
}

private fun parseDateTime(list: List<String>): ZonedDateTime {
    val (day, month, year, hour, minute) = list.map { it.toInt() }
    return ZonedDateTime.of(2000 + year, month, day, hour, minute, 0, 0, ZoneId.of("Europe/Berlin"))
}
