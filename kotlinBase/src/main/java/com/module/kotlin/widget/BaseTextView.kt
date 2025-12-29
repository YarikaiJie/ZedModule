package com.module.kotlin.widget

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView
import com.module.kotlin.R
import kotlin.math.ceil
import kotlin.math.max

/**
 * 描述：
 * setMovementMethod(ScrollingMovementMethod.getInstance());
 * TextView的textIsSelectable属性和setMovementMethod()
 * TextView的textIsSelectable属性可以支持长按文字可以复制，搜索等，而且支持对TextView的内容滑动。具体见图片
 *T extView的setMovementMethod()方法，也可以支持对TextView的内容滑动，但对Textview内容不支持长按文字可以复制，搜索等。
 * // 通过xml设置
 * android:outlineAmbientShadowColor="#FFAAAA" //控制没有背景时候的阴影颜色 需要设置outlineProvider不是background，安卓10以上有效
 * android:outlineSpotShadowColor="#BAFDCE" // 有背景时候，设置阴影颜色 安卓10以上有效
 * android:outlineProvider="background"
 */
open class BaseTextView : MaterialTextView {
    /**
     * 文字支持跑马灯
     */
    var isMarqueeText = false

    var fixAfterWrapEndIsToolLong = false

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
        context.obtainStyledAttributes(attributes, R.styleable.BaseTextView).apply {
            val canMarqueeText = getBoolean(R.styleable.BaseTextView_canMarqueeText, isMarqueeText)
            isMarqueeText = canMarqueeText

            fixAfterWrapEndIsToolLong = getBoolean(R.styleable.BaseTextView_fixAfterWrapEndIsToolLong, false)

            //处理背景色
            //这样的话，我们只需要拦截掉，handleCustomViewBackground()的运行即可，android:background 会自行被系统设置。
            if (customBackground()) {
                handleCustomViewBackground(
                    this,
                    _rippleColor = R.styleable.BaseTextView_rippleColor,
                    _unboundedRipple = R.styleable.BaseTextView_unboundedRipple,
                    _backgroundNormal = R.styleable.BaseTextView_backgroundNormal,
                    _backgroundPressed = R.styleable.BaseTextView_backgroundPressed,
                    _backgroundUnEnable = R.styleable.BaseTextView_backgroundUnEnable,
                    _cornerRadius = R.styleable.BaseTextView_cornerRadius,
                    _cornerSizeTopLeft = R.styleable.BaseTextView_cornerSizeTopLeft,
                    _cornerSizeTopRight = R.styleable.BaseTextView_cornerSizeTopRight,
                    _cornerSizeBottomLeft = R.styleable.BaseTextView_cornerSizeBottomLeft,
                    _cornerSizeBottomRight = R.styleable.BaseTextView_cornerSizeBottomRight,
                    _strokeColor = R.styleable.BaseTextView_strokeColor,
                    _strokeWidth = R.styleable.BaseTextView_strokeWidth,
                    _backgroundShape = R.styleable.BaseTextView_backgroundShape,
                    _backgroundAlpha = R.styleable.BaseTextView_backgroundAlpha,
                )
            }
        }.recycle()
    }

    /**
     * 有的子类不需要handleCustomViewBackground()这个复杂的自定义背景；
     * 而采用xml的drawable，使用的android:background，因此就需要过滤掉。
     */
    protected open fun customBackground() = true

    override fun isFocused(): Boolean {
        return if (isMarqueeText) {
            true
        } else {
            super.isFocused()
        }
    }

    //解决换行右侧太多的问题
    //https://stackoverflow.com/questions/50287198/textview-remove-space-after-line-break
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (fixAfterWrapEndIsToolLong) {
            var maxWidth = ceil(getMaxLineWidth(layout)).toInt()
            maxWidth += paddingRight + paddingLeft
            setMeasuredDimension(maxWidth, measuredHeight)
        }
    }

    open fun getMaxLineWidth(layout: Layout): Float {
        var maximumWidth = 0.0f
        val lines = layout.lineCount
        for (i in 0 until lines) {
            maximumWidth = max(layout.getLineWidth(i), maximumWidth)
        }

        return maximumWidth
    }
}