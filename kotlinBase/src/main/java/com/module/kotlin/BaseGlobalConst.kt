package com.module.kotlin

import android.app.Activity
import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.getkeepsafe.relinker.ReLinker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.module.kotlin.utils.MvvmActivityLifecycleCallbacksUtil
import com.tencent.mmkv.MMKV

object BaseGlobalConst {
    /**
     * application对象
     */
    private lateinit var internalApp: Application

    fun initInternalApp(application: Application) {
        if (!this::internalApp.isInitialized) {
            internalApp = application
        }
    }

    val app: Application
        get() = internalApp

    /**
     * activityList集合
     */
    val activityList: List<Activity>
        get() = MvvmActivityLifecycleCallbacksUtil._activityList

    val packageInfo by lazy {
        app.packageManager.getPackageInfoCompat(
            packageName,
            PackageManager.GET_ACTIVITIES
        )
    }

    fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
        }

    /**
     * 当前app的包名
     */
    val packageName by lazy { app.packageName }

    /**
     * 当前的app的versionName
     */
    val versionName by lazy { packageInfo.versionName }

    /**
     * 当前的app的versionCode
     */
    val versionCode by lazy {
        PackageInfoCompat.getLongVersionCode(packageInfo)
    }

    /**
     * 腾讯的数据存储库
     */
    val mmkv by lazy {
        // 尝试： 通过找到一个自行加载
        // 二次保护来解决Mmkv可能在Nexus5X 8.1的crash问题
        try {
            MMKV.initialize(app)
        } catch (e:Exception) {
            e.printStackTrace()
            try {
                val dir: String = app.filesDir.absolutePath + "/mmkv"
                MMKV.initialize(app, dir) { libName -> ReLinker.loadLibrary(app, libName) }
            } catch (e2:Exception) {
                e2.printStackTrace()
            }
        }
        MMKV.defaultMMKV()
    }

    /**
     * gson对象
     */
    val gson: Gson by lazy {
        val builder = GsonBuilder()
        BaseLibraryConfig.onGsonBuilder?.invoke(builder) ?: builder.create()
    }
}