package com.dy.networkdisk.email.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MailSender {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender sender;

    public void byHtml(String to, String subject, String context) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(context, true);
        helper.setFrom(from);
        sender.send(message);
    }
}
