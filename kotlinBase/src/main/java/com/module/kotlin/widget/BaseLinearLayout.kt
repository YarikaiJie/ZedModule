package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.module.kotlin.R
import androidx.core.content.withStyledAttributes

/**
 * 描述：统一常用控件，方便之后字体样式，或者其他统一修改
 */
open class BaseLinearLayout : LinearLayout {
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
        context.withStyledAttributes(attributes, R.styleable.BaseLinearLayout) {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseLinearLayout_rippleColor,
                _unboundedRipple = R.styleable.BaseLinearLayout_unboundedRipple,
                _backgroundNormal = R.styleable.BaseLinearLayout_backgroundNormal,
                _backgroundPressed = R.styleable.BaseLinearLayout_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseLinearLayout_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseLinearLayout_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseLinearLayout_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseLinearLayout_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseLinearLayout_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseLinearLayout_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseLinearLayout_strokeColor,
                _strokeWidth = R.styleable.BaseLinearLayout_strokeWidth,
                _backgroundShape = R.styleable.BaseLinearLayout_backgroundShape,
                _backgroundAlpha = R.styleable.BaseLinearLayout_backgroundAlpha,
            )
        }
    }
}