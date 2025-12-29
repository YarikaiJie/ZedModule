package com.module.kotlin.crash

import android.os.Process
import com.module.kotlin.BaseGlobalConst
import com.module.kotlin.filelog.doOnlyDebug
import com.module.kotlin.filelog.logE
import com.module.kotlin.filelog.logException
import kotlin.system.exitProcess

/**
 * 全局异常捕获
 */
object CrashHelper : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        logE("app ===uncaughtException Crash===")
        logException(Exception(e))
        UncaughtExceptionActivity.start(BaseGlobalConst.app, t, e)
        Process.killProcess(Process.myPid())
        exitProcess(-1)
    }

    fun initCrash() {
        doOnlyDebug {
            Thread.setDefaultUncaughtExceptionHandler(CrashHelper)
        }
    }
}