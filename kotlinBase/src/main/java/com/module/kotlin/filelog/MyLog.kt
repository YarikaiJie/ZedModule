package com.module.kotlin.filelog

import android.os.Looper
import com.module.kotlin.BaseLibraryConfig
import com.module.kotlin.BuildConfig

private const val TAG:String = "jc_np"


/**
 * E日志强制输出。并落盘。
 */
fun logE(s:String, tag:String="") {
    LogUtils.eFull(TAG, "$tag: $s")
    if (LogFileManager.isNeedLogFile){
        FileLog.write("$tag: $s")
    }
}

fun logException(exception: Exception, tag:String="") {
    exception.printStackTrace()
    val sb = StringBuilder()
    sb.append(exception.message).append("\n").append(exception.cause).append("\n")
    for (element in exception.stackTrace) {
        sb.append(element.toString()).append(System.lineSeparator())
    }
    val str = sb.toString()
    if (LogFileManager.isNeedLogFile){
        FileLog.write("$tag: $str")
    }
}

fun logW(s:String, tag:String="") {
    LogUtils.wFull(TAG, "$tag: $s")
    if (LogFileManager.isNeedLogFile){
        FileLog.write("$tag: $s")
    }
}


inline fun <RESULT> doOnlyDebug(block: () -> RESULT): RESULT? {
    if (BuildConfig.DEBUG || BaseLibraryConfig.ALWAYS_DEBUG ) {
        return block.invoke()
    }
    return null
}

fun logD(s:String, tag:String="", realTag:String? = null) {
    if (BuildConfig.DEBUG || LogFileManager.alwaysDebug) {
        LogUtils.dFull(realTag?: TAG, "$tag: $s")
    }
    if (LogFileManager.isNeedLogFile){
        FileLog.write("$tag: $s")
    }
}

fun logI(s:String, tag:String="", realTag:String? = null) {
    if (BuildConfig.DEBUG || LogFileManager.alwaysDebug) {
        LogUtils.iFull(realTag?: TAG, "$tag: $s")
    }
    if (LogFileManager.isNeedLogFile){
        FileLog.write("$tag: $s")
    }
}


fun logT(s:String, tag:String="", realTag: String? = null) {
    if (BuildConfig.DEBUG || LogFileManager.alwaysDebug) {
        LogUtils.iFull(realTag?: TAG, "$tag: thread${Thread.currentThread().id}: $s")
    }
}

fun logM(s:String, tag:String="") {
    if (BuildConfig.DEBUG || LogFileManager.alwaysDebug) {
        val isMainThread = (Thread.currentThread().id == Looper.getMainLooper().thread.id)
        LogUtils.dFull(TAG, "$tag: isMain($isMainThread): $s")
    }
}