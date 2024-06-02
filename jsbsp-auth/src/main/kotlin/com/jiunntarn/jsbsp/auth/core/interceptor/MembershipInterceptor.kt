package com.jiunntarn.jsbsp.auth.core.interceptor

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.core.data.enums.MemberStatus
import com.jiunntarn.jsbsp.auth.core.payload.Response
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import java.nio.charset.StandardCharsets

class MembershipInterceptor : HandlerInterceptor {
    private fun output(response: HttpServletResponse, data: Response) {
        response.status = 200
        val outputStream = response.outputStream
        response.contentType = "application/json;charset=utf-8"
        outputStream.write(data.toJson().toByteArray(StandardCharsets.UTF_8))
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val member: Member = request.getAttribute("member") as Member

        if (member.status == MemberStatus.BLOCKED) {
            output(response, Response.blocked())
            return false
        }

        return true
    }
}