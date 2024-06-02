package com.jiunntarn.jsbsp.cx369.core.payload

data class CX369Response<T>(
    val result: T?,
    val status: Status,
) {
    val isSuccess: Boolean get() = status.code == 0
    val code: Int get() = status.code
    val message: String get() = status.msg

    data class Status(
        val code: Int,
        val msg: String
    )
}
