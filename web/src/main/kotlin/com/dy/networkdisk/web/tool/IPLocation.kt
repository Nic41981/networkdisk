package com.dy.networkdisk.web.tool

import com.google.gson.Gson
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class IPLocation(val ip: String) {

    data class Data(
        val status: String = "fail",
        val message: String = "",
        val country: String = "unknown",
        val regionName: String = "unknown",
        val city: String = "unknown"
    )

    private var date: Data = Data()
    private var job: Job

    init {
        job = GlobalScope.launch(Dispatchers.IO) {
            if (!"unknown".equals(ip, true)) {
                try {
                    val url = URL("http://ip-api.com/json/${ip}" +
                            "?fields=status,message,country,regionName,city" +
                            "&lang=zh-CN")
                    val connection = url.openConnection() as HttpURLConnection
                    with(connection) {
                        requestMethod = "GET"
                        connectTimeout = 5000
                        readTimeout = 2000
                    }
                    if (connection.responseCode == 200) {
                        date = Gson().fromJson(connection.inputStream.reader().readText(), Data::class.java)
                    }
                } catch (e: Exception) {
                    date = Data()
                }
            }
        }
    }

    private fun waitQuery() {
        if (!job.isCompleted) {
            runBlocking {
                job.join()
            }
        }
    }

    private fun isSuccess():Boolean{
        waitQuery()
        return "success".equals(date.message,true)
    }

    fun getLocation():String{
        waitQuery()
        if (isSuccess()){
            return "${date.country}-${date.regionName}-${date.city}"
        }
        return "unknown"
    }

    fun getData():Data{
        waitQuery()
        return date
    }
}