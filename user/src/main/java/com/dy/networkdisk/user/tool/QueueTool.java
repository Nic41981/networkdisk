package com.dy.networkdisk.user.tool;

import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.Queue;
import java.util.HashMap;

public class QueueTool {

    private static HashMap<String,Queue> queueMap;

    public static Queue get(String name){
        if (queueMap == null){
            queueMap = new HashMap<>();
        }
        Queue queue = queueMap.get(name);
        if (queue == null){
            queue = new ActiveMQQueue(name);
            queueMap.put(name,queue);
        }
        return queue;
    }
}
