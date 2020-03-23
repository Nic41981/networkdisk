package com.dy.networkdisk.user.service

import com.dy.networkdisk.api.dto.dubbo.user.LoginDTO
import com.dy.networkdisk.api.user.UserLoginService
import com.dy.networkdisk.user.config.Const
import com.dy.networkdisk.user.dao.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import javax.annotation.Resource
import org.apache.dubbo.config.annotation.Service as DubboService

@Service
@DubboService
class UserLoginServiceImpl @Autowired constructor(
        private val template: StringRedisTemplate
) : UserLoginService {

    @Resource
    private lateinit var mapper: UserMapper

    override fun isGuests(email: String): Boolean {
        return template.hasKey("${Const.FUNC_GUESTS_REDIS_KEY}:${email}")
    }

    override fun login(dto: LoginDTO): Boolean {
        return false
    }
}