package com.jiunntarn.jsbsp.util.util

import com.jiunntarn.jsbsp.util.util.MD5Util.md5
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.also
import kotlin.collections.copyOfRange
import kotlin.collections.plus
import kotlin.text.substring
import kotlin.text.toByteArray

@Component
object AESUtil {
    private lateinit var salt: String

    @Value("\${AES_SALT:DEFAULT_SALT}")
    fun setSalt(salt: String) {
        this.salt = salt
    }

    fun generateKey(seed: String): ByteArray {
        val seedAndSalt = seed + salt
        return seedAndSalt.md5().substring(4, 20).toByteArray(Charsets.UTF_8)
    }

    private fun generateIv() = ByteArray(16).also { SecureRandom().nextBytes(it) }

    fun encrypt(key: ByteArray, plainText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(key, "AES")
        val iv = generateIv()
        val ivParameterSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val ivAndEncryptedData = iv + encrypted
        return Base64.getEncoder().encodeToString(ivAndEncryptedData)
    }

    fun decrypt(key: ByteArray, encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivAndEncryptedData = Base64.getDecoder().decode(encryptedText)
        val iv = ivAndEncryptedData.copyOfRange(0, 16)
        val encrypted = ivAndEncryptedData.copyOfRange(16, ivAndEncryptedData.size)

        val secretKeySpec = SecretKeySpec(key, "AES")
        val ivParameterSpec = IvParameterSpec(iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val original = cipher.doFinal(encrypted)
        return String(original, Charsets.UTF_8)
    }
}