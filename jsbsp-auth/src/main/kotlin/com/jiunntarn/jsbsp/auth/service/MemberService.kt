package com.jiunntarn.jsbsp.auth.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.jiunntarn.jsbsp.auth.core.data.enums.MemberStatus
import com.jiunntarn.jsbsp.auth.core.data.po.BlockInfo
import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.core.event.MemberJoinedEvent
import com.jiunntarn.jsbsp.auth.core.event.MemberLeftEvent
import com.jiunntarn.jsbsp.auth.core.payload.Response
import com.jiunntarn.jsbsp.auth.mapper.BlockInfoMapper
import com.jiunntarn.jsbsp.auth.mapper.MemberMapper
import com.jiunntarn.jsbsp.cx369.service.CX369AuthService
import com.jiunntarn.jsbsp.cx369.service.CX369BusQrCardService
import com.jiunntarn.jsbsp.cx369.service.CX369RenCaiKaService
import com.jiunntarn.jsbsp.cx369.service.CX369UserService
import com.jiunntarn.jsbsp.util.util.CX369TokenUtil
import com.jiunntarn.jsbsp.util.util.PasswordUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class MemberService : ServiceImpl<MemberMapper, Member>() {
    @Resource
    private lateinit var blockInfoMapper: BlockInfoMapper

    @Resource
    private lateinit var cx369AuthService: CX369AuthService

    @Resource
    private lateinit var cx369UserService: CX369UserService

    @Resource
    private lateinit var cx369RenCaiKaService: CX369RenCaiKaService

    @Resource
    private lateinit var cx369BusQrCardService: CX369BusQrCardService

    @Resource
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    fun join(cx369Token: String, password: String): Response {
        val idCardInfoResponse = cx369UserService.getIdCardInfo(cx369Token)
        if (!idCardInfoResponse.isSuccess) {
            return Response(code = 1, message = "Invalid cx369 token")
        }
        val renCaiKaTicket = cx369RenCaiKaService.getTicket(cx369Token).result
        if (renCaiKaTicket == null) {
            return Response(code = 2, message = "未领取畅游卡")
        }

        if (renCaiKaTicket.hasActivated) {
            return Response(code = 2, message = "你账号的人才卡已经被激活了")

        }

        val id = renCaiKaTicket.userId
        val phoneNumber = renCaiKaTicket.phone
        val name = renCaiKaTicket.fullName
        val status = MemberStatus.NORMAL
        val encryptedPassword = PasswordUtil.encrypt(phoneNumber, password)

        val savedMember = getById(id)
        if (savedMember != null) {
            return Response(code = 3, message = "Member already exists")
        }

        val member = Member(
            id = id,
            phoneNumber = phoneNumber,
            name = name,
            status = status,
            password = encryptedPassword,
            cx369Token = cx369Token
        )
        save(member)

        logger.info { "Member ${member.id} joined" }

        applicationEventPublisher.publishEvent(MemberJoinedEvent(this, member))

        return Response.ok(data = member)
    }

    fun announce(member: Member, cx369Token: String): Response {
        val idCardInfoResponse = cx369UserService.getIdCardInfo(cx369Token)
        if (!idCardInfoResponse.isSuccess) {
            return Response(code = 1, message = "Invalid cx369 token")
        }
        val idCardInfo = idCardInfoResponse.result!!
        val memberId = idCardInfo.userId
        if (memberId != member.id) {
            return Response(code = 2, message = "Member id not match")
        }
        updateCX369Token(member, cx369Token)

        return Response.ok(message = "Success")
    }

    fun leave(member: Member): Response {
        removeById(member)

        applicationEventPublisher.publishEvent(MemberLeftEvent(this, member))

        return Response.ok(message = "Success")
    }

    fun blockInfo(member: Member): Response {
        val blockInfo = blockInfoMapper.selectById(member.id)
        if (blockInfo == null) {
            return Response(code = 1, message = "Member not blocked")
        }

        return Response.ok(data = blockInfo)
    }

    fun updateCX369Token(member: Member, cx369Token: String): Boolean {
        val tokenExpireTime = CX369TokenUtil.getExpireTime(cx369Token)
        val savedTokenExpireTime = CX369TokenUtil.getExpireTime(member.cx369Token)
        if (tokenExpireTime.isAfter(savedTokenExpireTime)) {
            logger.info { "Update member ${member.id} 369 token" }
            member.cx369Token = cx369Token
            return updateById(member)
        } else {
            return false
        }
    }

    @Transactional
    fun block(member: Member, time: LocalDateTime, reason: String): Boolean {
        try {
            if (blockInfoMapper.exists(KtQueryWrapper(BlockInfo::class.java).eq(BlockInfo::memberId, member.id))) {
                return true
            }
            member.status = MemberStatus.BLOCKED
            updateById(member)

            val blockInfo = BlockInfo(memberId = member.id, time = time, reason = reason)
            blockInfoMapper.insert(blockInfo)

            logger.info { "Block member ${member.id}, because $reason" }

            applicationEventPublisher.publishEvent(MemberLeftEvent(this, member))

            return true
        } catch (e: Exception) {
            logger.error { "Block member ${member.id} failed: ${e.message}, rolled back" }
            return false
        }
    }

    @Transactional
    fun validateMemberCX369Token(member: Member): Boolean {
        val idCardInfoResponse = cx369UserService.getIdCardInfo(member.cx369Token)

        if (idCardInfoResponse.isSuccess) {
            return true
        }

        logger.info { "Member ${member.id} 369 token is invalid, re-login" }
        val plainPassword = PasswordUtil.decrypt(member.phoneNumber, member.password)
        val loginResponse = cx369AuthService.loginByPassword(member.phoneNumber, plainPassword)
        if (!loginResponse.isSuccess) {
            block(member, LocalDateTime.now(), "369 出行账户凭据失效")
            return false
        }
        val cx369Token = loginResponse.result!!.token
        updateCX369Token(member, cx369Token)
        logger.info { "Member ${member.id} 369 token is updated" }

        return true
    }

    private fun getMemberNumber() = count()

}