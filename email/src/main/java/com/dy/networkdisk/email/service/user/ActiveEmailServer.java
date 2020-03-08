package com.dy.networkdisk.email.service.user;

import com.dy.networkdisk.email.config.MailSender;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import java.util.Date;

@Async
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ActiveEmailServer {

    private final MailSender sender;
    private final TemplateEngine engine;

    public void send(String username,String token) throws MessagingException {
        Context context = new Context();
        context.setVariable("username",username);
        context.setVariable("token",token);
        context.setVariable("createTime",new Date());
        String html = engine.process("ActiveAccountEmail",context);
        sender.byHtml("","激活账户",html);
    }
}
