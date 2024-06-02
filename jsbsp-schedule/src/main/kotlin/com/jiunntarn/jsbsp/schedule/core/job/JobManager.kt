package com.jiunntarn.jsbsp.schedule.core.job

import jakarta.annotation.Resource
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.stereotype.Component

@Component
class JobManager {
    @Resource
    private lateinit var scheduler: Scheduler

    fun addJob(job: BasicJob) {
        val jobDetail = JobBuilder.newJob(job::class.java)
            .withIdentity(job.jobKey)
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withIdentity(job.triggerKey)
            .withSchedule(CronScheduleBuilder.cronSchedule(job.cron))
            .build()

        if (scheduler.checkExists(job.jobKey)) {
            scheduler.deleteJob(job.jobKey)
        }
        scheduler.scheduleJob(jobDetail, trigger)
    }
    fun removeJob() {}
    fun startJob() {}
    fun stopJob() {}
    fun pauseJob() {}
    fun resumeJob() {}
    fun executeJob() {}
    fun executeJobGroup() {}
}