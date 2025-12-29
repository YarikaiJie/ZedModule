package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import com.module.kotlin.R
import androidx.core.content.withStyledAttributes

/**
 * 描述：统一常用控件，方便之后字体样式，或者其他统一修改
 */
open class BaseRadioGroup : RadioGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        handleAttr(attrs)
    }

    private fun handleAttr(attributes: AttributeSet?) {
        context.withStyledAttributes(attributes, R.styleable.BaseRadioGroup) {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseRadioGroup_rippleColor,
                _unboundedRipple = R.styleable.BaseRadioGroup_unboundedRipple,
                _backgroundNormal = R.styleable.BaseRadioGroup_backgroundNormal,
                _backgroundPressed = R.styleable.BaseRadioGroup_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseRadioGroup_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseRadioGroup_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseRadioGroup_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseRadioGroup_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseRadioGroup_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseRadioGroup_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseRadioGroup_strokeColor,
                _strokeWidth = R.styleable.BaseRadioGroup_strokeWidth,
                _backgroundShape = R.styleable.BaseRadioGroup_backgroundShape,
                _backgroundAlpha = R.styleable.BaseRadioGroup_backgroundAlpha,
            )
        }
    }
}