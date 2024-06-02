package com.jiunntarn.jsbsp.auth.core.data.po

import com.baomidou.mybatisplus.annotation.TableId
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.jiunntarn.jsbsp.auth.core.data.enums.MemberStatus
import lombok.Data

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Member(
    @TableId
    val id: Long,
    val phoneNumber: String,
    val name: String,
    var status: MemberStatus,
    @JsonIgnore
    val password: String,
    @JsonIgnore
    var cx369Token: String,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Member) {
            id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + cx369Token.hashCode()
        return result
    }
}