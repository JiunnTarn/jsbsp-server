package com.jiunntarn.jsbsp.cx369.core.data.ren_cai_ka

import lombok.Data

@Data
data class RenCaiKaTicket(
    val renCaiTicketId: Int,
    val fullName: String,
    val idCardNo: String,
    val chsiCode: String,
    val sex: String,
    val dateOfGraduation: String,
    val dateOfBirth: String,
    val enableTime: String?,
    val imgUrl: String,
    val ethnic: String,
    val phone: String,
    val status: Int,
    val userId: Long,
) {
    val hasActivated: Boolean get() = enableTime != null
}
