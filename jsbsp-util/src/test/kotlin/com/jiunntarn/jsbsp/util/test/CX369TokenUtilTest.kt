package com.jiunntarn.jsbsp.util.test

import com.jiunntarn.jsbsp.util.util.CX369TokenUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest
class CX369TokenUtilTest {
    @Test
    fun getExpireTime() {
        val token = ""

        logger.info { CX369TokenUtil.getExpireTime(token) }
    }
}