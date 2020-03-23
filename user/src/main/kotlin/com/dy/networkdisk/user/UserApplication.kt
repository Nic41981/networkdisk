package com.dy.networkdisk.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
open class UserApplication {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(UserApplication::class.java, *args)
        }
    }
}
