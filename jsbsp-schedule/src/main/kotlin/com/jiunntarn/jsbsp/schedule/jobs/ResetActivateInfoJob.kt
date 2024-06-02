package com.jiunntarn.jsbsp.schedule.jobs

import com.jiunntarn.jsbsp.card.service.ActivateService
import com.jiunntarn.jsbsp.card.service.CardService
import com.jiunntarn.jsbsp.schedule.core.data.enums.JobStatus
import com.jiunntarn.jsbsp.schedule.core.job.BasicJob
import com.jiunntarn.jsbsp.schedule.util.CronUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.JobExecutionContext
import org.springframework.context.ApplicationContext

private val logger = KotlinLogging.logger {}

class ResetActivateInfoJob : BasicJob() {
    override val tag: String = "reset_activate_info"
    override val group: String = "reset"
    override val cron: String = "0 0 0 1 1 ? *"
    override var status: JobStatus = JobStatus.READY
    override var enabled: Boolean = true

    private lateinit var activateService: ActivateService
    private lateinit var cardService: CardService

    init {
        require(CronUtil.isValid(cron)) { "Invalid cron expression: $cron" }
    }

    override fun init(context: JobExecutionContext) {
        val applicationContext: ApplicationContext = context.scheduler.context["applicationContext"] as ApplicationContext
        activateService = applicationContext.getBean(ActivateService::class.java)
        cardService = applicationContext.getBean(CardService::class.java)
    }


    override fun exec(): Boolean {
        try {
            cardService.clearAllCards()
            activateService.clearAllActivateInfo()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun postSuccess() {
        logger.info { "Cleared all activate info" }
    }

    override fun postFail() {
        logger.error { "Failed to clear all activate info" }
    }

}