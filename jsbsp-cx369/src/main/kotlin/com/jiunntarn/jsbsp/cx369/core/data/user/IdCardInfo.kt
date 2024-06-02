package com.jiunntarn.jsbsp.cx369.core.data.user

import lombok.Data

@Data
data class IdCardInfo(
    val idCardUserId: Int,
    val userId: Long,
    val idCardNumber: String,
    val name: String,
)
