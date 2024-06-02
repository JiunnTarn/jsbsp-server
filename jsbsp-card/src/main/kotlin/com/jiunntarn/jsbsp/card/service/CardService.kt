package com.jiunntarn.jsbsp.card.service

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.service.MemberService
import com.jiunntarn.jsbsp.card.core.CardManager
import com.jiunntarn.jsbsp.card.core.data.dto.Card
import com.jiunntarn.jsbsp.card.core.data.vo.CardVO
import com.jiunntarn.jsbsp.card.core.payload.Response
import com.jiunntarn.jsbsp.cx369.service.CX369BusQrCardService
import com.jiunntarn.jsbsp.cx369.service.CX369RenCaiKaService
import com.jiunntarn.jsbsp.util.util.DateTimeUtil.toTimestamp
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import java.time.LocalDateTime


private val logger = KotlinLogging.logger {}

@DependsOn("AESUtil")
@Service
class CardService {
    @Resource
    private lateinit var cardManager: CardManager

    @Resource
    private lateinit var memberService: MemberService

    @Resource
    private lateinit var activateService: ActivateService

    @Resource
    private lateinit var cx369BusQrCardService: CX369BusQrCardService

    @Resource
    private lateinit var cx369RenCaiKaService: CX369RenCaiKaService

    @PostConstruct
    fun initCardService() {
        loadFromDatabase()
    }

    fun get(): Response {
        if (!cardManager.hasAvailableCard()) {
            return Response(code = 1, message = "No available card")
        }
        val card = cardManager.getAvailableCard()!!

        if (!card.isExpired) {
            val cardVO = CardVO(cardId = card.cardId, qrCodeData = card.qrCodeData!!)
            return Response.ok(data = cardVO)
        }
        val qrCodeData = cx369BusQrCardService.getQrCodeData(card.cardOwner.cx369Token, card.cardId)
        card.qrCodeData = qrCodeData
        card.lastUpdateTime = LocalDateTime.now()
        cardManager.addOrUpdateCard(card)

        val cardVO = CardVO(cardId = card.cardId, qrCodeData = qrCodeData)
        return Response.ok(data = cardVO)
    }

    fun nextAvailableTime(): Response {
        if (cardManager.hasAvailableCard()) {
            return Response(code = 1, message = "Available now")
        }
        if (cardManager.getCards().isEmpty()) {
            return Response(code = 2, message = "No card")
        }
        return Response.ok(data = cardManager.nextAvailableTime()!!.toTimestamp())
    }


    fun addCard(card: Card) {
        cardManager.addOrUpdateCard(card)
    }

    fun removeCardByMember(member: Member) {
        cardManager.filterBy({ it.cardOwner != member }, remove = true)
    }

    fun validateCard(cardId: Long, force: Boolean): Boolean {
        if (!force) {
            return cardManager.validateCard(cardId)
        } else {
            if (!cardManager.validateCard(cardId)) {
                return false
            } else {
                val member = cardManager.getCardById(cardId)!!.cardOwner
                if (memberService.validateMemberCX369Token(member)) {
                    val renCaiKaTicket = cx369RenCaiKaService.getTicket(member.cx369Token).result
                    if (renCaiKaTicket == null) {
                        return false
                    }
                    return !renCaiKaTicket.hasActivated
                } else {
                    return false
                }
            }
        }
    }

    fun hasCard(): Boolean {
        return cardManager.hasCard()
    }

    fun lockCard(cardId: Long) {
        val card = cardManager.getCardById(cardId)
        if (card == null) {
            logger.error { "Error occurred when locking card: Card $cardId not found" }
            return
        }
        card.frequency++
        card.nextAvailableTime = LocalDateTime.now().plusMinutes(3)
        cardManager.addOrUpdateCard(card)
    }

    private fun loadFromDatabase() {
        val activateInfoList = activateService.list()
        var cardNumber = 0
        for (activateInfo in activateInfoList) {
            if (!activateInfo.isAvailable) {
                continue
            }
            val member = memberService.getById(activateInfo.memberId)
            if (member == null) {
                logger.error { "Member ${activateInfo.memberId} not found" }
                continue
            }
            val cardId = activateService.getCardIdByMember(member)
            val card = Card(
                cardId = cardId,
                cardOwner = member,
                frequency = 0,
                nextAvailableTime = LocalDateTime.now(),
                lastUpdateTime = LocalDateTime.now(),
                qrCodeData = null,
            )
            addCard(card)
            cardNumber++
        }

        logger.info { "Loaded $cardNumber cards from database." }
    }

    fun clearAllCards() {
        cardManager.clear()
    }

}