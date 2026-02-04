package com.module.kotlin.utils

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.Locale
import kotlin.experimental.and
import kotlin.math.pow

object ByteUtil {
    /**
     * c无符号的值，转换成java-int值
     */
    fun byteToUnsignedInt(byteNum: Byte): Int {
        return byteNum.toInt() and 0xff
    }

    /**
     *  功能描述： 字节转16进制字符
     **/
    fun byteToHex(b: Byte): String {
        return String.format("%02x", b).uppercase(Locale.ENGLISH)
    }

    fun byteArrayToInt(byteArray: ByteArray): Int {
        require(byteArray.size <= 4) { "ByteArray 长度不能超过 4 字节" }
        var result = 0
        for (i in byteArray.indices) {
            result = result shl 8 or (byteArray[i].toInt() and 0xFF)
        }
        return result
    }

    fun byteArrayToInt(byteArray: ByteArray, range: IntRange): Int {
        val startIndex = range.first
        val endIndex = range.last
        
        // 验证范围下标不能超过数组长度
        require(startIndex >= 0) { "起始下标不能小于 0" }
        require(endIndex < byteArray.size) { "结束下标不能超过数组长度" }
        
        val rangeSize = endIndex - startIndex + 1
        // 验证范围差值不能大于 4
        require(rangeSize <= 4) { "范围长度不能超过 4 字节" }
        
        var result = 0
        for (i in startIndex..endIndex) {
            result = result shl 8 or (byteArray[i].toInt() and 0xFF)
        }
        return result
    }

    /**
     * 字节数组转成字符串（源必须是字符串，最终可能携带\u0000，所以要去除）
     */
    fun byteArrayToStringFormat(byteArray: ByteArray): String {
        return String(byteArray.dropWhile { it == 0.toByte() }.toByteArray()).replace("\u0000", "")
    }

    /**
     * 字符串转成字节数组，不够要补上0x00
     */
    fun stringToByteArrayWithPadding(text: String, length: Int): ByteArray {
        val byteArray = ByteArray(length)

        // 将字符串转换为字节数组
        val textBytes = text.toByteArray(Charsets.UTF_8)

        // 复制文本内容到目标数组
        val copyLength = minOf(textBytes.size, length)
        System.arraycopy(textBytes, 0, byteArray, 0, copyLength)

        // 剩余部分用 null 字符(0x00)填充
        for (i in copyLength until length) {
            byteArray[i] = 0x00
        }

        return byteArray
    }

    /**
     * 十六进制打印数组
     */
    fun getHexString(buffer: ByteArray?): String {
        if (buffer == null || buffer.isEmpty()) return ""
        val sb = StringBuilder()
        for (b in buffer) {
            val s = byteToHex(b)
            sb.append(s)
        }
        return sb.toString()
    }

    fun checkByteArrayStrValid(buffer: ByteArray?, expect: String): Boolean {
        if (buffer == null || buffer.isEmpty()) return false
        val str = getHexString( buffer)
        return str.equals(expect, true)
    }

    fun byteToCharSequence(buffer: ByteArray?): String {
        if (buffer == null || buffer.isEmpty()) {
            return ""
        }
        var sendString = ""
        try {
            sendString = String(buffer, Charset.forName("gbk"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return sendString
    }

    /**
     * 把16进制字符串转换成字节数组
     *
     * @param hex * @return
     */
    fun hexStringToByte(hex: String?): ByteArray? {
        if (hex.isNullOrBlank()) return null

        val cleanHex = hex.trim().uppercase(Locale.ENGLISH)
        val length = cleanHex.length

        // 检查长度和格式
        if (length % 2 != 0) throw IllegalArgumentException("Hex string must have even length")
        if (!cleanHex.matches(Regex("^[0-9A-F]*$"))) throw IllegalArgumentException("Hex string contains invalid characters")

        val result = ByteArray(length / 2)
        val hexChars = cleanHex.toCharArray()

        for (i in result.indices) {
            val pos = i * 2
            val high = Character.digit(hexChars[pos], 16)
            val low = Character.digit(hexChars[pos + 1], 16)

            if (high == -1 || low == -1) {
                throw IllegalArgumentException("Invalid hex character at position $pos")
            }

            result[i] = (high shl 4 or low).toByte()
        }

        return result
    }

    fun intToTwoBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value shr 8).toByte(),  // 高字节
            value.toByte()           // 低字节
        )
    }

