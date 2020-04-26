package com.dy.networkdisk.web.tool

import com.dy.networkdisk.api.config.CommonConst
import com.dy.networkdisk.web.bean.QYSessionInfo
import com.dy.networkdisk.web.config.Const
import javax.servlet.http.HttpServletRequest

val HttpServletRequest.sessionID: Long
    get() {
        return (getAttribute(Const.SESSION_KEY) ?: error("")) as Long
    }

val HttpServletRequest.sessionInfo: QYSessionInfo
    get() {
        val sessionKey = "${CommonConst.FUNC_SESSION_REDIS_KEY}:${sessionID}"
        val value: String? = RedisTemplateHolder.INSTANCE.opsForHash<String,String>()[sessionKey,"info"]
        return value.fromJson<QYSessionInfo>() ?: error("")
    }