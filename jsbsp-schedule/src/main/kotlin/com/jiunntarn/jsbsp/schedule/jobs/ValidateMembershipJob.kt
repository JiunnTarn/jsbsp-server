package com.jiunntarn.jsbsp.schedule.jobs

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.service.MemberService
import com.jiunntarn.jsbsp.card.service.ActivateService
import com.jiunntarn.jsbsp.schedule.core.data.enums.JobStatus
import com.jiunntarn.jsbsp.schedule.core.job.BasicJob
import com.jiunntarn.jsbsp.schedule.util.CronUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.JobExecutionContext
import org.springframework.context.ApplicationContext

private val logger = KotlinLogging.logger {}

class ValidateMembershipJob : BasicJob() {
    private lateinit var memberService: MemberService
    private lateinit var activateService: ActivateService

    override val tag: String = "validate_membership"
    override val group: String = "validate"
    override val cron: String = "0 30 6 * * ?"
    override var status: JobStatus = JobStatus.READY
    override var enabled: Boolean = true

    private lateinit var unactivatedMember: List<Member>

    private val failedMembers: MutableList<Member> = mutableListOf()

    init {
        require(CronUtil.isValid(cron)) { "Invalid cron expression: $cron" }
    }

    override fun init(context: JobExecutionContext) {
        val applicationContext: ApplicationContext =
            context.scheduler.context["applicationContext"] as ApplicationContext
        memberService = applicationContext.getBean(MemberService::class.java)
        activateService = applicationContext.getBean(ActivateService::class.java)
    }

    override fun preJob() {
        unactivatedMember = activateService.getUnactivatedMembers()
    }

    override fun preExec() {
        logger.info { "Start to validate membership for ${unactivatedMember.size} members" }
    }

    override fun exec(): Boolean {
        for (member in unactivatedMember) {
            if (!activateService.validateMembership(member)) {
                failedMembers.add(member)
            }
        }

        return failedMembers.isEmpty()
    }

    override fun postSuccess() {
        logger.info { "All members passed membership validation" }
    }

    override fun postFail() {
        logger.error { "Member ${failedMembers.joinToString(", ") { it.id.toString() }} failed membership validation, blocked" }
    }

}