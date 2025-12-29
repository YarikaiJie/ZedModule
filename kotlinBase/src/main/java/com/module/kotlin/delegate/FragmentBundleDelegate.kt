package com.module.kotlin.delegate

import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentBundleDelegate<T>(private val key: String, private val defaultValue: T) :
    ReadOnlyProperty<Fragment, T> {
    private var temp: T? = null
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val value = temp
        if (value != null) {
            return value
        }
        val newValue = thisRef.arguments?.get(key) ?: return defaultValue
        return newValue as T
    }
}