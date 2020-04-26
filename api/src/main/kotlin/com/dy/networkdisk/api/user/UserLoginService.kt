package com.dy.networkdisk.api.user

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.user.AutoLoginDTO
import com.dy.networkdisk.api.dto.user.LoginDTO
import com.dy.networkdisk.api.dto.user.LoginResult

interface UserLoginService {

    fun isGuest(email: String,password: String): Boolean

    fun login(dto: LoginDTO): QYResult<LoginResult>

    fun autoLogin(dto: AutoLoginDTO): Boolean
}