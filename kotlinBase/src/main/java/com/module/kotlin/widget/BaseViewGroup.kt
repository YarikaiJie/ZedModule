package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.module.kotlin.R

abstract class BaseViewGroup : ViewGroup {
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
        context.obtainStyledAttributes(attributes, R.styleable.BaseViewGroup).apply {
            //处理背景色
            handleCustomViewBackground(
                this,
                _rippleColor = R.styleable.BaseImageView_rippleColor,
                _unboundedRipple = R.styleable.BaseImageView_unboundedRipple,
                _backgroundNormal = R.styleable.BaseImageView_backgroundNormal,
                _backgroundPressed = R.styleable.BaseImageView_backgroundPressed,
                _backgroundUnEnable = R.styleable.BaseImageView_backgroundUnEnable,
                _cornerRadius = R.styleable.BaseImageView_cornerRadius,
                _cornerSizeTopLeft = R.styleable.BaseImageView_cornerSizeTopLeft,
                _cornerSizeTopRight = R.styleable.BaseImageView_cornerSizeTopRight,
                _cornerSizeBottomLeft = R.styleable.BaseImageView_cornerSizeBottomLeft,
                _cornerSizeBottomRight = R.styleable.BaseImageView_cornerSizeBottomRight,
                _strokeColor = R.styleable.BaseImageView_strokeColor,
                _strokeWidth = R.styleable.BaseImageView_strokeWidth,
                _backgroundShape = R.styleable.BaseImageView_backgroundShape,
                _backgroundAlpha = R.styleable.BaseImageView_backgroundAlpha,
            )
        }.recycle()
    }

    //1.MeasureSpec.UNSPECIFIED -> 未指定尺寸
    //2、MeasureSpec.EXACTLA -> 精确尺寸，控件的宽高指定大小或者为FILL_PARENT
    //3、MeasureSpec.AT_MOST -> 最大尺寸，控件的宽高为WRAP_CONTENT，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸
    //val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    //val heightMode = MeasureSpec.getMode(heightMeasureSpec)
    //val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    //val heightSize = MeasureSpec.getSize(heightMeasureSpec)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}