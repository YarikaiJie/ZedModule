package com.module.kotlin.iInterface

import androidx.lifecycle.*

/**
 * 对非生命周期对象添加生命周期能力
 */
interface ILifecycleObserver : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        onCreate()
    }

    override fun onStart(owner: LifecycleOwner) {
        onStart()
    }

    override fun onResume(owner: LifecycleOwner) {
        onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        onPause()
    }

    override fun onStop(owner: LifecycleOwner) {
        onStop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy()
    }

    fun onCreate() {

    }

    fun onStart() {
    }

    fun onResume() {
    }

    fun onPause() {
    }

    fun onStop() {
    }

    fun onDestroy() {
    }
}