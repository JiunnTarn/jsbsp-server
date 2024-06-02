package com.jiunntarn.jsbsp.auth.util

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import com.jiunntarn.jsbsp.auth.mapper.MemberMapper
import jakarta.annotation.Resource
import org.springframework.stereotype.Component


@Component
object TokenUtil {
    private lateinit var memberMapper: MemberMapper

    @Resource
    fun setMemberMapper(memberMapper: MemberMapper) {
        TokenUtil.memberMapper = memberMapper
    }

    fun sign(id: String?, info: Map<String, Any?>?): String? = JWTUtil.sign(id, info)

    fun getMember(token: String): Member? {
        if (!verify(token)) return null
        val id = JWTUtil.getId(token)

        return memberMapper.selectById(id?.toInt())
    }

    fun verify(token: String): Boolean = JWTUtil.verify(token)

}