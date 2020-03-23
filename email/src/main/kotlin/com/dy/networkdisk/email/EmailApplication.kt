package com.dy.networkdisk.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
open class EmailApplication {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(EmailApplication::class.java, *args)
        }
    }
}
