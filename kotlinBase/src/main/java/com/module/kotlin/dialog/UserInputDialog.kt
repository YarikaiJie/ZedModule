package com.module.kotlin.dialog

import android.text.InputType
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.module.kotlin.R
import com.module.kotlin.databinding.DialogUserInputBinding
import com.module.kotlin.utils.dp
import com.module.kotlin.utils.getStrRes
import com.module.kotlin.utils.onClick


open class UserInputDialog : BaseDialog<DialogUserInputBinding>() {
    companion object {
        fun show(
            manager: FragmentManager,
            title: String?,
            usernameValue: String? = null,
            passwordValue: String? = null,
            sureText: String? = null,
            cancelAble: Boolean = false,
            showBlock: Function1<UserInputDialog, Unit>? = null,
            cancelClick: Function1<UserInputDialog, Unit>? = null,
            sureClick: Function2<String, String, Unit>?,
        ): UserInputDialog {
            val userInputDialog = UserInputDialog().apply {
                onDialogViewLayoutParamBlock = { contentViewGroup, newLp ->
                    newLp.gravity = Gravity.TOP
                    newLp.marginStart = 26.dp
                    newLp.marginEnd = 26.dp
                }

                onShownBlock = {
                    val d = it as UserInputDialog
                    d.isCancelable = cancelAble
                    sureText?.run { d.mBinding.btnSure.text = this }
                    
                    d.mBinding.btnSure.onClick(null) {
                        val username = mBinding.editTextUsername.text.toString()
                        val password = mBinding.editTextPassword.text.toString()
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            sureClick?.invoke(username, password)
                            d.dismiss()
                        }else{
                            Toast.makeText(context, getStrRes(R.string.label_tips_username_or_pwd_null), Toast.LENGTH_SHORT)
                        }
                    }

                    d.mBinding.tvTitle.text = title ?: ""
                    
                    // Set initial values for input fields
                    usernameValue?.let { d.mBinding.editTextUsername.setText(it) }
                    passwordValue?.let { d.mBinding.editTextPassword.setText(it) }
                    
                    // Configure password field for password input
                    d.mBinding.editTextPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    
                    d.mBinding.btnCancel.onClick {
                        d.dismiss()
                        cancelClick?.invoke(d)
                    }
                    showBlock?.invoke(d)
                }
            }
            userInputDialog.show(manager, "UserInputDialog")
            return userInputDialog
        }
    }
}