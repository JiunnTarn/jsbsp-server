package com.jiunntarn.jsbsp.cx369.test

import com.jiunntarn.jsbsp.cx369.service.CX369BusQrCardService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}


@SpringBootTest
class CX369BusQrCardServiceTest {
    @Resource
    private lateinit var cx369BusQrCardService: CX369BusQrCardService

    @Test
    fun getCardListNew() {
        val token = ""

        logger.info { cx369BusQrCardService.getCardListNew(token).result?.first { it.isGreenCard }?.hasRenCaiKaTicket }
    }

    @Test
    fun getCardAuth() {
        val token = ""
        val cardId = 123456L

        logger.info { cx369BusQrCardService.getQrCodeData(token, cardId) }
    }
}