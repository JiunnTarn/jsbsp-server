package com.jiunntarn.jsbsp.util.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging


object JsonUtil {
    val logger = KotlinLogging.logger {}

    inline fun <reified T> fromJson(
        json: String?,
        fieldNamingPolicy: FieldNamingPolicy = FieldNamingPolicy.IDENTITY
    ): T {
        try {
            val type = object : TypeToken<T>() {}.type
            val gson = GsonBuilder().setFieldNamingPolicy(fieldNamingPolicy).create()
            return gson.fromJson(json, type)
        } catch (e: Exception) {
            logger.error { "Failed to parse JSON: ${e.message}" }
            throw e
        }
    }
}