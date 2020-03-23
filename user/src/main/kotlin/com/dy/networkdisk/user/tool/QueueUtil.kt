package com.dy.networkdisk.user.tool

import org.apache.activemq.command.ActiveMQQueue
import java.util.concurrent.ConcurrentHashMap
import javax.jms.Queue

class QueueUtil {
    companion object{
        private val queueMap = ConcurrentHashMap<String, Queue>()

        fun getQueue(name: String):Queue {
            return queueMap.getOrPut(name){
                ActiveMQQueue(name)
            }
        }
    }
}