package com.dy.networkdisk.api.user

import com.dy.networkdisk.api.dto.dubbo.user.LoginDTO
import com.dy.networkdisk.api.dto.dubbo.user.LoginResult

interface UserLoginService {

    fun getUserType(email: String): String

    fun login(dto: LoginDTO): LoginResult
}