package com.jiunntarn.jsbsp.cx369.test

import com.jiunntarn.jsbsp.cx369.service.CX369AuthService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}


@SpringBootTest
class CX369AuthServiceTest {
    @Resource
    private lateinit var cx369AuthService: CX369AuthService

    @Test
    fun loginByPassword() {
        val phoneNumber = "123456"
        val password = "P@ssw0rd"

        logger.info { cx369AuthService.loginByPassword(phoneNumber, password) }
    }
}