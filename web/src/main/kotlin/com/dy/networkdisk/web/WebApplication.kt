package com.dy.networkdisk.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class WebApplication {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(WebApplication::class.java, *args)
        }
    }
}