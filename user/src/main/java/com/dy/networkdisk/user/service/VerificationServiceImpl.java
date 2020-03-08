package com.dy.networkdisk.user.service;

import com.dy.networkdisk.api.config.UserConst;
import com.dy.networkdisk.api.user.VerificationService;
import com.dy.networkdisk.user.config.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerificationServiceImpl implements VerificationService {

    private final RedisTemplate<String,Object> template;

    public void storageAnswer(String token ,String answer){
        String key = Const.FUNC_VERIFICATION_REDIS_KEY + ":" + token;
        template.opsForValue().set(key,answer, UserConst.verificationExpire, TimeUnit.MINUTES);
    }

    @Override
    public boolean checkVerification(String token, String code) {
        String key = Const.FUNC_VERIFICATION_REDIS_KEY + ":" + token;
        String answer = (String)template.opsForValue().get(key);
        return code.equals(answer);
    }
}
