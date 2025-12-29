package com.module.kotlin.utils

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.module.kotlin.recyclerview.LinearLayoutManagerDivider

/**
 * 给线性布局设置分割线
 */
fun RecyclerView.addDividingWithLinearLayoutManager(
    size: Int,
    @ColorInt color: Int,
    radius: Float = 0f,
    orientation: Int = DividerItemDecoration.VERTICAL,
    startMargin: Int = 0,
    endMargin: Int = 0,
    drawLastDivider: Boolean = false,
) {
    val layoutManager = this.layoutManager
    this.addItemDecoration(
        LinearLayoutManagerDivider(
            this.context,
            if (layoutManager is LinearLayoutManager) {
                layoutManager.orientation
            } else {
                orientation
            }
        ).also {
            it.setDrawable(GradientDrawable().also { drawable ->
                drawable.setColor(color)
                drawable.cornerRadius = radius
                drawable.shape = GradientDrawable.RECTANGLE
                if (orientation == DividerItemDecoration.VERTICAL) {
                    drawable.setSize(-1, size)
                } else {
                    drawable.setSize(size, -1)
                }
            })
            it.startMargin = startMargin
            it.endMargin = endMargin
            it.drawLastDivider = drawLastDivider
        })
}