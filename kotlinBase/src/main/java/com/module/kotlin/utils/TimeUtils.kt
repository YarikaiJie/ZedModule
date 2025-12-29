package com.module.kotlin.utils

import com.module.kotlin.R
import com.module.kotlin.utils.ByteUtil.checkByteArrayStrValid

object TimeUtils {
    fun getYMDHMSStr(byteArray: ByteArray?, invalidStr: String = "00"): String {
        val length = byteArray?.size ?: 0
        val builder = StringBuilder()
        for (i in 0 until length) {
            builder.append(invalidStr)
        }
        return if (checkByteArrayStrValid(byteArray, builder.toString())) {
            "--"
        } else {
            getStartTimeStr(byteArray)
        }
    }

    fun getStartTimeStr(timeData: ByteArray?): String {
        if (timeData == null || timeData.size < 7) return  ""
        val year = (timeData[0].toInt() + 2000).toString()
        val month: Int = timeData[1].toInt()
        val monthStr: String?
        if (month < 10) {
            monthStr = "0$month"
        } else {
            monthStr = "" + month
        }
        val week = timeData[2].toInt().toString()
        val day: Int = timeData[3].toInt()
        val dayStr = if (day < 10) {
            "0$day"
        } else {
            "" + day
        }
        val hour: Int = timeData[4].toInt()
        val hourStr = if (hour < 10) {
            "0$hour"
        } else {
            "" + hour
        }
        val minute: Int = timeData[5].toInt()
        val minuteStr = if (minute < 10) {
            "0$minute"
        } else {
            "" + minute
        }
        val second: Int = timeData[6].toInt()
        val secondStr = if (second < 10) {
            "0$second"
        } else {
            "" + second
        }
        val sb = java.lang.StringBuilder()
        sb.append(year).append("-").append(monthStr).append("-").append(dayStr).append(" ")
            .append(hourStr).append(":").append(minuteStr).append(":").append(secondStr)

        return sb.toString()
    }

    fun parseTimeHMSplit(timeStr: String): Pair<Int, Int> {
        val parts = timeStr.split(
            getStrRes(R.string.logger_hour),
            getStrRes(R.string.logger_minute)
        )
        if (parts.size >= 2) {
            val hour = parts[0].trim().toInt()
            val minute = parts[1].trim().toInt()
            return Pair(hour, minute)
        }
        return Pair(0, 0)
    }

    fun mSStrToPair(timeStr: String): Pair<Int, Int> {
        val minuteTag = getStrRes(R.string.logger_minute)
        val secondTag = getStrRes(R.string.logger_second)
        val src = timeStr.trim()
        if (src.isEmpty()) return Pair(0, 0)

        val hasMin = src.contains(minuteTag)
        val hasSec = src.contains(secondTag)

        // 只有“秒”的特殊场景："30秒" → (0, 30)
        if (hasSec && !hasMin) {
            val secVal = src.replace(secondTag, "").trim().toIntOrNull() ?: 0
            return Pair(0, secVal)
        }

        // 同时包含“分”和“秒”的常规场景："2分5秒" → (2, 5)
        if (hasMin && hasSec) {
            val parts = src.split(minuteTag, secondTag)
            if (parts.size >= 2) {
                val minVal = parts[0].trim().toIntOrNull() ?: 0
                val secVal = parts[1].trim().toIntOrNull() ?: 0
                return Pair(minVal, secVal)
            }
        }

        // 仅“分”的场景："5分" → (5, 0)
        if (hasMin && !hasSec) {
            val minVal = src.replace(minuteTag, "").trim().toIntOrNull() ?: 0
            return Pair(minVal, 0)
        }
        return Pair(0, 0)
    }

    /**
     * 输入秒，输出“X分Y秒”的本地化字符串
     * 说明：与 parseTimeMSSplit 使用同一套字符串资源，保证互相兼容
     */
    fun secondsToMSStr(seconds: Int): String {
        // 负数防御：统一视为 0 秒，避免出现“-1分-30秒”这类异常展示
        val safeSec = if (seconds < 0) 0 else seconds
        val minute = safeSec / 60
        val sec = safeSec % 60
        // 使用资源串，确保与 parseTimeMSSplit 的分隔一致（便于双向转换）
        return buildString {
            if (minute > 0) {
                append(minute)
                append(getStrRes(R.string.logger_minute))
            }
            append(sec)
            append(getStrRes(R.string.logger_second))
        }
    }
}
