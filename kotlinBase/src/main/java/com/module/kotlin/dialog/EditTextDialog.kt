package com.module.kotlin.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.module.kotlin.databinding.DialogEditTextBinding
import com.module.kotlin.utils.dp
import com.module.kotlin.utils.onClick


open class EditTextDialog : BaseDialog<DialogEditTextBinding>() {
    companion object {
        fun show(
            manager: FragmentManager,
            title: String?,
            content: String?,
            sureText: String? = null,
            cancelAble: Boolean = false,
            showBlock: Function1<EditTextDialog, Unit>? = null,
            cancelClick: Function1<EditTextDialog, Unit>? = null,
            sureClick: Function1<String, Unit>?,
        ): EditTextDialog {
            val editTextDialog = EditTextDialog().apply {
                onDialogViewLayoutParamBlock = { contentViewGroup, newLp ->
                    newLp.gravity = Gravity.TOP
                    newLp.marginStart = 26.dp
                    newLp.marginEnd = 26.dp
                }

                onShownBlock = {
                    val d = it as EditTextDialog
                    d.isCancelable = cancelAble
                    sureText?.run {  d.mBinding.btnSure.text = this }
                    d.mBinding.btnSure.onClick(null) {
                        val value = mBinding.editText.text.toString()
                        if (value.isNotEmpty()) sureClick?.invoke(value)
                        d.dismiss()
                    }

                    d.mBinding.tvTitle.text = title?:""
                    d.mBinding.editText.setText(content?:"")
                    d.mBinding.btnCancel.onClick {
                        d.dismiss()
                        cancelClick?.invoke(d)
                    }
                    showBlock?.invoke(d)
                }
            }
            editTextDialog.show(manager, "EditTextDialog")
            return editTextDialog
        }
    }
}