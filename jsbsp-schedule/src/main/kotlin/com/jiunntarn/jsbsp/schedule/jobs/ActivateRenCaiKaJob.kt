package com.jiunntarn.jsbsp.schedule.jobs

import com.jiunntarn.jsbsp.auth.service.MemberService
import com.jiunntarn.jsbsp.card.service.ActivateService
import com.jiunntarn.jsbsp.card.service.CardService
import com.jiunntarn.jsbsp.schedule.core.data.enums.JobStatus
import com.jiunntarn.jsbsp.schedule.core.job.BasicJob
import com.jiunntarn.jsbsp.schedule.util.CronUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.JobExecutionContext
import org.springframework.context.ApplicationContext
import java.time.LocalDate
import kotlin.math.ceil

private val logger = KotlinLogging.logger {}

class ActivateRenCaiKaJob : BasicJob() {
    override val tag: String = "activate_ren_cai_ka"
    override val group: String = "activate"
    override val cron: String = "0 0 0 1 * ?"
    override var status: JobStatus = JobStatus.READY
    override var enabled: Boolean = true

    private lateinit var activateService: ActivateService
    private lateinit var cardService: CardService
    private lateinit var memberService: MemberService

    private var activateMemberNumber: Int = 0

    init {
        require(CronUtil.isValid(cron)) { "Invalid cron expression: $cron" }
    }

    override fun init(context: JobExecutionContext) {
        val applicationContext: ApplicationContext = context.scheduler.context["applicationContext"] as ApplicationContext
        activateService = applicationContext.getBean(ActivateService::class.java)
        memberService = applicationContext.getBean(MemberService::class.java)
        cardService = applicationContext.getBean(CardService::class.java)
    }

    override fun preExec() {
        cardService.clearAllCards()

        val unactivatedMemberNumber = activateService.getUnactivatedMemberNumber()
        val monthToEndOfYear = 12 - LocalDate.now().monthValue + 1
        activateMemberNumber = ceil(unactivatedMemberNumber.toDouble() / monthToEndOfYear).toInt()

        logger.info { "Start activate next $activateMemberNumber member" }
    }

    override fun exec(): Boolean {
        var result = true
        var i = 1
        var successCount = 0
        var retryCount = 0

        while (i <= activateMemberNumber || successCount < 1) {
            if (retryCount > 3) {
                logger.error { "Failed to activate member" }
                result = false
                break
            }
            try {
                val card = activateService.activateNextMember()
                cardService.addCard(card)
                successCount++
            } catch (e: Exception) {
                retryCount++
                logger.error { "Failed to activate member" }
                result = false
                continue
            } finally {
                i++
            }
        }
        return result
    }

    override fun postSuccess() {
        logger.info { "Activate $activateMemberNumber member successfully" }
    }

    override fun postFail() {
        logger.error { "Some member failed to activate" }
    }

}