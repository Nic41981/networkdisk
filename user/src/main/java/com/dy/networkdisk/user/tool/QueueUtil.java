package com.dy.networkdisk.user.tool;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QueueUtil {

    private ConcurrentHashMap<String,Queue> queueMap;

    @PostConstruct
    private void init(){
        queueMap = new ConcurrentHashMap<>();
    }

    public Queue get(String name){
        Queue queue = queueMap.get(name);
        if (queue == null){
            queue = new ActiveMQQueue(name);
            queueMap.put(name,queue);
        }
        return queue;
    }
}
