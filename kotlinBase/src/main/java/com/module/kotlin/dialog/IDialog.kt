package com.module.kotlin.dialog

import android.view.View
import android.widget.FrameLayout

/**
 * @author zed
 * @description:
 */
interface IDialog : IBottomSheetDialog {

    /**
     * 尽量早一点调用。在show之前。如果是继承，则放在init{}
     */
    var onDialogViewLayoutParamBlock:((contentViewGroup:View, newLp: FrameLayout.LayoutParams)->Unit)?
}