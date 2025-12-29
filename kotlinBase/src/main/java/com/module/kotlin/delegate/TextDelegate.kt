package com.tyiot.module.android.delegate

import android.widget.TextView
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class TextDelegate(private val textView: () -> TextView) : ReadWriteProperty<Any?, CharSequence?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): CharSequence? {
        return textView.invoke().text
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: CharSequence?) {
        textView.invoke().text = value
    }
}

