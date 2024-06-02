package com.jiunntarn.jsbsp.util.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.jiunntarn.jsbsp.util.core.data.CX369TokenPayload
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.java
import kotlin.text.split

@Component
object CX369TokenUtil {
    fun getExpireTime(cx369Token: String): LocalDateTime {
        val payload = getPayload(cx369Token)
        val instant = Instant.ofEpochMilli(payload.exp * 1000L)

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    fun getUserId(cx369Token: String): String {
        val payload = getPayload(cx369Token)

        return payload.nameid
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getPayload(cx369Token: String): CX369TokenPayload {
        val parts = cx369Token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format")
        }
        val decodedPayload = String(Base64.decode(parts[1]), Charsets.UTF_8)
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
        return gson.fromJson(decodedPayload, CX369TokenPayload::class.java)
    }
}