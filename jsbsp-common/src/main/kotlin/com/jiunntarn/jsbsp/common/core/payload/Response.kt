package com.jiunntarn.jsbsp.common.core.payload

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.Setter

@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response(var code: Int, var data: Any? = null, var message: String? = null) {
    companion object {
        fun ok(data: Any? = null, message: String? = null): Response {
            return Response(code = 0, data = data, message = message)
        }
    }
}