package com.module.kotlin.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.module.kotlin.BaseGlobalConst
import com.module.kotlin.R
import com.module.kotlin.dialog.CenterDialog

/**
@author: Zed.Qiu
@date: 2023/2/3
@description:
 */
fun dialogSingleConfirm(
    activity: Fragment,
    content: String?, colorRes: Int = R.color.black, block: () -> Unit
) {
    dialogSingleConfirmManager(activity.childFragmentManager, content, colorRes, block)
}


fun dialogSingleConfirm(
    activity: AppCompatActivity,
    content: String?,
    colorRes: Int = R.color.black, block: () -> Unit
) {
    dialogSingleConfirmManager(activity.supportFragmentManager, content, colorRes, block)
}

fun dialogSingleConfirmManager(
    manager: FragmentManager,
    content: String?, colorRes: Int = R.color.black, block: () -> Unit
) {
    CenterDialog.show(
        manager,
        null,
        content,
        getStrRes(R.string.sure),
        showBlock = {
            it.run {
                mBinding.btnCancel.gone()
                mBinding.tvContent.setTextColor(getColorRes(colorRes))
                mBinding.tvContent.textSize = 24f
            }
        }
    ) {
        it.dismiss()
        block.invoke()
    }
}

/**
@description 展示警告图标的确认弹窗
 */
fun dialogWarningConfirm(
    fragment: Fragment,
    title: String,
    confirmStr: String,
    warnRes: Int? = null,
    cancelClick: Function1<CenterDialog, Unit>? = null,
    confirmBlock: () -> Unit
) {
    dialogWarningConfirmManager(
        fragment.childFragmentManager,
        title,
        confirmStr,
        warnRes,
        cancelClick,
        confirmBlock
    )
}

/**
@description 展示警告图标的确认弹窗
 */
fun dialogWarningConfirm(
    activity: AppCompatActivity,
    title: String,
    confirmStr: String,
    warnRes: Int? = null,
    cancelClick: Function1<CenterDialog, Unit>? = null,
    confirmBlock: () -> Unit
) {
    dialogWarningConfirmManager(
        activity.supportFragmentManager,
        title,
        confirmStr,
        warnRes,
        cancelClick,
        confirmBlock
    )
}


/**
@description 展示警告图标的确认弹窗
 */
fun dialogWarningConfirmManager(
    manager: FragmentManager,
    title: String,
    confirmStr: String,
    warnRes: Int? = null,
    cancelClick: Function1<CenterDialog, Unit>? = null,
    confirmBlock: () -> Unit
) {
    CenterDialog.show(
        manager,
        null,
        title,
        confirmStr,
        false,
        showBlock = {
            it.run {
                mBinding.tvContent.setTextColor(getColorRes(R.color.black))
                warnRes?.run {
                    val icon = BaseGlobalConst.app.getDrawable(this)
                    mBinding.tvContent.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        icon,
                        null,
                        null
                    )
                    mBinding.tvContent.compoundDrawablePadding = 30.dp
                }
                mBinding.tvContent.textSize = 24f
            }
        },
        cancelClick = cancelClick
    ) {
        it.dismiss()
        confirmBlock.invoke()
    }
}
