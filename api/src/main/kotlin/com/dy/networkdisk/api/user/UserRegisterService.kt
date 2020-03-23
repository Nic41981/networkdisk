package com.dy.networkdisk.api.user;

import com.dy.networkdisk.api.dto.dubbo.user.ActiveDTO;
import com.dy.networkdisk.api.dto.dubbo.user.RegisterDTO;

interface UserRegisterService {

    fun register(dto: RegisterDTO):Boolean

    fun active(dto: ActiveDTO):Boolean
}
