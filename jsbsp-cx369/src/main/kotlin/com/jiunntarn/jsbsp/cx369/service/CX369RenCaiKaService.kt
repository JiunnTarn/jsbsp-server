package com.jiunntarn.jsbsp.cx369.service

import com.jiunntarn.jsbsp.cx369.core.payload.CX369Response
import com.jiunntarn.jsbsp.cx369.core.data.ren_cai_ka.RenCaiKaTicket
import com.jiunntarn.jsbsp.util.util.DateTimeUtil
import com.jiunntarn.jsbsp.cx369.util.requester.CX369Requester
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Service
class CX369RenCaiKaService {
    private val cx369RenCaiKaBaseUrl = "https://api.369cx.cn/v2/RenCaiKa"

    fun getTicket(token: String): CX369Response<RenCaiKaTicket> {
        val url = "$cx369RenCaiKaBaseUrl/GetTicket"

        return CX369Requester.get<CX369Response<RenCaiKaTicket>>(url, null, token)
    }

    fun setTicketEnable(token: String, activeDate: LocalDate): CX369Response<String> {
        val activeTimeStr = DateTimeUtil.toDateString(activeDate)
        val url = "$cx369RenCaiKaBaseUrl/SetTicketEnable/$activeTimeStr%2000:00:00"

        return CX369Requester.get<CX369Response<String>>(url, null, token)
    }
}