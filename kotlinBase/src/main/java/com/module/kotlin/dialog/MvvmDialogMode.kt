package com.module.kotlin.dialog

import android.view.Gravity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.module.kotlin.R

/**
 * @author zed
 * @description:
 */
enum class MvvmDialogV2Mode {
    Center,
    Bottom,
    Top
}

fun MvvmDialogV2Mode.toAnimRes():Int {
    return when (this) {
        MvvmDialogV2Mode.Center -> R.style.AnimScaleCenter
        MvvmDialogV2Mode.Bottom -> R.style.PopupWindowBottomIn
        MvvmDialogV2Mode.Top ->R.style.PopupWindowTopIn
    }
}

fun MvvmDialogV2Mode.toGravity():Int {
    return when (this) {
        MvvmDialogV2Mode.Center -> Gravity.CENTER
        MvvmDialogV2Mode.Bottom -> Gravity.BOTTOM
        MvvmDialogV2Mode.Top -> Gravity.TOP
    }
}

/**
 * 有的时候，我们把fragment显示到了XXXDialogV2中，通过该方法来找到对应的dialog。
 * 这样就可继而调用dismiss等函数。
 * @return 找到的dialog
 * @param contentFragment 显示的contentFragment
 */
fun findDialogV2(contentFragment: Fragment?): DialogFragment? {
    contentFragment ?: return null
    if (contentFragment is DialogFragment) {
        return contentFragment
    }
    return findDialogV2(contentFragment.parentFragment)
}