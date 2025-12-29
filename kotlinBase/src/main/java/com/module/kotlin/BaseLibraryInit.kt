package com.module.kotlin

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.startup.Initializer
import com.module.kotlin.crash.CrashHelper
import com.module.kotlin.utils.MvvmActivityLifecycleCallbacksUtil
import com.module.kotlin.utils.android.AppBackgroundRun

/**
 * 自动初始化
 */
class BaseLibraryInit : Initializer<Application> {
    override fun create(context: Context): Application {
        val app = context as Application
        BaseGlobalConst.initInternalApp(app)
        //监听程序生命周期
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppBackgroundRun)
        app.registerActivityLifecycleCallbacks(MvvmActivityLifecycleCallbacksUtil())
        //崩溃拦截
        CrashHelper.initCrash()
        return app
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}