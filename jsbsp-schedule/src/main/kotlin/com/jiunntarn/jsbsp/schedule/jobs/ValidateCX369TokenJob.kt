package com.jiunntarn.jsbsp.schedule.jobs

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.core.data.enums.MemberStatus
import com.jiunntarn.jsbsp.auth.service.MemberService
import com.jiunntarn.jsbsp.schedule.core.data.enums.JobStatus
import com.jiunntarn.jsbsp.schedule.core.job.BasicJob
import com.jiunntarn.jsbsp.schedule.util.CronUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.JobExecutionContext
import org.springframework.context.ApplicationContext

private val logger = KotlinLogging.logger {}

class ValidateCX369TokenJob : BasicJob() {
    private lateinit var memberService: MemberService

    override val tag: String = "validate_369cx_token"
    override val group: String = "validate"
    override val cron: String = "0 0 6 * * ?"
    override var status: JobStatus = JobStatus.READY
    override var enabled: Boolean = true

    private lateinit var normalMembers: List<Member>

    private val failedMembers: MutableList<Member> = mutableListOf()

    init {
        require(CronUtil.isValid(cron)) { "Invalid cron expression: $cron" }
    }

    override fun init(context: JobExecutionContext) {
        val applicationContext: ApplicationContext = context.scheduler.context["applicationContext"] as ApplicationContext
        memberService = applicationContext.getBean(MemberService::class.java)
    }

    override fun preJob() {
        normalMembers = memberService.list(KtQueryWrapper(Member::class.java).eq(Member::status, MemberStatus.NORMAL))
    }

    override fun preExec() {
        logger.info { "Start to validate 369 token for ${normalMembers.size} members" }
    }

    override fun exec(): Boolean {
        for (member in normalMembers) {
            if (!memberService.validateMemberCX369Token(member)) {
                failedMembers.add(member)
            }
        }

        return failedMembers.isEmpty()
    }

    override fun postSuccess() {
        logger.info { "All members passed 369 token validation" }
    }

    override fun postFail() {
        logger.error { "Member ${failedMembers.joinToString(", ") { it.id.toString() }} failed 369 token validation, blocked" }
    }

}