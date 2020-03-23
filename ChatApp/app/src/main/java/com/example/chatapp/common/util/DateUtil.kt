package com.example.chatapp.common.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    fun curDate(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        return sdf.format(date)
    }

}