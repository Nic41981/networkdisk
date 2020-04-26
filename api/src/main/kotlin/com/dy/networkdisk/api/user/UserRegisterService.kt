package com.dy.networkdisk.api.user;

import com.dy.networkdisk.api.dto.user.ActiveDTO;
import com.dy.networkdisk.api.dto.user.RegisterDTO;

interface UserRegisterService {

    fun register(dto: RegisterDTO):Boolean

    fun active(dto: ActiveDTO):Boolean
}
