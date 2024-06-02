package com.jiunntarn.jsbsp.auth.controller

import com.jiunntarn.jsbsp.auth.core.payload.Response
import com.jiunntarn.jsbsp.auth.service.AuthService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/auth")
class AuthController {
    @Resource
    private lateinit var authService: AuthService

    @PostMapping("/login")
    fun login(@RequestParam("cx369_token") cx369Token: String): Response {
        return authService.login(cx369Token)
    }
}