package com.dy.networkdisk.email.tool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.mail.MessagingException

@Component
class MailUtil @Autowired constructor(
        private val sender: JavaMailSender
) {
    @Value("\${spring.mail.username}")
    private lateinit var from: String

    fun sendByHtml(to: String, subject: String, context: String) {
        val message = sender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setFrom(from)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(context, true)
        sender.send(message)
    }
}