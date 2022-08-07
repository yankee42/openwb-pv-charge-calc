package com.github.yankee42.owbpvcalc

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

// "$(date +%H%M),$bezug,$einspeisung,$pv,$ll1,$ll2,$ll3,$llg,$speicheri,$speichere,$verbraucher1,$verbrauchere1,$verbraucher2,$verbrauchere2,$verbraucher3,$ll4,$ll5,$ll6,$ll7,$ll8,$speichersoc,$soc,$soc1,$temp1,$temp2,$temp3,$d1,$d2,$d3,$d4,$d5,$d6,$d7,$d8,$d9,$d10,$temp4,$temp5,$temp6
class DailyLogEntry(val dateTime: ZonedDateTime, val totalImport: Double, val totalCharged: Double)

fun parseDailyLogLine(line: String, date: LocalDate): DailyLogEntry {
    val fields = line.split(',', limit = 21)
    return DailyLogEntry(
        date.atTime(fields[0].substring(0, 2).toInt(), fields[0].substring(2, 4).toInt())
            .atZone(ZoneId.of("Europe/Berlin")),
        fields[1].toDouble(),
        ((4..6).asSequence() + (15..19).asSequence()).sumOf { fields[it].toDouble() }
    )
}
