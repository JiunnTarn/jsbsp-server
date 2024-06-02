package com.jiunntarn.jsbsp.auth.core.data.po

import com.baomidou.mybatisplus.annotation.TableId
import lombok.Data
import java.time.LocalDateTime

@Data
data class BlockInfo(
    @TableId
    val memberId: Long,
    val time: LocalDateTime,
    val reason: String?,
)
