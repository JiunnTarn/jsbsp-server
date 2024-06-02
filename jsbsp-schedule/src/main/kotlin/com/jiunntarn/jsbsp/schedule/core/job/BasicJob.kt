package com.jiunntarn.jsbsp.schedule.core.job

import com.jiunntarn.jsbsp.schedule.core.data.enums.JobStatus
import com.jiunntarn.jsbsp.schedule.util.CronUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.JobExecutionContext
import org.quartz.JobKey
import org.quartz.TriggerKey
import org.springframework.scheduling.quartz.QuartzJobBean

private val logger = KotlinLogging.logger {}

abstract class BasicJob: QuartzJobBean() {
    abstract val tag: String
    abstract val group: String
    abstract val cron: String
    abstract var status: JobStatus

    val id: String get() = "JOB:$group:$tag"

    val jobKey: JobKey get() = JobKey.jobKey(id, group)
    val triggerKey: TriggerKey get() = TriggerKey.triggerKey(id, group)

    abstract var enabled: Boolean

    fun enable() { enabled = true }
    fun disable() { enabled = false }

    // Job Run Process: preJob -> preExec -> exec -> postExec -> postSuccess / postFail
    open fun init(context: JobExecutionContext) {}
    open fun preJob() {}
    open fun preExec() {}
    abstract fun exec(): Boolean
    open fun postExec() {}
    open fun postSuccess() {}
    open fun postFail() {}

    final override fun executeInternal(context: JobExecutionContext) {
        try {
            if (!enabled) {
                logger.info { "Job $id is disabled, skip execution" }
                return
            }
            if (status == JobStatus.RUNNING) {
                logger.info { "Job $id is already running, skip execution" }
                return
            }
            status = JobStatus.RUNNING
            init(context)
            preJob()
            preExec()
            if (exec()) {
                postSuccess()
            } else {
                postFail()
            }
            postExec()
        } catch (e: Exception) {
            logger.error(e) { "Job $id execution failed: ${e.message}" }
        } finally {
            if (CronUtil.getNextTime(cron) > System.currentTimeMillis()) {
                status = JobStatus.READY
            } else {
                status = JobStatus.COMPLETED
            }
        }
    }
}