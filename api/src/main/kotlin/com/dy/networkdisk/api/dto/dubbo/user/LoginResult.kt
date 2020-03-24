package com.dy.networkdisk.api.dto.dubbo.user

import java.io.Serializable

data class LoginResult(val isSuccess: Boolean): Serializable {
    var content: String = ""
    var type: String = ""
}