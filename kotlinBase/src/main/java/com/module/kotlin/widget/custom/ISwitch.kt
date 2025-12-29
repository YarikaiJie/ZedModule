package com.module.kotlin.widget.custom

/**
 * @author allan
 * @date :2024/11/25 9:29
 * @description: 对于左右切换的按钮的抽象
 */
interface ISwitch {
    var valueCallback : ((isClosed:Boolean)->Unit)?
    fun initValue(close:Boolean)
    fun setValue(close:Boolean)
    var abort:Boolean
    val isInit:Boolean
    val isClosed:Boolean
}