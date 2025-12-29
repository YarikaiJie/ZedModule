package com.module.kotlin.utils.android

import android.util.Log
import com.module.kotlin.BaseGlobalConst.activityList
import com.module.kotlin.iInterface.ILifecycleObserver


/**
 * app是否后台运行监听
 */
object AppBackgroundRun : ILifecycleObserver {
    /**
     * false表示在前台；true不在前台
     */
    val isBackgroundRun: Boolean
        get() = _isBackgroundRun

    //是否后台运行
    private var _isBackgroundRun = false
    private val listeners = mutableListOf<Function1<@ParameterName("isBackground") Boolean, Unit>>()

    fun addOnBackgroundListener(listener: Function1<@ParameterName("isBackground") Boolean, Unit>) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeOnBackgroundListener(listener: Function1<@ParameterName("isBackground") Boolean, Unit>) {
        listeners.remove(listener)
    }

    fun clearOnBackgroundListener() {
        listeners.clear()
    }

    override fun onStart() {
        super.onStart()
        if (isBackgroundRun) {
            _isBackgroundRun = false
            notifyListener()
        }
    }

    override fun onStop() {
        super.onStop()
        _isBackgroundRun = activityList.isNotEmpty()
        if (isBackgroundRun) {
            notifyListener()
        }
    }

    private fun notifyListener() {
        val run = isBackgroundRun
        Log.d("app", "notify current run: $run")
        listeners.forEach {
            it.invoke(run)
        }
    }
}