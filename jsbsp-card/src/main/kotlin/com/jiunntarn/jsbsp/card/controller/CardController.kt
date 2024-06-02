package com.jiunntarn.jsbsp.card.controller

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.card.core.payload.Response
import com.jiunntarn.jsbsp.card.service.ActivateService
import com.jiunntarn.jsbsp.card.service.CardService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/card")
class CardController {
    @Resource
    private lateinit var cardService: CardService

    @Resource
    private lateinit var activateService: ActivateService

    @GetMapping("/get")
    fun get(@RequestAttribute("member") member: Member): Response {
        logger.info { "Member ${member.id} get card" }
        return cardService.get()
    }

    @GetMapping("/next_available_time")
    fun nextAvailableTime(@RequestAttribute("member") member: Member): Response {
        return cardService.nextAvailableTime()
    }

    @GetMapping("/schedule/get")
    fun getMemberSchedule(@RequestAttribute("member") member: Member): Response {
        return activateService.getMemberSchedule(member)
    }

}