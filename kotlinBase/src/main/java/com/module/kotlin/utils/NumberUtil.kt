package com.module.kotlin.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.text.format.DateUtils
import android.util.Base64
import android.util.Log
import androidx.core.text.isDigitsOnly
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.module.kotlin.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * 字符串转double
 */
val String?.double: Double?
    get() = ignoreError { this?.toDoubleOrNull() }

/**
 * 字符串转float
 */
val String?.float: Float?
    get() = ignoreError { this?.toFloatOrNull() }

/**
 * 字符串转bigDecimal
 */
val String?.bigDecimal: BigDecimal?
    get() = ignoreError { this?.toBigDecimalOrNull() }


/**
 * 最大时间为一周 显示为（七天前）
 * 否则显示年月日
 */
fun getRelativeTimeSpanString(
    startTime: Long,
    endTime: Long = System.currentTimeMillis(),
    minResolution: Long = DateUtils.SECOND_IN_MILLIS/*默认最小精度单位为秒*/
): CharSequence {
    return DateUtils.getRelativeTimeSpanString(startTime, endTime, minResolution)
}

/**
 *
 * Formats an elapsed time in the form "MM:SS" or "H:MM:SS"
 * for display on the call-in-progress screen.
 * @param elapsedSeconds the elapsed time in seconds.
 * DateUtils:安卓提供的工具类
 */
fun formatElapsedTime(elapsedSeconds: Long): String {
    return DateUtils.formatElapsedTime(elapsedSeconds)
}

/**
 * 指定起始颜色，获取中间颜色
 */
fun getColorByFraction(
    fraction: Float,
    startColor: Int,
    endColor: Int,
): Int {
    return ArgbEvaluatorCompat.getInstance().evaluate(fraction, startColor, endColor)
}

/**
 * 毫秒换成00:00:00
 */
//一小时的毫秒数
const val ONE_HOUR_OF_MILLISECOND = 1 * 60 * 60 * 1000L

/**
 * 毫秒换成00:00:00
 */
fun getCountTimeByLong(
    finishTime: Long,
    isNeedHour: Boolean = finishTime > ONE_HOUR_OF_MILLISECOND/*大于1小时则显示*/,
    splitHourMinStr: String = ":",
    splitMinSecStr: String = ":"
): String {
    var totalTime = (finishTime / 1000).toInt()//秒
    var hour = 0
    var minute = 0
    var second = 0
    if (3600 <= totalTime) {
        hour = totalTime / 3600
        totalTime -= 3600 * hour
    }
    if (60 <= totalTime) {
        minute = totalTime / 60
        totalTime -= 60 * minute
    }
    if (0 <= totalTime) {
        second = totalTime
    }
    val sb = StringBuilder()
    if (isNeedHour) {
        if (hour < 10) {
            sb.append("0").append(hour).append(splitHourMinStr)
        } else {
            sb.append(hour).append(splitHourMinStr)
        }
    }
    if (minute < 10) {
        sb.append("0").append(minute).append(splitMinSecStr)
    } else {
        sb.append(minute).append(splitMinSecStr)
    }
    if (second < 10) {
        sb.append("0").append(second)
    } else {
        sb.append(second)
    }
    return sb.toString()
}

fun Int.secToDurationStr(): String {
    if (this <= 0) return "00d 00m 00h 00s"

    val days = this / 86400
    val hours = (this % 86400) / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    val parts = mutableListOf<String>()
    /*if (days > 0) parts.add("${days}d")
    if (hours > 0) parts.add("${hours}h")
    if (minutes > 0) parts.add("${minutes}m")
    if (seconds > 0 || parts.isEmpty()) parts.add("${seconds}s")*/

    if (days > 0) {
        if (days > 9) {
            parts.add("${days}d")
        } else {
            parts.add("0${days}d")
        }
    } else {
        parts.add("00d")
    }
    parts.add(" ")
    if (hours > 0) {
        if (hours > 9) {
            parts.add("${hours}h")
        } else {
            parts.add("0${hours}h")
        }
    } else {
        parts.add("00h")
    }
    parts.add(" ")
    if (minutes > 0) {
        if (minutes > 9) {
            parts.add("${minutes}m")
        } else {
            parts.add("0${minutes}m")
        }
    } else {
        parts.add("00m")
    }
    parts.add(" ")
    if (seconds > 0 || parts.isEmpty()) {
        if (seconds > 9) {
            parts.add("${seconds}s")
        } else {
            parts.add("0${seconds}s")
        }
    }

    return parts.joinToString("")
}

