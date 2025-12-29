package com.module.kotlin.recyclerview

/**
 * author: allan.jiang
 * Time: 2022/11/24
 * Desc:
 */
interface IBindAdapter<DATA> {
    fun submitList(
        newList: List<DATA>?,
        isReplaceData: Boolean,
    )

    suspend fun submitListAsync(
        newList: List<DATA>?,
        isReplaceData: Boolean,
    )
}