package com.jiunntarn.jsbsp.util.util

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
object DateTimeUtil {
    fun toDateString(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    fun LocalDateTime.toTimestamp(): Long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun Long.toLocalDateTime(): LocalDateTime {
        return if (this.toString().length < 13) {
            LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this * 1000L), ZoneId.systemDefault())
        } else {
            LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this), ZoneId.systemDefault())
        }
    }

    fun String.toLocalDateTime(): LocalDateTime {
        return if (this.length < 13) {
            LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli("${this}000".toLong()), ZoneId.systemDefault())
        } else {
            LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this.toLong()), ZoneId.systemDefault())
        }
    }
}