fun Int.secToDurationStr2(): String {
    if (this <= 0) return "00d 00h 00m"

    val days = this / 86400
    val hours = (this % 86400) / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    val parts = mutableListOf<String>()
    /*if (days > 0) parts.add("${days}d")
    if (hours > 0) parts.add("${hours}h")
    if (minutes > 0) parts.add("${minutes}m")*/
    if (days > 0) {
        if (days > 9) {
            parts.add("${days}d")
        } else {
            parts.add("0${days}d")
        }
    } else {
        parts.add("00d")
    }
    parts.add(" ")
    if (hours > 0) {
        if (hours > 9) {
            parts.add("${hours}h")
        } else {
            parts.add("0${hours}h")
        }
    } else {
        parts.add("00h")
    }
    parts.add(" ")
    if (minutes > 0) {
        if (minutes > 9) {
            parts.add("${minutes}m")
        } else {
            parts.add("0${minutes}m")
        }
    }

    return parts.joinToString("")
}

fun Int.secToHM(): String {
    if (this <= 0) return "00h 00m"

    val hours = this / 3600
    var minutes = (this % 3600) / 60
    val seconds = this % 60
    if (seconds > 0) minutes++

    val parts = mutableListOf<String>()
    /*if (hours > 0) parts.add("${hours}h")
    if (minutes > 0) parts.add("${minutes}m")*/
    if (hours > 0) {
        if (hours > 9) {
            parts.add("${hours}h")
        } else {
            parts.add("0${hours}h")
        }
    } else {
        parts.add("00h")
    }
    parts.add(" ")
    if (minutes > 0) {
        if (minutes > 9) {
            parts.add("${minutes}m")
        } else {
            parts.add("0${minutes}m")
        }
    }


    return parts.joinToString("")
}

fun Int.getTempUnitString(isApi : Boolean = false): String {
    val value = if (isApi) this -1 else this
    return when (value) {
        0 -> "℃"
        else -> "℉"
    }
}


/**时间戳转日期*/
fun Long.millisToStr(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(this).toString()
}

