package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.module.kotlin.R

/**
 * 描述：
 */
open class BaseFrameLayout:FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        handleAttr(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        handleAttr(attrs)
    }
    
    private fun handleAttr(attributes: AttributeSet?) {
        context.withStyledAttributes(attributes, R.styleable.BaseFrameLayout) {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseFrameLayout_rippleColor,
                _unboundedRipple = R.styleable.BaseFrameLayout_unboundedRipple,
                _backgroundNormal = R.styleable.BaseFrameLayout_backgroundNormal,
                _backgroundPressed = R.styleable.BaseFrameLayout_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseFrameLayout_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseFrameLayout_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseFrameLayout_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseFrameLayout_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseFrameLayout_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseFrameLayout_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseFrameLayout_strokeColor,
                _strokeWidth = R.styleable.BaseFrameLayout_strokeWidth,
                _backgroundShape = R.styleable.BaseFrameLayout_backgroundShape,
                _backgroundAlpha = R.styleable.BaseFrameLayout_backgroundAlpha,
            )
        }
    }
}