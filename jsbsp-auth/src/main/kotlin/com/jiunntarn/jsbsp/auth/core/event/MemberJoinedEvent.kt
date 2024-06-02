package com.jiunntarn.jsbsp.auth.core.event

import com.jiunntarn.jsbsp.auth.core.data.po.Member
import org.springframework.context.ApplicationEvent

class MemberJoinedEvent(
    s: Any,
    val member: Member,
): ApplicationEvent(s)