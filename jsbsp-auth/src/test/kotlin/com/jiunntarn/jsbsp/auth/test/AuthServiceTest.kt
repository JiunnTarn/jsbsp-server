package com.jiunntarn.jsbsp.auth.test

import com.jiunntarn.jsbsp.auth.service.AuthService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest
class AuthServiceTest {
    @Resource
    private lateinit var authService: AuthService

    @Test
    fun login() {
        val cx369Token = ""

        logger.info { authService.login(cx369Token) }
    }
}