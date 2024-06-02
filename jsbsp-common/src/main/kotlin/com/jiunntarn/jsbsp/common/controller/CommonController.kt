package com.jiunntarn.jsbsp.common.controller

import com.jiunntarn.jsbsp.common.core.payload.Response
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/common")
class CommonController {

    @GetMapping("/ping")
    fun ping(): Response {
        return Response.ok(message = "pong")
    }

    @GetMapping("/announcement")
    fun announcement(): Response {
        return Response.ok(data = "announcement")
    }
}