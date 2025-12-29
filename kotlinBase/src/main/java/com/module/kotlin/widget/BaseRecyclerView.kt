package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.module.kotlin.R
import androidx.core.content.withStyledAttributes


open class BaseRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        handleAttr(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        handleAttr(attrs)
    }

    private fun handleAttr(attributes: AttributeSet?) {
        context.withStyledAttributes(attributes, R.styleable.BaseRecyclerView) {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseRecyclerView_rippleColor,
                _unboundedRipple = R.styleable.BaseRecyclerView_unboundedRipple,
                _backgroundNormal = R.styleable.BaseRecyclerView_backgroundNormal,
                _backgroundPressed = R.styleable.BaseRecyclerView_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseRecyclerView_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseRecyclerView_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseRecyclerView_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseRecyclerView_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseRecyclerView_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseRecyclerView_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseRecyclerView_strokeColor,
                _strokeWidth = R.styleable.BaseRecyclerView_strokeWidth,
                _backgroundShape = R.styleable.BaseRecyclerView_backgroundShape,
                _backgroundAlpha = R.styleable.BaseRecyclerView_backgroundAlpha,
            )
            if (getBoolean(R.styleable.BaseRecyclerView_hasFixedSize, false)) {
                //如果recyclerView的大小是固定的，设置这个参数，可以优化一些性能
                setHasFixedSize(true)
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (layoutManager == null) {
            //设置一个默认的管理器，否则recyclerView不显示
            layoutManager = LinearLayoutManager(context)
        }
    }
}