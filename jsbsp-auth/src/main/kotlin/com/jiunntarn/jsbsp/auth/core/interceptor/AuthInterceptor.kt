package com.jiunntarn.jsbsp.auth.core.interceptor

import com.jiunntarn.jsbsp.auth.core.payload.Response
import com.jiunntarn.jsbsp.auth.util.TokenUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import java.nio.charset.StandardCharsets

class AuthInterceptor : HandlerInterceptor {
    private fun output(response: HttpServletResponse, data: Response) {
        response.status = 200
        val outputStream = response.outputStream
        response.contentType = "application/json;charset=utf-8"
        outputStream.write(data.toJson().toByteArray(StandardCharsets.UTF_8))
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val authorization = request.getHeader("Authorization")

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            output(response, Response.unauthorized())
            return false
        }

        val token = authorization.substring(7)
        if (!TokenUtil.verify(token)) {
            output(response, Response.unauthorized())
            return false
        }

        val member = TokenUtil.getMember(token)
        if (member == null) {
            output(response, Response.unauthorized())
            return false
        }
        request.setAttribute("member", member)

        return true
    }
}