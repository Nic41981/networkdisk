package com.dy.networkdisk.email.tool;

import com.dy.networkdisk.api.config.UserConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
public class MailSenderTool {

    private JavaMailSender sender;

    private static class SingletonHolder{
        private static MailSenderTool INSTANCE = new MailSenderTool();
    }

    private MailSenderTool(){}

    @Autowired
    public MailSenderTool(JavaMailSender sender){
        this.sender = sender;
    }

    @PostConstruct
    private void init(){
        SingletonHolder.INSTANCE.sender = this.sender;
    }

    private void iSendByHtml(String to, String subject, String context) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(context, true);
        helper.setFrom(UserConst.mailSignature);
        sender.send(message);
    }

    public static void sendByHtml(String to, String subject, String context){
        try{
            SingletonHolder.INSTANCE.iSendByHtml(to,subject,context);
        } catch (Exception e){
            log.warn("邮件发送失败!",e);
        }
    }
}
