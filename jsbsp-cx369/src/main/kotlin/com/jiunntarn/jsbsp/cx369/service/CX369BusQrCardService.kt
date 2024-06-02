package com.jiunntarn.jsbsp.cx369.service

import com.jiunntarn.jsbsp.cx369.core.data.bus_qr_card.BusQrCard
import com.jiunntarn.jsbsp.cx369.core.data.bus_qr_card.CardAuth
import com.jiunntarn.jsbsp.cx369.core.payload.CX369Response
import com.jiunntarn.jsbsp.cx369.util.requester.CX369Requester
import com.tcps.jnqrcodepay.sm.ConversionUtil
import com.tcps.jnqrcodepay.sm.SM2Util
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service


private val logger = KotlinLogging.logger {}

@Service
class CX369BusQrCardService {
    private val cx369BusQrCardBaseUrl = "https://api.369cx.cn/v2/BusQrCard"

    fun getCardListNew(token: String): CX369Response<ArrayList<BusQrCard>> {
        val url = "$cx369BusQrCardBaseUrl/GetCardListNew"

        return CX369Requester.get<CX369Response<ArrayList<BusQrCard>>>(url, null, token)
    }


    fun getQrCodeData(token: String, cardId: Long): String {
        val pubX = "DD79E1FCC657FD1D5C4EE619A4E0E78D82BBD28391E1730969F10D0517E028E7"
        val pubY = "8170225BC95F528BAF34CC078C25FF3DCF85E8AF78BAE707C68AB4563A7B1EC0"
        val priD = "4F507EF8F75510B42CA0FCA8E1225F5DEF560F60312DBCB0ED5157B6BCA8D01A"

        val url = "$cx369BusQrCardBaseUrl/GetCardAuth/$cardId/$pubX/$pubY"
        val getCardAuthResponse = CX369Requester.get<CX369Response<CardAuth>>(url, null, token)

        if (!getCardAuthResponse.isSuccess) {
            logger.error { "Failed to get card $cardId auth: ${getCardAuthResponse.message}" }
            throw Exception("Failed to get card auth: ${getCardAuthResponse.message}")
        }

        val cardAuth = getCardAuthResponse.result!!
        val qrCodePart = cardAuth.authData

        val c = if (qrCodePart.substring(412, 414) == "00") 28800 else 0
        val l = (System.currentTimeMillis() / 1000 - c).toString(16).padStart(8, '0')

        val stringBuffer = StringBuffer()
        stringBuffer.append("810169")
        stringBuffer.append(qrCodePart)
        stringBuffer.append(l)
        val str = SM2Util.getSM2Sign(pubX, pubY, priD, stringBuffer.toString())

        stringBuffer.append("15")
        stringBuffer.append(str)

        return ConversionUtil.convertQrCode(stringBuffer.toString())
    }

}