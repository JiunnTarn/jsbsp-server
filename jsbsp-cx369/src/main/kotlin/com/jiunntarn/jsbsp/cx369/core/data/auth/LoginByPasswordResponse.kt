package com.jiunntarn.jsbsp.cx369.core.data.auth

import lombok.Data

@Data
data class LoginByPasswordResponse(
    val token: String,
    val role: Int,
    val roles: String,
    val nickName: String,
    val headUrl: String,
    val phone: String,
    val uniqueId: String,
)
