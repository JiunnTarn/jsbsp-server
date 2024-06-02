package com.jiunntarn.jsbsp.auth.test

import com.jiunntarn.jsbsp.auth.mapper.MemberMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest
class MemberDAOTest {
    @Resource
    private lateinit var memberMapper: MemberMapper

    @Test
    fun test() {
        val memberId = 12345
        val member = memberMapper.selectById(memberId)

        logger.info { member }
    }
}