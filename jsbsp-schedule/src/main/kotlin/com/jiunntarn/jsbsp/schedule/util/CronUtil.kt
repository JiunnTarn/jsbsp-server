package com.jiunntarn.jsbsp.schedule.util

import org.quartz.CronExpression
import java.util.Date

object CronUtil {
    fun isValid(cron: String): Boolean {
        return CronExpression.isValidExpression(cron)
    }

    fun getNextTime(cron: String): Long {
        return CronExpression(cron).getNextValidTimeAfter(Date(System.currentTimeMillis())).time
    }
}