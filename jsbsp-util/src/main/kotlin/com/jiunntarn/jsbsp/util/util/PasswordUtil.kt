package com.jiunntarn.jsbsp.util.util

import org.springframework.stereotype.Component

@Component
object PasswordUtil {
    fun encrypt(phoneNumber: String, password: String): String {
        val key = AESUtil.generateKey(phoneNumber)
        return AESUtil.encrypt(key, password)
    }

    fun decrypt(phoneNumber: String, encryptedPassword: String): String {
        val key = AESUtil.generateKey(phoneNumber)
        return AESUtil.decrypt(key, encryptedPassword)
    }
}