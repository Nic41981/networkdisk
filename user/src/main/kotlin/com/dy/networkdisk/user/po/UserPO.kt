package com.dy.networkdisk.user.po

import com.dy.networkdisk.api.annotation.NoArg
import java.util.*

@NoArg
data class UserPO (
        var id: Long = 0L,
        var email: String = "",
        var nickname: String = "",
        var password: String = "",
        var type: String = "",
        var inviterID: Long? = null,
        var registerTime: Date = Date(),
        var registerIP: String = "",
        var registerIPLocation: String = "",
        var activeTime: Date = Date(),
        var activeIP: String = "",
        var activeIPLocation: String = "",

        /**
         * 0:正常
         * 1:已注销
         */
        var status: Int = -1
) {
        fun format() {
                if (registerIPLocation.length > 200){
                        registerIPLocation = formatIPLocation(registerIPLocation)
                }
                if (activeIPLocation.length > 200){
                        activeIPLocation = formatIPLocation(activeIPLocation)
                }
        }

        private fun formatIPLocation(location: String): String {
                val locationArray = location.split("-")
                val buff = StringBuilder()
                for (it in locationArray) {
                        if (it.length + buff.length <= 199) {
                                break
                        }
                        buff.append(it).append("-")
                }
                if (buff.isBlank()) {
                        buff.append("unknown")
                }
                return buff.toString()
        }
}