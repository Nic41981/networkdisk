package com.dy.networkdisk.api.email

import com.dy.networkdisk.api.dto.email.ActiveEmailDTO
import com.dy.networkdisk.api.dto.email.RemoteLoginEmailDTO

interface UserEmailService {

    fun activeAccount(dto: ActiveEmailDTO)

    fun remoteLogin(dto: RemoteLoginEmailDTO)
}