package com.jiunntarn.jsbsp.cx369.util.requester

import com.google.gson.FieldNamingPolicy
import com.jiunntarn.jsbsp.cx369.util.OkHttpUtil
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone

@Component
object CX369Requester {
    private const val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 0; PhoneBrand/Model) Cx369Android/8010 BusQrCodeSdkVersion/4 DarkMode/0 CityId/2500"

    private val preBuildRequest = { requestBuilder: Request.Builder ->
        requestBuilder
            .addHeader("Host", "api.369cx.cn")
            .addHeader("user-agent", USER_AGENT)
            .addHeader("Referer", "JSBSP")
    }

    val okHttpUtil = OkHttpUtil(preBuildRequest, CX369SignInterceptor, FieldNamingPolicy.IDENTITY)

    inline fun <reified T> get(url: String, params: Map<String, String>? = null, token: String? = null): T =
        okHttpUtil.get<T>(url, params, token?.let { mapOf("Authorization" to it) })


    inline fun <reified T> post(url: String, postBody: Map<String, String>, token: String? = null): T =
        okHttpUtil.postJson<T>(url, postBody, token?.let { mapOf("Authorization" to it) })


    private object CX369SignInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val str: String
            val encrypt: String?
            val request: Request = chain.request()
            var str2 = ""
            if ((request.url.toString() + "").startsWith("https://api.369cx.cn/v2") && request.header("Sign") == null) {
                System.nanoTime()
                var unsafeUrl = request.url.toString()
                if (unsafeUrl.startsWith("https://")) {
                    unsafeUrl = unsafeUrl.replace("https://", "http://")
                }
                val cityId: String = "2500"
                val gMTString = toGMTString(Date())
                if (request.body != null) {
                    val buffer: Buffer = Buffer()
                    request.body!!.writeTo(buffer)
                    var charset: Charset = StandardCharsets.UTF_8
                    val contentType: MediaType? = request.body!!.contentType()
                    if (contentType != null) {
                        charset = contentType.charset()!!
                    }
                    str = buffer.readString(charset)
                } else {
                    str = ""
                }
                encrypt = if (request.method == "GET") {
                    encrypt(request.method + unsafeUrl + "Mozilla/5.0 (Linux; Android 0; PhoneBrand/Model) Cx369Android/8010 BusQrCodeSdkVersion/4 DarkMode/0 CityId/2500" + "" + gMTString + cityId + "tk2020")

                } else {
                    encrypt(request.method + unsafeUrl + "Mozilla/5.0 (Linux; Android 0; PhoneBrand/Model) Cx369Android/8010 BusQrCodeSdkVersion/4 DarkMode/0 CityId/2500" + "" + gMTString + cityId + str + "tk2020")
                }
                val newBuilder: Request.Builder = request.newBuilder()
                if (encrypt != null) {
                    newBuilder.header("Sign", encrypt)
                }
                newBuilder.header("CityId", cityId)
                newBuilder.header("Date", gMTString)
                val proceed: Response = chain.proceed(newBuilder.build())
                System.nanoTime()
                val build = proceed.newBuilder().body(proceed.peekBody(1048576L)).build()
                val header = build.header("Sign")
                val header2 = build.header("Date")
                if (build.body != null) {
                    Buffer()
                    val buffer2: Buffer = build.body!!.source().buffer
                    var charset2: Charset = charset("UTF-8")
                    val contentType2: MediaType? = build.body!!.contentType()
                    if (contentType2 != null) {
                        charset2 = contentType2.charset(StandardCharsets.UTF_8)!!
                    }
                    str2 = buffer2.readString(charset2)
                }
                val encrypt2 = encrypt(header2 + str2 + "cx369")
                if (encrypt2 == header) {
                    return proceed
                }
                return proceed
            }
            return chain.proceed(request)
        }

        fun toGMTString(date: Date?): String {
            val simpleDateFormat = SimpleDateFormat("EEE',' dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
            simpleDateFormat.timeZone = SimpleTimeZone(0, "GMT")
            return simpleDateFormat.format(date)
        }

        fun encrypt(str: String): String? {
            val cArr = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
            return try {
                val bytes = str.toByteArray()
                val messageDigest = MessageDigest.getInstance("MD5")
                messageDigest.update(bytes)
                val digest = messageDigest.digest()
                val cArr2 = CharArray(digest.size * 2)
                var i = 0
                for (b in digest) {
                    val i2 = i + 1
                    cArr2[i] = cArr[(b.toInt() shr 4) and 15]
                    i = i2 + 1
                    cArr2[i2] = cArr[b.toInt() and 15]
                }
                String(cArr2)
            } catch (_: Exception) {
                null
            }
        }
    }
}