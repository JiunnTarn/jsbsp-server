package com.jiunntarn.jsbsp.auth.core.handler

import com.jiunntarn.jsbsp.auth.core.payload.Response
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody


private val logger = KotlinLogging.logger {}

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    fun exceptionHandler(e: Exception): Response {
        logger.error { e.stackTraceToString() }
        return Response.unknownError("错误: ${e.message}")
    }

    @ExceptionHandler(value = [MissingServletRequestParameterException::class])
    @ResponseBody
    fun exceptionHandler(e: MissingServletRequestParameterException): Response {
        logger.error { "Missing Parameter: ${e.parameterName} in ${e.methodParameter?.method}" }
        return Response.missingParam(e.parameterName)
    }
}