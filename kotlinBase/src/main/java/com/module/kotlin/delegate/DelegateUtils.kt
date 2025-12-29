package com.tyiot.module.android.delegate

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.module.kotlin.delegate.ActivityViewModelDelegate
import com.module.kotlin.delegate.FragmentBundleDelegate
import com.module.kotlin.delegate.MmkvDelegate
import com.module.kotlin.delegate.ViewModelDelegate


/**
 * 不安全的初始化，性能更高
 */
fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * 请自己注意检查，是否是支持的类型
 * bundle托管
 */
fun <T> bundleDelegate(key: String, defaultValue: T) = FragmentBundleDelegate(key, defaultValue)

/**
 * mmkv存储
 */
fun <T:Any> mmkvDelegate(key: String, defaultValue: T) = MmkvDelegate(key, defaultValue)

/**
 * textView文本
 */
fun textViewDelegate(textView: () -> TextView) = TextDelegate(textView)

/**
 * 创建viewModel的托管类，方便创建viewModel
 */
inline fun <reified T : ViewModel> viewModelDelegate(
    factory: ViewModelProvider.Factory? = null,
    noinline ownerCall: (() -> ViewModelStoreOwner?)? = null
) =
    ViewModelDelegate(T::class.java, factory, ownerCall)


/**
 * 创建activity的viewModel的托管类，方便创建viewModel
 */
inline fun <reified T : ViewModel> activityViewModelDelegate(factory: ViewModelProvider.Factory? = null) =
    ActivityViewModelDelegate(T::class.java, factory)
