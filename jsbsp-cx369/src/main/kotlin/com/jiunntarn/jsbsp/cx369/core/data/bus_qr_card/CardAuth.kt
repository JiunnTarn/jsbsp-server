package com.jiunntarn.jsbsp.cx369.core.data.bus_qr_card

import lombok.Data

@Data
data class CardAuth(
    val authData: String,
    val expireTime: String,
    val cardVdate: String,
    val cardSdate: String,
    val cardMoney: Double,
    val cardLimitMoney: Double,
    val cardType: String,
    val cardSubType: String,
    val cardId: Long,
    val cardMainType: Int,
    val cardName: String,
    val cardDescription: String,
    val cardNo: String,
    val cardState: Int,
    val backgroundImg1: String,
    val logoImg1: String,
    val backgroundImg2: String,
    val isDefaultCard: Boolean,
)