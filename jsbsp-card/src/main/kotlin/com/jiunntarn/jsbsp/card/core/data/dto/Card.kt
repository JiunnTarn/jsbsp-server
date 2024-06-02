package com.jiunntarn.jsbsp.card.core.data.dto

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import java.time.LocalDateTime

data class Card(
    val cardId: Long,
    val cardOwner: Member,
    var frequency: Int,
    var nextAvailableTime: LocalDateTime,
    var lastUpdateTime: LocalDateTime,
    var qrCodeData: String? = null,
) {
    val isAvailable: Boolean
        get() = nextAvailableTime.isBefore(LocalDateTime.now())

    val isExpired: Boolean
        get() = lastUpdateTime.isBefore(LocalDateTime.now().minusSeconds(30)) || qrCodeData == null
}