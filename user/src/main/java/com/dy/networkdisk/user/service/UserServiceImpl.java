package com.dy.networkdisk.user.service;

import com.dy.networkdisk.api.config.ConfigRedisKey;
import com.dy.networkdisk.api.config.QueueConst;
import com.dy.networkdisk.api.dto.mq.email.AccountActiveDTO;
import com.dy.networkdisk.api.dto.dubbo.user.GuestsDTO;
import com.dy.networkdisk.api.user.UserService;
import com.dy.networkdisk.user.config.Const;
import com.dy.networkdisk.user.dao.UserMapper;
import com.dy.networkdisk.user.tool.BeanTransUtil;
import com.dy.networkdisk.user.tool.ConfigUtil;
import com.dy.networkdisk.user.tool.GsonTool;
import com.dy.networkdisk.user.tool.QueueUtil;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.Service;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;
    private final StringRedisTemplate template;
    private final JmsTemplate jmsTemplate;
    private final ConfigUtil config;
    private final QueueUtil queue;

    /**
     * 启动时初始化数据库连接
     */
    @PostConstruct
    private void init(){
        mapper.init();
    }

    /**
     * 检查重名并给用户名加锁
     * @param username 用户名
     * @return 锁
     */
    @Override
    public String getGuestsLock(String username) {
        String key = Const.FUNC_GUESTS_LOCK_REDIS_KEY + ":" + username;
        String value = UUID.randomUUID().toString();
        Boolean result = template.execute((RedisCallback<Boolean>) connection -> connection.setNX(key.getBytes(StandardCharsets.UTF_8),value.getBytes(StandardCharsets.UTF_8)));
        if (result != null && result){
            //用户名锁定成功
            long expire = config.getLong(ConfigRedisKey.USER_GUESTS_EXPIRE,12L);
            template.expire(key,expire,TimeUnit.HOURS);
            if (mapper.checkUsernameExist(username) == 0){
                return value;
            }
        }
        return null;
    }

    @Override
    public void register(GuestsDTO guests) {
        //密码加密
        guests.setPassword(BCrypt.hashpw(guests.getPassword(),BCrypt.gensalt()));
        //存储游客信息
        String key = Const.FUNC_GUESTS_REDIS_KEY + ":" + guests.getLock();
        String value = GsonTool.toJson(guests);
        long expire = config.getLong(ConfigRedisKey.USER_GUESTS_EXPIRE,12L);
        template.opsForValue().set(key,value,expire, TimeUnit.HOURS);
        //发送激活邮件
        String activeHost = config.getString(ConfigRedisKey.USER_GUESTS_ACTIVE_HOST,"127.0.0.1");
        AccountActiveDTO dto = BeanTransUtil.trans(guests,new AccountActiveDTO());
        dto.setActiveURL(activeHost + "/user/active");
        String message = GsonTool.toJson(dto);
        jmsTemplate.convertAndSend(queue.get(QueueConst.MAIL_ACCOUNT_ACTIVE),message);
    }

    @Override
    public boolean hasLogin(String token) {
        return false;
    }
}
