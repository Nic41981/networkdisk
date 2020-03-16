package com.dy.networkdisk.user.tool

import com.dy.networkdisk.api.dto.mq.email.AccountActiveDTO
import com.dy.networkdisk.api.dto.dubbo.user.GuestsDTO

class BeanTransUtil {
    companion object{

        @JvmStatic
        fun trans(from: GuestsDTO, to: AccountActiveDTO): AccountActiveDTO {
            with(to){
                username = from.username
                email = from.email
                token = from.lock
                ip = from.ip
                registerDate = from.registerDate
            }
            return to
        }
    }
}