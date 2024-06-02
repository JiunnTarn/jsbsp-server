package com.jiunntarn.jsbsp.util.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Locale
import kotlin.text.lowercase
import kotlin.text.toByteArray

object MD5Util {

    fun ByteArray.md5(): String {
        val f201857a =
            charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

        val md = MessageDigest.getInstance("MD5")
        md.update(this)
        val digest: ByteArray = md.digest()
        val cArr = CharArray(digest.size * 2)
        var i14 = 0
        for (b14 in digest) {
            val i15 = i14 + 1
            cArr[i14] = f201857a[b14.toInt() ushr 4 and 15]
            i14 = i15 + 1
            cArr[i15] = f201857a[b14.toInt() and 15]
        }
        return String(cArr).lowercase(Locale.getDefault())
    }

    fun String.md5(): String {
        val f208225a =
            charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

        val messageDigest = MessageDigest.getInstance("MD5")
        val charset = StandardCharsets.UTF_8
        messageDigest.update(this.toByteArray(charset))
        val digest = messageDigest.digest()
        val cArr = CharArray(digest.size * 2)
        val length = digest.size
        var i14 = 0
        var i15 = 0
        while (i14 < length) {
            val b14 = digest[i14]
            i14++
            val i16 = i15 + 1
            val cArr2: CharArray = f208225a
            cArr[i15] = cArr2[b14.toInt() ushr 4 and 15]
            i15 = i16 + 1
            cArr[i16] = cArr2[b14.toInt() and 15]
        }
        return String(cArr)
    }
}