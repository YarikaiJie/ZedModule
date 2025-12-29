package com.module.kotlin.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * @Description: ViewPager2简单适配器
 * @Author: zed_qiu
 * @CreateDate: 2025/11/26 14:06
 */
fun <T> ViewPager2.simplePagerAdapter(
    fragment: Fragment,
    datas: List<T>,
    onCreateItem: Function2<@ParameterName("position") Int, T, Fragment>
): FragmentStateAdapter {
    return simplePagerAdapter(
        fragment.childFragmentManager,
        fragment.lifecycle,
        datas,
        onCreateItem
    )
}

fun <T> ViewPager2.simplePagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    datas: List<T>,
    onCreateItem: Function2<@ParameterName("position") Int, T, Fragment>
): FragmentStateAdapter {
    val baseAdapter = object : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = datas.count()

        override fun createFragment(position: Int): Fragment {
            return onCreateItem.invoke(position, datas[position])
        }
    }
    if (adapter == null) {
        adapter = baseAdapter
    }
    return baseAdapter
}