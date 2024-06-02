package com.jiunntarn.jsbsp.auth.config

import com.jiunntarn.jsbsp.auth.core.interceptor.AuthInterceptor
import com.jiunntarn.jsbsp.auth.core.interceptor.MembershipInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class InterceptorConfig : WebMvcConfigurer {
    private val loginRequiredList = listOf(
        // Member
        "/member/announce",
        "/member/leave",
        "/member/block_info",

        // Card
        "/card/get",
        "/card/next_available_time",
        "/card/schedule/get",
    )

    private val membershipRequiredList = listOf(
        // Card
        "/card/get",
        "/card/next_available_time",
        "/card/schedule/get",
    )

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AuthInterceptor()).addPathPatterns(loginRequiredList)
        registry.addInterceptor(MembershipInterceptor()).addPathPatterns(membershipRequiredList)
    }
}