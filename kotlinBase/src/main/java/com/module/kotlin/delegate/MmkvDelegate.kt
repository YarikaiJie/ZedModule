package com.module.kotlin.delegate

import com.module.kotlin.mvvm.mmkvGetValue
import com.module.kotlin.mvvm.mmkvPutValue
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 属性托管
 */
class MmkvDelegate<T:Any>(
    private val key: String,
    private val defaultValue: T
) : ReadWriteProperty<Any?, T> {
    //使用临时变量，防止每次都访问mmkv
    private var temp: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val lastValue = temp
        return if (lastValue == null) {
            val mmkvValue = mmkvGetValue(key, defaultValue)
            temp = mmkvValue
            mmkvValue
        } else {
            lastValue
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (mmkvPutValue(key, value)) {
            temp = value
        }
    }
}