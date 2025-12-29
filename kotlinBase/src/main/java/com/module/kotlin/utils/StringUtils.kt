package com.module.kotlin.utils

/**
 * 判断参数值是否超出上下限范围
 * @param paramValue 参数值
 * @param upperLimit 上限值
 * @param lowerLimit 下限值
 * @return true表示超出范围，false表示在范围内或无法判断
 */
fun String.isValueOutOfRange(upperLimit: String?, lowerLimit: String?): Boolean {
    // 如果参数值为空，则无法判断
    if (this.isEmpty()) return false

    return try {
        val value = this.toFloat()
        var isOutOfRange = false

        // 检查上限（如果不为空）
        if (!upperLimit.isNullOrEmpty()) {
            val upper = upperLimit.toFloat()
            if (value > upper) {
                isOutOfRange = true
            }
        }

        // 检查下限（如果不为空）
        if (!lowerLimit.isNullOrEmpty()) {
            val lower = lowerLimit.toFloat()
            if (value < lower) {
                isOutOfRange = true
            }
        }

        isOutOfRange
    } catch (e: NumberFormatException) {
        // 转换失败则认为没有超出范围
        false
    }
}