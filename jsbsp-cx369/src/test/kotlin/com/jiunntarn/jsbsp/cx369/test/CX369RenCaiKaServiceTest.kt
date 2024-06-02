package com.jiunntarn.jsbsp.cx369.test

import com.jiunntarn.jsbsp.cx369.service.CX369RenCaiKaService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest
class CX369RenCaiKaServiceTest {
    @Resource
    private lateinit var cx369RenCaiKaService: CX369RenCaiKaService

    @Test
    fun getTicket() {
        val token = ""

        logger.info { cx369RenCaiKaService.getTicket(token) }
    }
}