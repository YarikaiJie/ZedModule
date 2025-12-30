package com.module.kotlin.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.module.kotlin.mvvm.IMvvmBinding
import com.module.kotlin.mvvm.MvvmBindingImpl

/**
 * @author zed
 * @description: 新的基础binding的Dialog
 * 不建议显示带EditText，动画比较难看，目前没有找到比较好的解决方案。
 */
open class BaseDialog<Binding : ViewBinding>(mode: MvvmDialogV2Mode = MvvmDialogV2Mode.Center) : MvvmDialog(mode),
    IMvvmBinding<Binding> by MvvmBindingImpl() {
    final override fun onMvvmCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return onCreateViewBinding(this, inflater, container).also {
            _onBindingInitBlock?.invoke(it)
            _onBindingInitBlock = null
        }.root
    }

    private var _onBindingInitBlock:((binding: Binding) -> Unit)? = null

    /**
     * 在创建dialog后立刻调用。否则可能错过他的执行.
     * 只用作初始化一些界面。
     */
    fun setOnBindingInitBlock(block : (binding: Binding) -> Unit) {
        _onBindingInitBlock = block
    }
}