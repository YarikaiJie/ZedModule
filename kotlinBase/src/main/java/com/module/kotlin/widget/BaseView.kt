package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.module.kotlin.R

open class BaseView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        handleAttr(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        handleAttr(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        handleAttr(attrs)
    }

    private fun handleAttr(attributes: AttributeSet?) {
        context.obtainStyledAttributes(attributes, R.styleable.BaseView).apply {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseView_rippleColor,
                _unboundedRipple = R.styleable.BaseView_unboundedRipple,
                _backgroundNormal = R.styleable.BaseView_backgroundNormal,
                _backgroundPressed = R.styleable.BaseView_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseView_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseView_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseView_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseView_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseView_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseView_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseView_strokeColor,
                _strokeWidth = R.styleable.BaseView_strokeWidth,
                _backgroundShape = R.styleable.BaseView_backgroundShape,
                _backgroundAlpha = R.styleable.BaseView_backgroundAlpha,
            )
        }.recycle()
    }
}