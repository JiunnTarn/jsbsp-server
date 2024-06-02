package com.jiunntarn.jsbsp.card.service

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.card.core.data.dto.Event
import com.jiunntarn.jsbsp.card.core.data.enums.EventType
import com.jiunntarn.jsbsp.card.core.interceptor.AuthInterceptor
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import jakarta.websocket.OnClose
import jakarta.websocket.OnError
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger {}

@Service
@ServerEndpoint(
    value = "/card/event/{cardId}/{sessionId}",
    configurator = AuthInterceptor::class
)
class EventService {
    private lateinit var session: Session
    private lateinit var sessionId: String
    private lateinit var cardId: String
    private lateinit var memberId: String

    @Resource
    private fun setCardService(cardService: CardService) {
        Companion.cardService = cardService
    }

    @OnOpen
    fun onOpen(
        session: Session,
        @PathParam(value = "cardId") cardId: String,
        @PathParam(value = "sessionId") sessionId: String
    ) {
        this.session = session
        this.cardId = cardId
        this.sessionId = sessionId
        val member = session.userProperties["member"] as Member?
        if (member == null) {
            session.close()
            return
        }
        this.memberId = member.id.toString()

        if (cardId.toLongOrNull() == null) {
            session.close()
            logger.info { "CardId $cardId is invalid, session closed" }
            return
        }

        if (!cardService.validateCard(cardId.toLong(), false)) {
            session.close()
            logger.info { "Card $cardId is invalid, session closed" }
            return
        }

        cardChannels.get(cardId)?.get(memberId)?.get(sessionId)?.session?.close()

        val cardChannel = cardChannels.getOrPut(cardId) { ConcurrentHashMap() }
        val cardMemberChannel = cardChannel.getOrPut(memberId) { ConcurrentHashMap() }
        cardMemberChannel.put(sessionId, this)
    }

    @OnClose
    fun onClose() {
        logger.info { "Member $memberId@$sessionId disconnected from channel $cardId" }
        cardChannels.get(cardId)?.get(memberId)?.remove(sessionId)
        if (cardChannels[cardId]?.get(memberId)?.isEmpty() == true) {
            logger.info { "CardMemberChannel $cardId:$memberId has no session connected, removed" }
            cardChannels[cardId]?.remove(memberId)
        }

        if (cardChannels[cardId]?.isEmpty() == true) {
            cardChannels.remove(cardId)
            logger.info { "Channel $cardId has no member connected, removed" }
        }
    }

    @OnMessage
    fun onEvent(eventString: String) {
        try {
            val event = Event.parseString(eventString)
            handleEvent(event)
        } catch (e: Exception) {
            logger.error { "Error occurred when parsing event: $eventString" }
            session.close()
        }
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        logger.error { "Error occurred in event channel (cardId: $cardId, MemberSession: $memberId@$sessionId)" }
        error.printStackTrace()
    }

    private fun handleEvent(event: Event) {
        when (event.type) {
            EventType.CONSUME -> {
                logger.info { "Card $cardId consumed by member $memberId@$sessionId at ${event.time}: ${event.data}, channel $cardId is about to be shut down" }
                cardService.lockCard(cardId.toLong())
                cardChannels.get(cardId)?.forEach { (_, cardMemberChannel) ->
                    cardMemberChannel.forEach { (_, eventService) ->
                        eventService.session.close()
                    }
                }
            }

            EventType.FAIL -> {
                logger.error { "Member $memberId@$sessionId reported failure when consuming card $cardId at ${event.time}: ${event.data}" }

                if (!cardService.validateCard(cardId.toLong(), true)) {
                    cardChannels.get(cardId)?.forEach { (_, cardMemberChannel) ->
                        cardMemberChannel.forEach { (_, eventService) ->
                            eventService.session.close()
                        }
                    }
                }
            }
        }
    }


    companion object {
        // cardId -> memberId -> sessionId -> EventService
        private val cardChannels =
            ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, EventService>>>()

        private lateinit var cardService: CardService

        fun appointSending(session: Session, message: String): Boolean {
            return try {
                session.basicRemote?.sendText(message)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }

        fun appointSending(cardId: String, memberId: String, message: String): Boolean {
            return try {
                cardChannels.get(cardId)?.get(memberId)?.forEach { (_, eventService) ->
                    eventService.session.basicRemote?.sendText(message)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }

        fun groupSending(message: String) {
            cardChannels.forEach { (_, cardChannel) ->
                cardChannel.forEach { (_, cardMemberChannel) ->
                    cardMemberChannel.forEach { (_, eventService) ->
                        eventService.session.basicRemote?.sendText(message)
                    }
                }
            }
        }
    }
}