fun String.strToMillis(pattern: String = "yyyy-MM-dd HH:mm"): Long? {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return try {
        format.parse(this)?.time
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun longTimeToCalendar(time: Long?): Calendar? {
    if (time == null) {
        return null
    }
    val instance = Calendar.getInstance(Locale.getDefault())
    instance.timeInMillis = time
    return instance
}

/**年月日转时间戳*/
fun ymdToLongTime(year: Int, month: Int, day: Int, isFirst: Boolean): Long {
    val calendar = GregorianCalendar()
    if (isFirst) {
        calendar.set(year, month - 1, day, 0, 0)
    } else {
        calendar.set(year, month - 1, day, 23, 59)
    }
    return calendar.timeInMillis
}

/**年月日转时间戳*/
fun ymdHmToLongTime(year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int): Long {
    val calendar = GregorianCalendar()
    calendar.set(year, month - 1, day, hourOfDay, minute)
    return calendar.timeInMillis
}

/**时间戳转 1st MMM*/
fun longTimeToDMMM(time: Long?): String {
    if (time == null) return ""
    val tempCalendar = Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = time
    }
    val dayNumberSuffix = getDayNumberSuffix(tempCalendar[Calendar.DAY_OF_MONTH])
    val dateFormat: DateFormat = SimpleDateFormat(" d'$dayNumberSuffix' MMMM")
    return dateFormat.format(tempCalendar.time)
}

fun getDayNumberSuffix(day: Int): String {
    return if (day in 11..13) {
        "th"
    } else when (day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

/**
 * 字节转为kb
 */
fun Long?.formatLength(): String {
    val size = this?.toFloat() ?: return "0MB"
    return when {
        size < 1024 * 1024 * 1024 -> {//不足1g
            "${(size / 1024 / 1024).keepTwoPoint()}MB"
        }

        else -> {
            "${(size / 1024 / 1024 / 1024).keepTwoPoint()}GB"
        }
    }
}

/**
 * 保留两位小数
 */
fun BigDecimal?.keepTwoPoint(roundingMode: RoundingMode = RoundingMode.HALF_EVEN): String {
    return try {
        this?.setScale(2, roundingMode)?.toString() ?: "0.00"
    } catch (e: Throwable) {
        "0.00"
    }
}

fun BigDecimal?.keepDecimal(
    count: Int,
    roundingMode: RoundingMode = RoundingMode.HALF_EVEN
): BigDecimal? {
    return try {
        this?.setScale(count, roundingMode)
    } catch (e: Throwable) {
        e.printStackTrace()
        this
    }
}

/**
 * byte转为kb/mb
 */
fun Long.byteLengthToString(): String {
    val gb = 1024 * 1024 * 1024
    val mb = 1024 * 1024
    val kb = 1024
    return when {
        this > gb -> {
            (this.toFloat() / gb).keepTwoPoint() + "G"
        }

        this > mb -> {
            (this.toFloat() / mb).keepTwoPoint() + "M"
        }

        this > kb -> {
            (this.toFloat() / kb).keepTwoPoint() + "KB"
        }

        else -> {
            "${this}B"
        }
    }
}

/**
 * 保留两位小数
 */
fun Double?.keepTwoPoint(roundingMode: RoundingMode = RoundingMode.HALF_EVEN): String {
    this ?: return "0.00"
    return this.toString().keepTwoPoint()
}

/**
 * 保留两位小数
 */
fun Float?.keepTwoPoint(roundingMode: RoundingMode = RoundingMode.HALF_EVEN): String {
    this ?: return "0.00"
    return this.toString().keepTwoPoint()
}

/**
 * 保留两位小数
 */
fun String?.keepTwoPoint(roundingMode: RoundingMode = RoundingMode.HALF_EVEN): String {
    return try {
        if (this == null) {
            "0.00"
        } else {
            BigDecimal(this).setScale(2, roundingMode)?.toString() ?: this
        }
    } catch (e: Throwable) {
        "0.00"
    }
}

/**
 * 毫秒四舍五入转为秒
 */
fun millisecondToSecond(millisecond: Long): Long {
    return BigDecimal(millisecond / 1000.0).setScale(0, RoundingMode.HALF_EVEN).toLong()
}

/**
 * 判断某个对象是否是这个类型
 */
inline fun <reified Obj> Any?.asThis(isThis: Obj.() -> Unit) {
    if (this is Obj) {
        isThis(this)
    }
}


/**
 * 比较两个字符串的相识度
 * 核心算法：用一个二维数组记录每个字符串是否相同，如果相同记为0，不相同记为1，每行每列相同个数累加
 * 则数组最后一个数为不相同的总数，从而判断这两个字符的相识度
 */
private fun compare(str: String, target: String): Int {
    val d: Array<IntArray> // 矩阵
    val n = str.length
    val m = target.length
    var i = 0 // 遍历str的
    var j: Int // 遍历target的
    var ch1: Char // str的
    var ch2: Char // target的
    var temp: Int // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
    if (n == 0) {
        return m
    }
    if (m == 0) {
        return n
    }
    d = Array(n + 1) { IntArray(m + 1) }
    // 初始化第一列
    while (i <= n) {
        d[i][0] = i
        i++
    }
    // 初始化第一行
    j = 0
    while (j <= m) {
        d[0][j] = j
        j++
    }
    i = 1
    while (i <= n) {
        // 遍历str
        ch1 = str[i - 1]
        // 去匹配target
        j = 1
        while (j <= m) {
            ch2 = target[j - 1]
            temp =
                if (ch1 == ch2 || ch1.toInt() == ch2.toInt() + 32 || ch1.toInt() + 32 == ch2.toInt()) {
                    0
                } else {
                    1
                }
            // 左边+1,上边+1, 左上角+temp取最小
            d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp)
            j++
        }
        i++
    }
    return d[n][m]
}


/**
 * 获取最小的值
 */
private fun min(oldOne: Int, two: Int, three: Int): Int {
    var one = oldOne
    return if ((if (one < two) one else two.also { one = it }) < three) one else three
}


/**
 * 获取两字符串的相似度
 */
fun getStringSimilarityRatio(str: String, target: String): Float {
    val max = str.length.coerceAtLeast(target.length)
    return 1 - compare(str, target).toFloat() / max
}


/**
 * 如果数字大于1万，怎用万作为单位
 */
fun String?.tenThousandFormat(text: String = "万", decimalCount: Int = 1): String {
    this ?: return ""
    if (!this.isDigitsOnly()) {
        return this
    }
    val doubleValue = this.toDouble()
    val max = 10000.0
    if (doubleValue < max) {
        return this
    }
    val format = BigDecimal.valueOf(doubleValue / max).keepDecimal(decimalCount)?.toString() ?: ""
    return "${format}$text"
}

/**
 *
 * 字符串美化
 *  1234->1，234
 *  1234->123,4
 *  123456->123,456
 */
fun String?.insetSplitStr(
    splitCount: Int = 3/*多少字符之后插入新字符*/,
    splitStr: String = ","/*插入新字符*/,
    startFromEnd: Boolean = true/*是否从末尾开始计算插入*/
): String {
    this ?: return ""
    val numStr = StringBuilder(this)
    val count = numStr.count()
    val n1 = count % splitCount
    val n2 = count / splitCount
    var insetCount = 0
    var insetOff = 0
    val splitStrCount = splitStr.count()
    if (startFromEnd) {
        repeat(n2) {
            when {
                n1 == 0 && it == 0 -> {
                    //这种情况，不做处理
                    // 否则出现 123456->,123,456
                }

                else -> {
                    numStr.insert(n1 + it * splitCount + insetOff, splitStr)
                    insetCount++
                    insetOff = insetCount * splitStrCount
                }
            }
        }
    } else {
        repeat(n2) {
            when {
                n1 == 0 && it == n2 - 1 -> {
                    //这种情况，不做处理
                    // 否则出现 123456->123,456,
                }

                else -> {
                    numStr.insert(splitCount + it * splitCount + insetOff, splitStr)
                    insetCount++
                    insetOff = insetCount * splitStrCount
                }
            }
        }
    }
    return numStr.toString()
}

private val EARTHS_RADIUS = doubleArrayOf(
    6378.1,  // Kilometers
    3963.1676,  // Statue miles
    3443.89849 // Nautical miles
)

/**
 * 根据经纬度计算两个点的距离
 * 单位返回的m还是km待验证
 */
fun getDistanceByLatLng(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double
): Double {
    val lat1 = Math.PI / 180 * startLatitude
    val lat2 = Math.PI / 180 * endLatitude
    val lon1 = Math.PI / 180 * startLongitude
    val lon2 = Math.PI / 180 * endLongitude
    val earthRadius: Double = EARTHS_RADIUS[0] //6371;//Radius of the earth
    val d = (acos(
        sin(lat1) * sin(lat2) + (cos(lat1) * cos(lat2)
                * cos(lon2 - lon1))
    )
            * earthRadius)
    return d * 1000
}

/**
 * 获取年龄
 * 月份按照正常的计算，1-12
 */
fun getAge(year: Int, month: Int, day: Int): Int {
    val currentTime = Calendar.getInstance()
    val yearNow = currentTime.get(Calendar.YEAR)
    val monthNow = currentTime.get(Calendar.MONTH) + 1
    val dayOfMonthNow = currentTime.get(Calendar.DAY_OF_MONTH)
    var age = yearNow - year
    when {
        month == monthNow -> {
            //月份相同去判断日
            if (day >= dayOfMonthNow) {
                age--
            }
        }

        month > monthNow -> {
            age--
        }
    }
    return abs(age)
}

/**
 * 深度拷贝
 */
fun <T : Serializable> T.copyDeep(): T {
    val startTime = System.currentTimeMillis()
    val baos = ByteArrayOutputStream()
    val out = ObjectOutputStream(baos)
    out.writeObject(this)
    out.close()
    val ins = ObjectInputStream(ByteArrayInputStream(baos.toByteArray()))
    val newEvent = ins.readObject()
    ins.close()
    val end = System.currentTimeMillis()
    Log.i("copyDeep", "深度拷贝\"${javaClass.simpleName}\"花费时间：${end - startTime}")
    return newEvent as T
}

fun String.md5(): String {
    try {
        //获取md5加密对象
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        //对字符串加密，返回字节数组
        val digest: ByteArray = instance.digest(this.toByteArray())
        var sb: StringBuffer = StringBuffer()
        for (b in digest) {
            //获取低八位有效值
            var i: Int = b.toInt() and 0xff
            //将整数转化为16进制
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                //如果是一位的话，补0
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

fun String.get32MD5Str(): String {
    var messageDigest: MessageDigest? = null
    try {
        messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(toByteArray(charset("UTF-8")))
    } catch (e: NoSuchAlgorithmException) {
        println("NoSuchAlgorithmException caught!")
        System.exit(-1)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    val byteArray = messageDigest!!.digest()
    val md5StrBuff = StringBuffer()
    for (i in byteArray.indices) {
        if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1) md5StrBuff.append("0")
            .append(
                Integer.toHexString(
                    0xFF and byteArray[i]
                        .toInt()
                )
            ) else md5StrBuff.append(
            Integer.toHexString(
                0xFF and byteArray[i]
                    .toInt()
            )
        )
    }
    return md5StrBuff.toString()
}

/**
 * aes解密
 */
fun String.aesDecrypt(key: String): String {
    return try {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val instance = Cipher.getInstance("AES/ECB/PKCS7Padding")
        instance.init(2, secretKeySpec)
        String(instance.doFinal(Base64.decode(this, 0))).trim()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        this
    }
}

/**
 * aes加密
 */
fun String.aesEncrypt(key: String): String {
    return try {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val instance: Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
        instance.init(1, secretKeySpec)
        Base64.encodeToString(instance.doFinal(this.toByteArray()), 0).trim()
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}


/**
 * 修改动画时长缩放数值
 * 防止作弊
 * android10 无法访问
 */
@SuppressLint("SoonBlockedPrivateApi")
fun disEnableSystemAnimDurationScale(anim: ValueAnimator?) {
    val cls = ValueAnimator::class.java
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        ignoreError {
            val field = cls.getDeclaredField("sDurationScale")
            field.isAccessible = true
            field.set(null, 1.0f)
        }
    } else {
        if (anim != null) {
            ignoreError {
                val field = cls.getDeclaredField("mDurationScale")
                field.isAccessible = true
                field.set(anim, 1.0f)
            }
        }
    }
}

/**
@description 给数字加逗号
 */
fun getCommaNum(str: String): String {
    val format = DecimalFormat()
    format.applyPattern("#,###")
    return format.format(str.toDouble())
}