package com.dy.networkdisk.user.po

import java.util.*

data class UserPO (
        var id: Long = 0L,
        var email: String = "",
        var nickname: String? = null,
        var password: String = "",
        var inviterID: Long? = null,
        var registerTime: Date = Date(),
        var registerIP: String = "",
        var activeTime: Date = Date(),
        var activeIP: String = "",

        /**
         * -1:未初始化
         * 0:正常
         * 1:锁定
         * 2:管理员
         */
        var state: Int = -1
)