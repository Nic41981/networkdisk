package com.dy.networkdisk.user.service;

import com.dy.networkdisk.api.config.UserConst;
import com.dy.networkdisk.api.dto.email.AccountActiveDTO;
import com.dy.networkdisk.api.dto.user.RegisterInfoDTO;
import com.dy.networkdisk.api.user.UserService;
import com.dy.networkdisk.user.config.Const;
import com.dy.networkdisk.user.dao.UserMapper;
import com.dy.networkdisk.user.tool.GsonTool;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Service;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;
    private final StringRedisTemplate template;
    private final JmsTemplate jmsTemplate;

    @Override
    public String tryLockUsername(String username) {
        String token = template.execute((RedisCallback<String>) redisConnection -> {
            //redis尝试对用户名加锁
            String key = Const.FUNC_TEMP_ACCOUNT_LOCK_REDIS_KEY + ":" + username;
            String value = UUID.randomUUID().toString();
            Boolean result = redisConnection.setNX(key.getBytes(StandardCharsets.UTF_8),value.getBytes(StandardCharsets.UTF_8));
            if (result != null && result){
                //适当延长锁以确保同名用户影响消除
                long expire = UserConst.tempAccountExpire + 60*30;
                redisConnection.expire(key.getBytes(StandardCharsets.UTF_8),expire);
                return value;
            }
            return null;
        });
        if (token != null) {
            //加锁成功后检查正式用户同名
            boolean result = mapper.checkUserExist(username) == 0;
            if (result) {
                return token;
            }
        }
        return null;
    }

    @Override
    public void register(String activeToken, RegisterInfoDTO registerInfo) {
        registerInfo.setPassword(BCrypt.hashpw(registerInfo.getPassword(),BCrypt.gensalt()));
        String key = Const.FUNC_TEMP_ACCOUNT_REDIS_KEY + ":" + activeToken;
        String value = GsonTool.toJson(registerInfo);
        template.opsForValue().set(key,value,UserConst.tempAccountExpire, TimeUnit.MINUTES);
        AccountActiveDTO dto = new AccountActiveDTO();
        dto.setActiveURL(UserConst.activeHost + "/user/active");
        dto.setUsername(registerInfo.getUsername());
        dto.setToken(activeToken);
        String message = GsonTool.toJson(dto);
        jmsTemplate.convertAndSend("account_active",message);
    }

    @Override
    public boolean hasLogin(String token) {
        return false;
    }
}
