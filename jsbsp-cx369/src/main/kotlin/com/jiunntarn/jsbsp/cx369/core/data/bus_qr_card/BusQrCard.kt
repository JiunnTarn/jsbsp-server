package com.jiunntarn.jsbsp.cx369.core.data.bus_qr_card

import lombok.Data

@Data
data class BusQrCard(
    val tickets: List<Ticket>?,
    val theme: Map<String, Any>,
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
    val backgroundImg1: String?,
    val logoImg1: String,
    val backgroundImg2: String?,
    val isDefaultCard: Boolean,
) {
    val isGreenCard: Boolean get() = cardMainType == 2 && cardType == "37"
    val hasTicket: Boolean get() = tickets != null && tickets.isNotEmpty()
    val hasRenCaiKaTicket: Boolean get() = tickets != null && tickets.any { it.isRenCaiKaTicket }

    @Data
    data class Ticket(
        val daylyTicketId: Int,
        val money: Double,
        val ticketStatus: Int,
        val enableTime: String,
        val disableTime: String,
        val cardType: String,
        val cardSubType: String,
        val rechargeId: Int,
        val ticketNo: String,
        val cardId: Int,
        val ticketName: String,
        val userId: Int,
    ) {
        val isRenCaiKaTicket: Boolean get() = cardType == "37" && cardSubType == "30" && ticketName == "“天下泉城 人来无忧”畅游卡"
    }
}