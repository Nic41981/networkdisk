package com.dy.networkdisk.email.tool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.mail.MessagingException

@Component
class MailUtil() {
    private lateinit var sender: JavaMailSender

    @Value("\${spring.mail.username}")
    private lateinit var from: String

    private object SingletonHolder {
        val INSTANCE = MailUtil()
    }

    @Autowired
    constructor(sender: JavaMailSender) : this() {
        this.sender = sender
    }

    @PostConstruct
    private fun init() {
        SingletonHolder.INSTANCE.sender = sender
        SingletonHolder.INSTANCE.from = from
    }

    @Throws(MessagingException::class)
    private fun iSendByHtml(to: String, subject: String, context: String) {
        val message = sender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setFrom(from)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(context, true)
        sender.send(message)
    }

    companion object {
        fun sendByHtml(to: String, subject: String, context: String) {
            try {
                SingletonHolder.INSTANCE.iSendByHtml(to, subject, context)
            } catch (e: Exception) {

            }
        }
    }
}