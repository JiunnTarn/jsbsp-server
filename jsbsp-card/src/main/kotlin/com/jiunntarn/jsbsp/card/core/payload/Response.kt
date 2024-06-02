package com.jiunntarn.jsbsp.card.core.payload

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import lombok.Setter

@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response(var code: Int, var data: Any? = null, var message: String? = null, var token: String? = null) {
    companion object {
        fun ok(data: Any? = null, message: String? = null, token: String? = null): Response {
            return Response(code = 0, data = data, message = message, token = token)
        }

        fun unauthorized(): Response {
            return Response(code = -10, message = "Token 校验失败，检查登录状态")
        }

        fun blocked(): Response {
            return Response(code = -20, message = "此 JSBSP 账户已停用")
        }

        fun missingParam(param: String? = null): Response {
            return Response(code = -1, message = "输入信息不全".plus(param?.let { "：${it}" }))
        }

        fun invalidParam(param: String? = null): Response {
            return Response(code = -2, message = "参数非法".plus(param?.let { "：${it}" }))
        }

        fun unknownError(message: String? = null): Response {
            return Response(code = -30, message = message)
        }
    }

    fun toJson() = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().toJson(this)
}