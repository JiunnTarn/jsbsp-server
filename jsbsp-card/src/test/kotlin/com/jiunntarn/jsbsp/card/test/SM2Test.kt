package com.jiunntarn.jsbsp.card.test

import com.tcps.jnqrcodepay.sm.ConversionUtil
import com.tcps.jnqrcodepay.sm.SM2Util
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest(classes = [SM2Util::class])
class SM2Test {

    @Test
    fun sign() {
        val pubX = ""
        val pubY = ""
        val priD = ""

        val qrCodePart = ""

        val c = if (qrCodePart.substring(412, 414) == "00") 28800 else 0

        val stringBuffer = StringBuffer()
        stringBuffer.append("810169")
        stringBuffer.append(qrCodePart)

        val l = (System.currentTimeMillis() / 1000 - c).toString(16).padStart(8, '0')

        logger.info { "l: $l" }

        stringBuffer.append(l)

        logger.info { "f: $stringBuffer" }

        val str = try {
            SM2Util.getSM2Sign(pubX, pubY, priD, stringBuffer.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        stringBuffer.append("15")
        stringBuffer.append(str)

        val result = ConversionUtil.convertQrCode(stringBuffer.toString())

        logger.info { "result: $result" }
    }
}