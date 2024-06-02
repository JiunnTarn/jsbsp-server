package com.jiunntarn.jsbsp.schedule.service

import com.jiunntarn.jsbsp.schedule.core.job.JobManager
import com.jiunntarn.jsbsp.schedule.jobs.ActivateRenCaiKaJob
import com.jiunntarn.jsbsp.schedule.jobs.ResetActivateInfoJob
import com.jiunntarn.jsbsp.schedule.jobs.ValidateCX369TokenJob
import com.jiunntarn.jsbsp.schedule.jobs.ValidateMembershipJob
import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

@Service
class ScheduleService {
    @Resource
    private lateinit var jobManager: JobManager

    @PostConstruct
    fun addJob(){
        addVerifyCX369TokenJob()
        addVerifyMembershipJob()
        addActivateRenCaiKaJob()
        addResetActivateInfoJob()
    }

    private fun addVerifyCX369TokenJob() {
        jobManager.addJob(ValidateCX369TokenJob())
    }

    private fun addVerifyMembershipJob() {
        jobManager.addJob(ValidateMembershipJob())
    }

    private fun addActivateRenCaiKaJob() {
        jobManager.addJob(ActivateRenCaiKaJob())
    }

    private fun addResetActivateInfoJob() {
        jobManager.addJob(ResetActivateInfoJob())
    }
}