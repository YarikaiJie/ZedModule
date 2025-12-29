package com.module.kotlin.mvvm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface IMvvmBinding<Binding : ViewBinding> {
    /**
     * Binding变量
     */
    val mBinding: Binding

    /**
     * 这个函数里面，会进行泛型的解析
     * @param self 指代activity、fragment、dialog、popupWindow等界面hold
     */
    fun onCreateViewBinding(self: IMvvmBinding<Binding>,
                                          inflater: LayoutInflater,
                                          container: ViewGroup?,
                                          attach: Boolean = false) : Binding
}