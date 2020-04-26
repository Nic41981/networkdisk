package com.dy.networkdisk.user.po

import com.dy.networkdisk.api.annotation.NoArg
import java.lang.StringBuilder
import java.util.*

@NoArg
data class LoginPO(
        var id: Long,
        var userID: Long,
        var loginTime: Date,
        var loginIP: String,
        var loginIPLocation: String,
        var type: String
){
    init {
        if (loginIPLocation.length > 200){
            val locationArray = loginIPLocation.split("-")
            val buff = StringBuilder()
            for (it in locationArray){
                if (it.length + buff.length > 199){
                    break
                }
                buff.append(it).append("-")
            }
            if (buff.isBlank()){
                buff.append("unknown")
            }
            loginIPLocation = buff.toString()
        }
    }
}