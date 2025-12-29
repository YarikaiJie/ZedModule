package com.module.kotlin.delegate

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.module.kotlin.utils.android.asOrNull
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


class ViewModelDelegate<T : ViewModel>(
    private val cls: Class<T>,
    private val factory: ViewModelProvider.Factory? = null,
    private val ownerCall: (() -> ViewModelStoreOwner?)? = null/*指定在哪里创建*/
) :
    ReadOnlyProperty<Any?, T> {
    private var viewModelTemp: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val viewModel = viewModelTemp
        return if (viewModel == null) {
            val viewModelStoreOwner = ownerCall?.invoke() ?: thisRef.asOrNull<ViewModelStoreOwner>()
            ?: throw IllegalArgumentException("需要传入一个ViewModelStoreOwner类型的参数，或者在ViewModelStoreOwner的作用域类创建变量")
            val createViewModel = viewModelStoreOwner.createViewModel(cls, factory)

            viewModelTemp = createViewModel
            createViewModel
        } else {
            viewModel
        }
    }
}

class ActivityViewModelDelegate<T : ViewModel>(
    private val cls: Class<T>,
    private val factory: ViewModelProvider.Factory? = null
) :
    ReadOnlyProperty<Fragment, T> {
    private var viewModelTemp: T? = null
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val viewModel = viewModelTemp
        return if (viewModel == null) {
            val createViewModel = thisRef.requireActivity().createViewModel(cls, factory)
            viewModelTemp = createViewModel
            createViewModel
        } else {
            viewModel
        }
    }
}

/**
 * 创建viewModel
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.createViewModel(factory: ViewModelProvider.Factory? = null) =
    createViewModel(T::class.java, factory)


/**
 * 创建viewModel
 */
fun <T : ViewModel> ViewModelStoreOwner.createViewModel(
    cls: Class<T>,
    factory: ViewModelProvider.Factory? = null
) =
    getViewModelProvider(this, factory)[cls]

/**
 * 获取viewModel工厂
 */
private fun getViewModelProvider(
    owner: ViewModelStoreOwner,
    factory: ViewModelProvider.Factory?
): ViewModelProvider {
    return if (factory == null) {
        ViewModelProvider(owner)
    } else {
        ViewModelProvider(owner, factory)
    }
}

