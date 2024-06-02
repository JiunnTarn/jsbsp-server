package com.jiunntarn.jsbsp.cx369.service

import com.jiunntarn.jsbsp.cx369.core.payload.CX369Response
import com.jiunntarn.jsbsp.cx369.core.data.user.IdCardInfo
import com.jiunntarn.jsbsp.cx369.util.requester.CX369Requester
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CX369UserService {
    private val cx369UserBaseUrl = "https://api.369cx.cn/v2/User"

    fun getIdCardInfo(token: String): CX369Response<IdCardInfo> {
        val url = "$cx369UserBaseUrl/GetIdCardInfo"

        return CX369Requester.get<CX369Response<IdCardInfo>>(url, null, token)
    }

}