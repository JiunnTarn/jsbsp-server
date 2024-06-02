package com.jiunntarn.jsbsp.card.listener

import com.jiunntarn.jsbsp.auth.core.event.MemberJoinedEvent
import com.jiunntarn.jsbsp.auth.core.event.MemberLeftEvent
import com.jiunntarn.jsbsp.card.service.ActivateService
import com.jiunntarn.jsbsp.card.service.CardService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class MemberEventListener {
    @Resource
    private lateinit var cardService: CardService

    @Resource
    private lateinit var activateService: ActivateService

    @EventListener
    fun onMemberJoined(event: MemberJoinedEvent) {
        if (!cardService.hasCard()) {
            logger.info { "No cards currently available, activation of card triggered when member joins" }
            try {
                val card = activateService.activateNextMember()
                cardService.addCard(card)
            } catch (_: Exception) {
                logger.error { "Failed to activate member" }
            }
        }
    }

    @EventListener
    fun onMemberLeft(event: MemberLeftEvent) {
        cardService.removeCardByMember(event.member)
        if (!cardService.hasCard()) {
            logger.info { "No cards currently available, activation of card triggered when member leaves" }
            try {
                val card = activateService.activateNextMember()
                cardService.addCard(card)
            } catch (_: Exception) {
                logger.error { "Failed to activate member" }
            }
        }
    }
}