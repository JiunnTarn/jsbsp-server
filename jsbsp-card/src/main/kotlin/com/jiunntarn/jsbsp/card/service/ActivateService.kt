package com.jiunntarn.jsbsp.card.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.core.data.enums.MemberStatus
import com.jiunntarn.jsbsp.card.mapper.ActivateStatusMapper
import com.jiunntarn.jsbsp.auth.service.MemberService
import com.jiunntarn.jsbsp.card.core.data.dto.Card
import com.jiunntarn.jsbsp.card.core.data.po.ActivateInfo
import com.jiunntarn.jsbsp.card.core.payload.Response
import com.jiunntarn.jsbsp.cx369.service.CX369BusQrCardService
import com.jiunntarn.jsbsp.cx369.service.CX369RenCaiKaService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

private val logger = KotlinLogging.logger {}

@Service
class ActivateService : ServiceImpl<ActivateStatusMapper, ActivateInfo>() {
    @Resource
    private lateinit var memberService: MemberService

    @Resource
    private lateinit var cx369RenCaiKaService: CX369RenCaiKaService

    @Resource
    private lateinit var cx369BusQrCardService: CX369BusQrCardService


    fun getMemberSchedule(member: Member): Response {
        val unactivatedMembers = getUnactivatedMembers()

        if (!unactivatedMembers.contains(member)) {
            val activateInfo = getById(member.id)
            return Response.ok(data = activateInfo.time.format(DateTimeFormatter.ofPattern("yyyy-MM")))
        }

        val index = unactivatedMembers.indexOfFirst { it.id == member.id }
        val month = LocalDate.now().monthValue

        val g = getUnactivatedMemberNumber()
        var u = g
        var t = 12 - month
        while (g - u <= index && t >= 0) {
            val c = ceil(u.toDouble() / t).toInt()
            u = u - c
            t--
        }
        val scheduledDate = LocalDate.now().plusMonths((12 - month - t).toLong()).toString().substring(0, 7)

        return Response.ok(data = scheduledDate)
    }

    fun getCardIdByMember(member: Member): Long {
        val cx369CardListResponse = cx369BusQrCardService.getCardListNew(member.cx369Token)

        if (cx369CardListResponse.isSuccess) {
            val cx369CardList = cx369CardListResponse.result
            val cardId = cx369CardList?.first { it.isGreenCard }?.cardId
                ?: run {
                    logger.error { "Failed to get cardId for member ${member.id}" }
                    throw Exception("Failed to get cardId for member ${member.id}")
                }
            return cardId
        } else if (cx369CardListResponse.code == 102) {
            memberService.validateMemberCX369Token(member)
        }
        val cx369CardList = cx369BusQrCardService.getCardListNew(member.cx369Token).result
        val cardId = cx369CardList?.first { it.isGreenCard }?.cardId
            ?: run {
                logger.error { "Failed to get cardId for member ${member.id}" }
                throw Exception("Failed to get cardId for member ${member.id}")
            }
        return cardId
    }

    fun validateMembership(member: Member): Boolean {
        val renCaiKaTicketResponse = cx369RenCaiKaService.getTicket(member.cx369Token)
        val renCaiKaTicket = renCaiKaTicketResponse.result
        if (renCaiKaTicket == null) {
            memberService.block(member, LocalDateTime.now(), "未获得 369 出行人才卡")
            return false
        }

        if (renCaiKaTicket.hasActivated) {
            memberService.block(member, LocalDateTime.now(), "369 出行人才卡在计划外激活")
            return false
        }
        return true
    }

    fun activateNextMember(): Card {
        val member = getUnactivatedMembers().firstOrNull()
        if (member == null) {
            logger.warn { "No member to activate" }
            throw Exception("No member to activate")
        }
        logger.info { "Activating member ${member.id}" }
        val activateResponse = cx369RenCaiKaService.setTicketEnable(member.cx369Token, LocalDate.now())

        if (!activateResponse.isSuccess) {
            memberService.validateMemberCX369Token(member)
            validateMembership(member)
            logger.error { "Failed to activate member ${member.id}, response: $activateResponse" }
            throw Exception("Failed to activate member ${member.id}, response: $activateResponse")
        }

        logger.info { "Member ${member.id} activated successfully" }

        val activateInfo = ActivateInfo(memberId = member.id, time = LocalDateTime.now())
        save(activateInfo)

        val cardId = getCardIdByMember(member)
        val card = Card(
            cardId = cardId,
            cardOwner = member,
            frequency = 0,
            nextAvailableTime = LocalDateTime.now(),
            lastUpdateTime = LocalDateTime.now(),
            qrCodeData = null
        )

        return card
    }

    fun getUnactivatedMemberNumber() = getUnactivatedMembers().size

    fun getUnactivatedMembers(): List<Member> {
        val members = memberService.list(KtQueryWrapper(Member::class.java).eq(Member::status, MemberStatus.NORMAL))
        val activatedMemberIds = list().map { it.memberId }
        val unactivatedMembers = members.filter { !activatedMemberIds.contains(it.id) }
        val sortedUnactivatedMembers = unactivatedMembers.sortedBy { it.id }

        return sortedUnactivatedMembers
    }

    fun clearAllActivateInfo() {
        remove(KtQueryWrapper(ActivateInfo::class.java))
    }

}