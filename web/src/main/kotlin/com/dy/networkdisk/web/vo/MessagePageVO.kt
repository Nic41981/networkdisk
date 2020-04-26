package com.dy.networkdisk.web.vo

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class MessagePageVO(
        val title: String,
        val content: ArrayList<String> = arrayListOf(),
        var jump: JumpInfo? = null
) {

    companion object{
        const val NAME = "message"
    }

    data class JumpInfo(val name: String, val url: String)

    fun invalidOperation(operation: String, reason: String) = this.apply {
        content.add("系统检测到您的${operation}操作存在异常，已经中断执行并通知管理员。")
        content.add("原因：${reason}")
    }

    fun notAllowRegister() = this.apply {
        content.add("管理员未开启注册功能。")
    }

    fun hasRegister() = this.apply {
        content.add("该邮箱已注册，您可以直接登录或者尝试找回密码。")
    }

    fun activeEmail(expire: Int) = this.apply {
        content.add("激活邮件将发送至您的邮箱。")
        content.add("游客账号无法使用网盘功能，账号信息仅会保存${expire}个小时，请及时激活您的账号。")
    }

    fun activeSuccess() = this.apply {
        content.add("您的账号已经激活成功，希望您使用愉快。")
    }

    fun activeFail() = this.apply {
        content.add("激活信息不存在或已失效,请重新注册。")
    }

    fun requestError(code: Int, reason: String) = this.apply {
        when(code){
            404 -> {
                content.add("很抱歉，页面离家出走了……")
            }
            500 -> {
                content.add("很抱歉，程序猿自闭了……")
            }
            else -> {
                content.add("很抱歉，服务器闹矛盾了……")
                content.add("异常代码：${code}")
                content.add("异常信息：${reason}")
            }
        }
    }

    fun withTime() = this.apply {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        content.add("时间：${time}")
    }

    fun jumpTo(name: String, url: String) = this.apply {
        jump = JumpInfo(name,url)
    }
}