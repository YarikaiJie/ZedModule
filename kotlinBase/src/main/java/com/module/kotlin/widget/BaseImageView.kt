package com.module.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.module.kotlin.R
import androidx.core.content.withStyledAttributes

/**
 * 描述：
 * imageView?.shapeAppearanceModel = ShapeAppearanceModel.builder()
 * .setAllCorners(CornerFamily.ROUNDED,20f)
 * .setTopLeftCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
 * .setTopRightCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
 * .setBottomRightCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
 * .setBottomLeftCorner(CornerFamily.CUT,RelativeCornerSize(0.3f))
 * .setAllCornerSizes(ShapeAppearanceModel.PILL)
 * .setTopLeftCornerSize(20f)
 * .setTopRightCornerSize(RelativeCornerSize(0.5f))
 * .setBottomLeftCornerSize(10f)
 * .setBottomRightCornerSize(AbsoluteCornerSize(30f))
 * .build()
 */
open class BaseImageView : AppCompatImageView {
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
        context.withStyledAttributes(attributes, R.styleable.BaseImageView) {
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
        }
    }
}