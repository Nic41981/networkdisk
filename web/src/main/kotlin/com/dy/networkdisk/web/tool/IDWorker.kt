package com.dy.networkdisk.web.tool

import com.dy.networkdisk.api.config.IDWorkerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class IDWorker{
    //服务ID
    private val serviceID = IDWorkerConfig.WEB_MODULE.id

    //服务器ID
    @Value("\${QYDisk.id-worker.workerID}")
    private val workerID: Long = 0

    //序列号
    private var sequence = 0L

    //起始时间(2020-01-01 00:00:00)
    private val startTimestamp = 1577808000000L

    //服务ID长度
    private val serviceIDBits = 5L

    //主机ID长度
    private val workerIDBits = 5L

    //序列号长度
    private val sequenceBits = 11L

    //主机ID左移长度
    private val workerIDShift = sequenceBits

    //服务ID左移长度
    private val serviceIDShift = sequenceBits + workerIDBits

    //时间戳左移长度
    private val timestampLeftShift = sequenceBits + workerIDBits + serviceIDBits

    //序列号掩码
    private val sequenceMask = (-1L shl sequenceBits.toInt()).inv()

    //上次记录的时间
    private var lastTimestamp = -1L

    @Synchronized
    fun nextId(): Long {
        var timestamp = System.currentTimeMillis()
        if (timestamp < lastTimestamp) {
            throw RuntimeException("发生时钟回拨,当前时间:${timestamp},上次记录时间:${lastTimestamp}")
        }
        if (lastTimestamp == timestamp) {
            sequence = sequence + 1 and sequenceMask
            if (sequence == 0L) {
                timestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0
        }
        lastTimestamp = timestamp
        return timestamp - startTimestamp shl timestampLeftShift.toInt() or
                (serviceID shl serviceIDShift.toInt()) or
                (workerID shl workerIDShift.toInt()) or sequence
    }

    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}