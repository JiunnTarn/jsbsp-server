package com.jiunntarn.jsbsp.util.test

import com.jiunntarn.jsbsp.util.util.PasswordUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest
class PasswordUtilTest {
    @Test
    fun test() {
        val phone = "123456"
        val password = "P@ssw0rd"
        val encryptedPassword = PasswordUtil.encrypt(phone, password)
        logger.info { "Encrypted password: $encryptedPassword" }
        val encryptedPassword1 = PasswordUtil.encrypt(phone, password)
        logger.info { "Encrypted password1: $encryptedPassword1" }
        val decryptedPassword = PasswordUtil.decrypt(phone, encryptedPassword)
        logger.info { "Decrypted password: $decryptedPassword" }
    }
}