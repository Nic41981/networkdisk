package com.dy.networkdisk.web.config;

import com.dy.networkdisk.api.config.UserConst;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        return new DefaultKaptcha();
//        DefaultKaptcha kaptcha = new DefaultKaptcha();
//        Properties properties = new Properties();
//        properties.setProperty("kaptcha.border","no");
//        properties.setProperty("kaptcha.textproducer.char.string", UserConst.verificationCharSet);
//        properties.setProperty("kaptcha.textproducer.font.color", "black");
//        properties.setProperty("kaptcha.textproducer.font.size", "20");
//        properties.setProperty("kaptcha.textproducer.char.length", "2");
//        properties.setProperty("kaptcha.image.width", "70");
//        properties.setProperty("kaptcha.image.height", "25");
//        properties.setProperty("kaptcha.textproducer.char.space","15");
//        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
//        properties.setProperty("kaptcha.obscurificator.impl","com.google.code.kaptcha.impl.ShadowGimpy");
//        properties.setProperty("kaptcha.background.clear.from","darkGray");
//        properties.setProperty("kaptcha.background.clear.to","gray");
//        Config config = new Config(properties);
//        kaptcha.setConfig(config);
//        return kaptcha;
    }

}
