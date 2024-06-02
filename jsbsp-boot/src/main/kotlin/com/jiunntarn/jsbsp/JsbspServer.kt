package com.jiunntarn.jsbsp

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.jiunntarn.jsbsp"])
@MapperScan("com.jiunntarn.**.mapper")
class JsbspServer

fun main(args: Array<String>) {
    runApplication<JsbspServer>(*args)
}
