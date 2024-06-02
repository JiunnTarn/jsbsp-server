package com.jiunntarn.jsbsp.auth.service

import com.jiunntarn.jsbsp.auth.core.payload.Response
import com.jiunntarn.jsbsp.auth.util.TokenUtil
import com.jiunntarn.jsbsp.cx369.service.CX369UserService
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class AuthService {
    @Resource
    private lateinit var cx369UserService: CX369UserService

    @Resource
    private lateinit var memberService: MemberService

    fun login(cx369Token: String): Response {
        val idCardInfoResponse = cx369UserService.getIdCardInfo(cx369Token)
        if (!idCardInfoResponse.isSuccess) {
            return Response(code = 1, message = "Invalid cx369 token")
        }
        val idCardInfo = idCardInfoResponse.result!!
        val memberId = idCardInfo.userId
        val member = memberService.getById(memberId)
        if (member == null) {
            return Response(code = 2, message = "Member not found")
        }
        memberService.updateCX369Token(member, cx369Token)

        val token = TokenUtil.sign(memberId.toString(), null)
        return Response.ok(message = "Success", data = member, token = token)
    }

}