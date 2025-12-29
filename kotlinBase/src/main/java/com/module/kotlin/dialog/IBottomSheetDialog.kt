package com.ustcinfo.f.ch.kotlin.base.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

/**
 * @author allan
 * @date :2024/2/20 11:04
 * @description:
 */
interface IBottomSheetDialog {
    /**
     * 查找到可以用于toast的ViewGroup。
     */
    fun findToastViewGroup() : ViewGroup?

    /**
     * 尽量早一点调用。在show之前。如果是继承，则放在init{}
     */
    var onDismissBlock:((IBottomSheetDialog)->Unit)?

    /**
     * 尽量早一点调用。在show之前。如果是继承，则放在init{}
     */
    var onShownBlock:((IBottomSheetDialog)->Unit)?

    val window: Window?

    var rootView: View?

    var createdDialog:Dialog?

    /**
     * 提供Dialog、Activity、Fragment、等组件的创建view
     */
    fun onMvvmCreateView(inflater: LayoutInflater,
                                  container: ViewGroup? = null,
                                  savedInstanceState: Bundle? = null) : View

}