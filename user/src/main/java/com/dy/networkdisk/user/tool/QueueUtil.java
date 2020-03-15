package com.dy.networkdisk.user.tool;

import org.apache.activemq.command.ActiveMQQueue;

import javax.annotation.PostConstruct;
import javax.jms.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class QueueUtil {

    private ConcurrentHashMap<String,Queue> queueMap;

    /*激活邮件队列*/
    public static final String MAIL_ACCOUNT_ACTIVE_QUEUE = "email.account.active";

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
