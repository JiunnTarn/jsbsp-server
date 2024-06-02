package com.jiunntarn.jsbsp.schedule.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import java.util.Properties

@Configuration
class SchedulerConfig {

    @Bean
    fun schedulerFactoryBean(): SchedulerFactoryBean {
        val schedulerFactoryBean = SchedulerFactoryBean()

        val properties = Properties()
        properties.setProperty("org.quartz.threadPool.threadCount", "20")
        schedulerFactoryBean.setQuartzProperties(properties)
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext")

        return schedulerFactoryBean
    }
}