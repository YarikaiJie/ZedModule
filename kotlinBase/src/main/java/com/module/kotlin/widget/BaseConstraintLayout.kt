package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.module.kotlin.R
import androidx.core.content.withStyledAttributes


/**
 * 描述：
 */
open class BaseConstraintLayout : ConstraintLayout {
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
        context.withStyledAttributes(attributes, R.styleable.BaseConstraintLayout) {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseConstraintLayout_rippleColor,
                _unboundedRipple = R.styleable.BaseConstraintLayout_unboundedRipple,
                _backgroundNormal = R.styleable.BaseConstraintLayout_backgroundNormal,
                _backgroundPressed = R.styleable.BaseConstraintLayout_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseConstraintLayout_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseConstraintLayout_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseConstraintLayout_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseConstraintLayout_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseConstraintLayout_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseConstraintLayout_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseConstraintLayout_strokeColor,
                _strokeWidth = R.styleable.BaseConstraintLayout_strokeWidth,
                _backgroundShape = R.styleable.BaseConstraintLayout_backgroundShape,
                _backgroundAlpha = R.styleable.BaseConstraintLayout_backgroundAlpha,
            )
        }
    }
}