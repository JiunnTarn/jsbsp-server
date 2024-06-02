package com.jiunntarn.jsbsp.cx369.service

import com.jiunntarn.jsbsp.cx369.core.payload.CX369Response
import com.jiunntarn.jsbsp.cx369.core.data.auth.LoginByPasswordResponse
import com.jiunntarn.jsbsp.cx369.util.requester.CX369Requester
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.to

private val logger = KotlinLogging.logger {}

@Service
class CX369AuthService {
    private val cx369AuthBaseUrl = "https://api.369cx.cn/v2/Auth"

    fun loginByPassword(phoneNumber: String, password: String): CX369Response<LoginByPasswordResponse> {
        val url = "$cx369AuthBaseUrl/LoginByPassword"
        val body = mapOf("userName" to phoneNumber, "password" to password)

        return CX369Requester.post<CX369Response<LoginByPasswordResponse>>(url, body)
    }
}