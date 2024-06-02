package com.jiunntarn.jsbsp.auth.controller

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.core.payload.Response
import com.jiunntarn.jsbsp.auth.service.MemberService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/member")
class MemberController {
    @Resource
    private lateinit var memberService: MemberService

    @PostMapping("/join")
    fun join(
        @RequestParam("cx369_token") cx369Token: String,
        @RequestParam("password") password: String
    ): Response {
        return memberService.join(cx369Token, password)
    }

    @PostMapping("/announce")
    fun announce(
        @RequestAttribute("member") member: Member,
        @RequestParam("cx369_token") cx369Token: String
    ): Response {
        logger.info { "Announce from member ${member.id}" }
        return memberService.announce(member, cx369Token)
    }

    @GetMapping("/leave")
    fun leave(
        @RequestAttribute("member") member: Member,
    ): Response {
        logger.info { "Member ${member.id} leave" }
        return memberService.leave(member)
    }

    @GetMapping("/block_info")
    fun blockInfo(
        @RequestAttribute("member") member: Member,
    ): Response {
        return memberService.blockInfo(member)
    }

}