    /**
     * 整数转四字节数组
     * @param value 整数值
     * @return 四字节数组
     */
    fun intToCountBytes(value: Int, count: Int = 4): ByteArray {
        require(count in 1..10) { "count must be between 1 and 10" }
        val result = ByteArray(count)
        // count 为 1-4 时，高位在前，从高字节开始取
        val shiftCount = (count - 1) * 8
        for (i in 0 until count) {
            result[i] = (value shr (shiftCount - i * 8)).toByte()
        }
        return result
    }

    // 字符串转字节数组，用于固定长度字节数组转换，如密码
    fun stringToByteArrayWithLength(str: String, length: Int): ByteArray {
        val originalBytes = str.toByteArray(Charsets.UTF_8)
        return originalBytes.copyOf(length)
    }


    private fun toByte(c: Char): Byte {
        return HEX_CHARS.indexOf(c).toByte()
    }

    private const val HEX_CHARS = "0123456789ABCDEF"

    private val powMap = hashMapOf<Int, Int>(
        1 to (256.0.pow(1)).toInt(),
        2 to (256.0.pow(2)).toInt(),
        3 to (256.0.pow(3)).toInt(),
        4 to (256.0.pow(4)).toInt(),
    )

    /**
     * c-byte数组转换成java-int值 <br></br> 高位在前 <br></br> [24 - 16 - 08 - 00]
     *
     * @param buffer byte数组
     * @param index  转换开始位置
     * @param len    转换的长度
     */
    fun ByteArray.toIntHighBefore(index: Int, len: Int): Int {
        require(index + len > this.size){"ByteArray 下标超出"}
        require( len > 4){"长度最大长度为4"}
        var value = 0
        var i = 0
        // 反向左移变量
        var j = len - 1
        val isPositive = this[index].toInt() >= 0
        while (i < len) {
            val byteNum = this[i + index]
            // byteToUnsignedInt（）转成无符号数，再进行移位
            value += (byteToUnsignedInt(byteNum) shl (j * 8))
            i++
            j--
        }
        return if (isPositive) value else -(powMap[len]!! - value)
    }

    /**
     * c-byte数组转换成java-int值 <br></br> 低位在前 <br></br>
     *
     * @param buffer byte数组
     * @param index  转换开始位置
     * @param len    转换的长度
     */
    fun ByteArray.toIntLowBefore(index: Int, len: Int): Int {
        require(index + len > this.size){"ByteArray 下标超出"}
        require( len > 4){"长度最大长度为4"}
        var value = 0
        var i = 0
        // 正向左移变量
        var j = 0
        val isPositive = this[index + len - 1].toInt() >= 0
        while (i < len) {
            val byteNum = this[i + index]
            // byteToUnsignedInt（）转成无符号数，再进行移位
            value += (byteToUnsignedInt(byteNum) shl (8 * i))
            i++
            j++
        }
        return if (isPositive) value else -(powMap[len]!! - value)
    }

    /**
     *  功能描述：根据int值转成2byte
     *  @param value 对应的int值
     *  @param hFirst 是否高位在前
     **/
    fun getIntToBytes(value: Int, hFirst: Boolean = true): ByteArray {
        val src = ByteArray(2)
        if (hFirst) {
            src[0] = (value shr 8 and 0xFF).toByte()
            src[1] = (value and 0xFF).toByte()
        } else {
            src[0] = (value and 0xFF).toByte()
            src[1] = (value shr 8 and 0xFF).toByte()
        }
        return src
    }

    /**
     * 描述： 取某一位
     * @param i 位置是高位在前，如果要按顺序取，得从7 - 0
     */
    fun getBit(b: Int, i: Int): Int {
        return (b shr i and 0x1)
    }

    fun Byte.toBinaryString(): String {
        val bt = this and 0xFF.toByte()
        return Integer.toBinaryString(bt + 0x100).substring(1)
    }

    fun String.binaryToInt(): Int = Integer.parseInt(this, 2)

    // 简化二进制位读取
    fun Byte.getBits(range: IntRange): Int = this.toBinaryString().substring(range).binaryToInt()

    /**
    * 描述： 取某一位
    * @param position 位置是高位在前，如果要按顺序取，得从7 - 0
    */
    fun Byte.getBit(position: Int): Int = (this.toInt() shr position and 0x1)

    fun getCombinedBits(vararg byteRangePairs: Pair<Byte, IntRange>): Int {
        val combinedBits = StringBuilder()
        for ((byte, range) in byteRangePairs) {
            combinedBits.append(byte.toBinaryString().substring(range))
        }
        return combinedBits.toString().binaryToInt()
    }

}