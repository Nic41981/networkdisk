package com.dy.networkdisk.web.tool

import com.dy.networkdisk.api.config.QueueConst
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct

enum class Level{
    DEBUG,
    INFO,
    WARNING,
    ERROR
}

class Log(private val level: Level){
    val serviceName = "web"
    var time: Date = Date()
    var threadName: String = ""
    var className: String = ""
    var methodName: String = ""
    var lineNumber: Int = -1
    var message: String = ""
    var tip: String = ""

    fun local(){
        val timeString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(time)
        val indent = level.name.length + 2
        println("[${level.name}]${timeString}@${threadName}")
        repeat(indent){ print(" ") }
        print("${className}.${methodName}[${lineNumber}]")
        if (message.isNotBlank()){
            print(":${message}")
        }
        println()
        if (tip.isNotBlank()){
            repeat(level.name.length) { print(" ")}
            println(tip)
        }
    }

    fun remote(){
        val template = RemoteHolder.jmsTemplate
        val queue = QueueUtil.getQueue(QueueConst.logRemote)
        val message = GsonUtil.toJson(this)
        template.convertAndSend(queue, message)
    }
}

@Component
class RemoteHolder @Autowired constructor(
        private val mJmsTemplate: JmsTemplate
) {

    companion object{
        lateinit var jmsTemplate: JmsTemplate
    }

    @PostConstruct
    fun init(){
        jmsTemplate = mJmsTemplate
    }
}

fun Any.debug(message: String = "",tip: String = ""): Log{
    val thread = Thread.currentThread()
    val position = thread.stackTrace[2]
    return Log(Level.DEBUG).apply {
        this.threadName = thread.name
        this.className = position.className
        this.methodName = position.methodName
        this.lineNumber = position.lineNumber
        this.message = message
        this.tip = tip
    }
}
