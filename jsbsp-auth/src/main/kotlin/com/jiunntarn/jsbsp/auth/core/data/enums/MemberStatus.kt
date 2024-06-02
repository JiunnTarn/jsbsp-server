package com.jiunntarn.jsbsp.auth.core.data.enums

import com.baomidou.mybatisplus.annotation.EnumValue
import com.fasterxml.jackson.annotation.JsonValue

enum class MemberStatus(@EnumValue @JsonValue val id: Int) {
    NORMAL(0),
    BLOCKED(1)
}