package com.dy.networkdisk.user.po

import java.lang.StringBuilder
import java.util.*

data class LoginPO(
        var id: Long = 0L,
        var userID: Long = 0L,
        var loginTime: Date = Date(),
        var loginIP: String = "",
        var loginIPLocation: String = ""
){
    fun format(){
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