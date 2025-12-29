package com.module.kotlin

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object BaseLibraryConfig {
    const val ALWAYS_DEBUG = false
    /**
     * app主动关闭的回调
     */
    var appDestroyedListener: Function0<Unit>? = null

    /**
     * 全局的gson
     */
    var onGsonBuilder: Function1<GsonBuilder, Gson>? = null
}