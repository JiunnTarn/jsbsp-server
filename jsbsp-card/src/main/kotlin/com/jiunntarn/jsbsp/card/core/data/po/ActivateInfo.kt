package com.jiunntarn.jsbsp.card.core.data.po

import com.baomidou.mybatisplus.annotation.TableId
import lombok.Data
import java.time.LocalDateTime

@Data
data class ActivateInfo(
    @TableId
    val memberId: Long,
    val time: LocalDateTime
) {
    val isAvailable: Boolean get() = time.isAfter(LocalDateTime.now().minusMonths(1))
}
