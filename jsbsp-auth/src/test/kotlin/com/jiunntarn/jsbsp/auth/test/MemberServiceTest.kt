package com.jiunntarn.jsbsp.auth.test

import com.jiunntarn.jsbsp.auth.service.MemberService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@SpringBootTest
class MemberServiceTest {
    @Resource
    private lateinit var memberService: MemberService

    @Test
    fun block() {
        val member = memberService.getById(12345)
        logger.info { memberService.block(member, LocalDateTime.now(), "369 出行账户凭据失效") }
    }
}