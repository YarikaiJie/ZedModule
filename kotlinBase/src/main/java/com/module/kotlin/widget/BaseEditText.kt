package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import com.google.android.material.textfield.TextInputEditText
import com.module.kotlin.R

/**
 * 描述：BaseEditText
 */
open class BaseEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        handleTextViewAttribute(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        handleTextViewAttribute(attrs)
    }

    private fun handleTextViewAttribute(attributes: AttributeSet?) {
        context.withStyledAttributes(attributes, R.styleable.BaseEditText) {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseEditText_rippleColor,
                _unboundedRipple = R.styleable.BaseEditText_unboundedRipple,
                _backgroundNormal = R.styleable.BaseEditText_backgroundNormal,
                _backgroundPressed = R.styleable.BaseEditText_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseEditText_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseEditText_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseEditText_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseEditText_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseEditText_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseEditText_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseEditText_strokeColor,
                _strokeWidth = R.styleable.BaseEditText_strokeWidth,
                _backgroundShape = R.styleable.BaseEditText_backgroundShape,
                _backgroundAlpha = R.styleable.BaseEditText_backgroundAlpha,
            )
        }
    }


    private var onSelectionChanged:((edit:BaseEditText, selStart:Int, selEnd:Int)->Unit)? = null

    fun doOnSelectionChanged(change:(edit:BaseEditText, selStart:Int, selEnd:Int)->Unit) {
        onSelectionChanged = change
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        onSelectionChanged?.invoke(this, selStart, selEnd)
    }
}