package com.jiunntarn.jsbsp.card.core.interceptor

import com.jiunntarn.jsbsp.auth.util.TokenUtil
import jakarta.websocket.HandshakeResponse
import jakarta.websocket.server.HandshakeRequest
import jakarta.websocket.server.ServerEndpointConfig
import org.springframework.stereotype.Component

@Component
class AuthInterceptor: ServerEndpointConfig.Configurator() {
    override fun modifyHandshake(
        sec: ServerEndpointConfig,
        request: HandshakeRequest,
        response: HandshakeResponse
    ) {
        val authorization = request.headers["Authorization"]?.firstOrNull()
        if (authorization != null && authorization.startsWith("Bearer ")) {
            val token = authorization.substring(7)
            if (TokenUtil.verify(token)) {
                val member = TokenUtil.getMember(token)
                if (member != null) {
                    sec.userProperties["member"] = member
                }
            }
        }
    }
}
