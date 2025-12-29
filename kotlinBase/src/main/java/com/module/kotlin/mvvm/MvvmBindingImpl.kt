package com.module.kotlin.mvvm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 默认通过泛型创建view的逻辑实现类
 */
class MvvmBindingImpl<Binding : ViewBinding> : IMvvmBinding<Binding> {
    private lateinit var binding: Binding

    override val mBinding: Binding
        get() = binding

    private fun findViewBinding(javaClass:Class<*>) : Class<Binding>? {
        val parameterizedType = javaClass.getParameterizedType() ?: return null
        val actualTypeArguments = parameterizedType.actualTypeArguments
        val type = actualTypeArguments[0]
        if ((ViewBinding::class.java).isAssignableFrom(type as Class<*>)) {
            return type as Class<Binding>
        }
        return null
    }

    override fun onCreateViewBinding(self: IMvvmBinding<Binding>, inflater: LayoutInflater, container: ViewGroup?, attach: Boolean): Binding {
        var clz:Class<Binding>? = findViewBinding(self.javaClass)
        //修正框架，允许往上寻找3层superClass的第一个泛型做为ViewBinding
        if (clz == null) {
            val superClass = self.javaClass.superclass
            if (superClass != null) {
                clz = findViewBinding(superClass) ?: superClass.superclass?.let { findViewBinding(it) }
            }
        }
        if (clz == null) throw IllegalArgumentException("需要一个ViewBinding类型的泛型")
        binding = clz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, inflater, container, attach) as Binding
        return binding
    }

    fun Class<*>?.getParameterizedType(): ParameterizedType? {
        if (this == null) {
            return null
        }
        val type = this.genericSuperclass
        return if (type == null || type !is ParameterizedType) {
            this.superclass.getParameterizedType()
        } else {
            type
        }
    }
}