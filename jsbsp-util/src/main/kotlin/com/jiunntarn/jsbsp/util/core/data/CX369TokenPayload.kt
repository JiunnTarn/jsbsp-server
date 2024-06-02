package com.jiunntarn.jsbsp.util.core.data

import lombok.Data

@Data
data class CX369TokenPayload(
    val uniqueName: String,
    val role: String,
    val nameid: String,
    val jti: String,
    val app: String,
    val sid: String,
    val nbf: Long,
    val exp: Long,
    val iat: Long,
    val iss: String,
    val aud: String,
)