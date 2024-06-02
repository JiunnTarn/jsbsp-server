package com.jiunntarn.jsbsp.cx369.test

import com.jiunntarn.jsbsp.cx369.service.CX369UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest
class CX369UserServiceTest {
    @Resource
    private lateinit var cx369UserService: CX369UserService

    @Test
    fun getIdCardInfo() {
        val token = ""

        logger.info { cx369UserService.getIdCardInfo(token).isSuccess }
    }
}