package com.dy.networkdisk.api.user

import com.dy.networkdisk.api.dto.dubbo.user.LoginDTO

interface UserLoginService {

    fun isGuests(email: String): Boolean

    fun login(dto: LoginDTO):Boolean
}