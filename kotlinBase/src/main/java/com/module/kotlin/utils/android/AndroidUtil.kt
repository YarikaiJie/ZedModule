package com.module.kotlin.utils.android

/**
 * @Description:
 * @Author: zed_qiu
 * @CreateDate: 2025/11/28 18:15
 */

/**
 * 类型转换
 */
inline fun <reified Obj> Any?.asOrNull(): Obj? {
    return if (this is Obj) {
        this
    } else {
        null
    }
}