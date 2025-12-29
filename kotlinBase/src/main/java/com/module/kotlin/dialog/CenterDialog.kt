package com.module.kotlin.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.module.kotlin.databinding.DialogCenterBinding
import com.module.kotlin.utils.dp
import com.module.kotlin.utils.gone
import com.module.kotlin.utils.onClick
import com.module.kotlin.utils.visible


open class CenterDialog : BaseDialog<DialogCenterBinding>() {
    companion object {
        fun show(
            manager: FragmentManager,
            title: String?,
            content: String?,
            sureText: String? = null,
            cancelAble: Boolean = false,
            showBlock: Function1<CenterDialog, Unit>? = null,
            cancelClick: Function1<CenterDialog, Unit>? = null,
            sureClick: Function1<CenterDialog, Unit>?,
        ): CenterDialog {
            val centerDialog = CenterDialog().apply {
                onDialogViewLayoutParamBlock = { contentViewGroup, newLp ->
                    newLp.gravity = Gravity.CENTER
                    newLp.marginStart = 26.dp
                    newLp.marginEnd = 26.dp
                }

                onShownBlock = {
                    val d = it as CenterDialog
                    d.isCancelable = cancelAble
                    sureText?.let {
                        d.mBinding.btnSure.text = it
                    }

                    d.mBinding.btnSure.onClick(null) {
                        sureClick?.invoke(d)
                        d.dismiss()
                    }

                    d.mBinding.tvTitle.text = title
                    if (content.isNullOrEmpty()){
                        d.mBinding.tvContent.gone()
                    }else{
                        d.mBinding.tvContent.visible()
                        d.mBinding.tvContent.text = content
                    }
                    d.mBinding.btnCancel.onClick {
                        d.dismiss()
                        cancelClick?.invoke(d)
                    }
                    showBlock?.invoke(d)
                }
            }
            centerDialog.show(manager, "CenterDialog")
            return centerDialog
        }
    }
}