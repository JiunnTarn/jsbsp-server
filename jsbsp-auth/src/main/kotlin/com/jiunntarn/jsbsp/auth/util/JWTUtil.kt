package com.jiunntarn.jsbsp.auth.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
object JWTUtil {
    private const val EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000L

    private lateinit var secret: String

    @Value("\${JWT_SECRET:DEFAULT_SECRET}")
    fun setSecret(secret: String) {
        this.secret = secret
    }

    fun sign(id: String?, info: Map<String, Any?>?): String? {
        return try {
            val date = Date(System.currentTimeMillis() + EXPIRE_TIME)
            val algorithm = Algorithm.HMAC256(secret)
            JWT.create()
                .withAudience(id)
                .withClaim("info", info)
                .withExpiresAt(date)
                .sign(algorithm)
        } catch (e: Exception) {
            logger.error { "Failed to sign JWT: ${e.message}" }
            null
        }
    }

    fun getId(token: String?): String? {
        return try {
            JWT.decode(token).audience[0]
        } catch (e: JWTDecodeException) {
            logger.error { "Failed to get ID from token: ${e.message}" }
            null
        }
    }

    private fun getInfo(token: String?): Map<String, Any?>? {
        return try {
            JWT.decode(token).getClaim("info").asMap()
        } catch (e: JWTDecodeException) {
            logger.error { "Failed to get info from token: ${e.message}" }
            null
        }
    }

    fun verify(token: String?): Boolean {
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val verifier = JWT.require(algorithm)
                .build()
            verifier.verify(token)
            true
        } catch (e: JWTVerificationException) {
            logger.error { "Token verification failed: ${e.message}" }
            false
        }
    }
}