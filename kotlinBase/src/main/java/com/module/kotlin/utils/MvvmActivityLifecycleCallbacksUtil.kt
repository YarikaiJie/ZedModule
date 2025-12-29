package com.module.kotlin.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.module.kotlin.BaseLibraryConfig
import com.module.kotlin.BuildConfig

import com.module.kotlin.filelog.logD
import com.module.kotlin.utils.android.asOrNull


class MvvmActivityLifecycleCallbacksUtil : Application.ActivityLifecycleCallbacks {
    companion object {
        //启动的activity集合
        internal val _activityList = mutableListOf<Activity>()
    }

    private fun print(reason:String) {
        if (!BuildConfig.DEBUG) return
        logD ("activityList======>$reason")
        for (activity in _activityList) {
            val frag = activity.asOrNull<Activity>()?.javaClass?.name
            logD("activityList $activity $frag")
        }
    }

    /*********************Created******************/
    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        setScreenAdaptation(activity)
        super.onActivityPreCreated(activity, savedInstanceState)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        setScreenAdaptation(activity)
        _activityList.add(activity)
        print("onActivity Created")
    }

    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        setScreenAdaptation(activity)
        super.onActivityPostCreated(activity, savedInstanceState)
    }

    /*********************Started******************/
    override fun onActivityPreStarted(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPreStarted(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        setScreenAdaptation(activity)
        val size = _activityList.size
        val index = _activityList.indexOf(activity)
        if (index >= 0 && index != size - 1) {
            _activityList[index] = _activityList[size - 1]
            _activityList[size - 1] = activity
        }
        print("onActivity Started")
    }

    override fun onActivityPostStarted(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPostStarted(activity)
    }

    /*********************Resumed******************/
    override fun onActivityPreResumed(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPreResumed(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        setScreenAdaptation(activity)
    }

    override fun onActivityPostResumed(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPostResumed(activity)
    }

    /*********************Paused******************/
    override fun onActivityPrePaused(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPrePaused(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        setScreenAdaptation(activity)
    }

    override fun onActivityPostPaused(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPostPaused(activity)
    }

    /*********************Stopp******************/
    override fun onActivityPreStopped(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPreStopped(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        setScreenAdaptation(activity)
        val size = _activityList.size
        if (size > 1 && _activityList[size - 1] == activity) {
            _activityList[size - 1] = _activityList[size - 2]
            _activityList[size - 2] = activity
        }
    }

    override fun onActivityPostStopped(activity: Activity) {
        setScreenAdaptation(activity)
        super.onActivityPostStopped(activity)
    }

    /*********************Destroyed******************/
    override fun onActivityPreDestroyed(activity: Activity) {
        super.onActivityPreDestroyed(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        _activityList.remove(activity)
        if (_activityList.count() == 0) {
            BaseLibraryConfig.appDestroyedListener?.invoke()
        }

        print("onActivty Destoryed")
    }

    override fun onActivityPostDestroyed(activity: Activity) {
        super.onActivityPostDestroyed(activity)
    }

    /*********************SaveInstanceState******************/
    override fun onActivityPreSaveInstanceState(activity: Activity, outState: Bundle) {
        setScreenAdaptation(activity)
        super.onActivityPreSaveInstanceState(activity, outState)
    }


    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        setScreenAdaptation(activity)
    }

    override fun onActivityPostSaveInstanceState(activity: Activity, outState: Bundle) {
        setScreenAdaptation(activity)
        super.onActivityPostSaveInstanceState(activity, outState)
    }
}

//是否开启屏幕适配,支持所有activity，字体过大可以调整缩放因子
fun setScreenAdaptation(activity: Activity) {
    //是否开启第三方库的屏幕适配
//    ScreenAdaptationObj.setCustomDensity(
//        activity,
//        BaseLibraryConfig.enableScreenAdaptation
//                && BaseLibraryConfig.enableOtherLibraryScreenAdaptation
//                && activity !is MvvmActivity
//    )
}