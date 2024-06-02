package com.jiunntarn.jsbsp.cx369.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.jiunntarn.jsbsp.util.util.JsonUtil.fromJson
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class OkHttpUtil(
    val preBuildRequest: (Request.Builder) -> Request.Builder,
    private val interceptor: Interceptor? = null,
    val fieldNamingPolicy: FieldNamingPolicy = FieldNamingPolicy.IDENTITY
) {
    val client = OkHttpClient.Builder().apply { interceptor?.let { addInterceptor(it) } }.build()

    val logger = KotlinLogging.logger {}

    val requestBuilder get() = preBuildRequest(Request.Builder())

    inline fun <reified T> get(url: String, params: Map<String, String>? = null, headers: Map<String, String>? = null): T {
        val finalUrl = StringBuilder(url).apply { params?.let { append("?"); it.forEach { (k, v) -> append("$k=$v&") } } }.removeSuffix("&").toString()
        val request = requestBuilder.url(finalUrl).apply { headers?.forEach { addHeader(it.key, it.value) } }.get().build()
        return call(request)
    }

    inline fun <reified T> postJson(url: String, postBody: Map<String, String>, headers: Map<String, String>? = null): T {
        val jsonBody = Gson().toJson(postBody).toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = requestBuilder.url(url).apply { headers?.forEach { addHeader(it.key, it.value) } }.post(jsonBody).build()
        return call(request)
    }

    inline fun <reified T> postForm(url: String, postBody: Map<String, String>, headers: Map<String, String>? = null): T {
        val formBody = FormBody.Builder().apply { postBody.forEach { (k, v) -> add(k, v) } }.build()
        val request = requestBuilder.url(url).apply { headers?.forEach { addHeader(it.key, it.value) } }.post(formBody).build()
        return call(request)
    }

    inline fun <reified T> call(request: Request): T {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.error { "Request to ${request.url} failed: ${response.code} ${response.body?.string()}" }
                throw RuntimeException("Error occurred in request to ${request.url}: ${response.code}")
            }
            return fromJson(response.body?.string(), fieldNamingPolicy)
        }
    }
}
