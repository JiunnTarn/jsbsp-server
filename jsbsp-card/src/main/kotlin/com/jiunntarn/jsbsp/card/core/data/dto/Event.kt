package com.jiunntarn.jsbsp.card.core.data.dto

import com.jiunntarn.jsbsp.card.core.data.enums.EventType
import com.jiunntarn.jsbsp.util.util.DateTimeUtil.toLocalDateTime
import com.jiunntarn.jsbsp.util.util.DateTimeUtil.toTimestamp
import java.time.LocalDateTime

data class Event(
    val time: LocalDateTime,
    val type: EventType,
    val data: String?
) {
    companion object {
        fun parseString(string: String): Event {
            try {
                val parts = string.split(":")
                val time = parts[0].toLocalDateTime()
                val type = enumValueOf<EventType>(parts[1])
                val data = parts[2]
                return Event(
                    time = time,
                    type = type,
                    data = data
                )
            } catch (_: Exception) {
                throw IllegalArgumentException("Invalid event string: $string")
            }
        }
    }

    override fun toString(): String {
        return "${time.toTimestamp()}:$type:$data"
    }
}