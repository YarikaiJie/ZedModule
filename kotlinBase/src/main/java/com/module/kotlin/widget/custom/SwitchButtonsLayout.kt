package com.module.kotlin.widget.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.module.kotlin.R
import com.module.kotlin.databinding.SwitchButtonsLayoutBinding
import com.module.kotlin.utils.invisible
import com.module.kotlin.utils.onClick
import com.module.kotlin.utils.visible
import kotlin.math.max

/**
 * 自定义SwitchView 全新设计。 滑块。
 * 支持 wrap_content (自动取最大宽度) 和 固定宽度/match_parent (平分宽度)。
 * /**
 * 自定义SwitchView 全新设计。 滑块。
 * 支持 wrap_content (自动取最大宽度) 和 固定宽度/match_parent (平分宽度)。
 *    <com.module.kotlin.widget.custom.SwitchButtonsLayout
 *        android:id="@+id/modeSwitch"
 *        android:layout_width="match_parent"
 *        android:layout_height="40dp"
 *        android:layout_gravity="center_horizontal"
 *        android:layout_marginStart="@dimen/dp_30"
 *        android:layout_marginTop="@dimen/dp_30"
 *        android:layout_marginEnd="@dimen/dp_30"
 *        app:first_str="@string/low_pressure"
 *        app:second_str="@string/high_pressure"
 *        app:select_text_color_left="@color/blue"
 *        app:select_text_color_right="@color/red"
 *        app:textSize="18sp" />
 */
 */
class SwitchButtonsLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(
        context, attrs, defStyleAttr
    ) {
    private var mViewBinding: SwitchButtonsLayoutBinding
    var isLeft = true
        private set

    private var isInit = false
    fun isInited() = isInit

    /**
     * 点击切换的回调函数
     */
    var valueCallback : ((Boolean)->Unit)? = null

    @Volatile
    private var isPost = false

    private val textColor:Int
    private val textSelectColorLeft:Int
    private var textSelectColorRight:Int = -1
    private val textColorDisable:Int
    private val textSelectColorDisable:Int

    private var isDisabled = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButtonsLayout)

        textColor = typedArray.getColor(
            R.styleable.SwitchButtonsLayout_text_color, context.getColor(
                R.color.color_switch_block_text))
        textSelectColorLeft = typedArray.getColor(
            R.styleable.SwitchButtonsLayout_select_text_color_left, context.getColor(
                R.color.color_switch_block_text_left_sel))
        textSelectColorRight = typedArray.getColor(R.styleable.SwitchButtonsLayout_select_text_color_right, textSelectColorLeft)
        textColorDisable = typedArray.getColor(
            R.styleable.SwitchButtonsLayout_text_color_disable, context.getColor(
                R.color.color_switch_block_text_dis))
        textSelectColorDisable = typedArray.getColor(
            R.styleable.SwitchButtonsLayout_select_text_color_disable, context.getColor(
                R.color.color_switch_block_text_sel_dis))

        val textPaddingHorz = typedArray.getDimension(R.styleable.SwitchButtonsLayout_textPaddingHorz, -1f).toInt()

        val leftStr = typedArray.getString(R.styleable.SwitchButtonsLayout_first_str)
        val rightStr = typedArray.getString(R.styleable.SwitchButtonsLayout_second_str)

        val paddingInner = typedArray.getDimension(R.styleable.SwitchButtonsLayout_padding_inner, -1f).toInt()
        val textSize = typedArray.getDimension(R.styleable.SwitchButtonsLayout_textSize, -1f)
        val isBold = typedArray.getBoolean(R.styleable.SwitchButtonsLayout_isBold, false)

        typedArray.recycle()
        mViewBinding = SwitchButtonsLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        mViewBinding.leftTv.text = leftStr
        mViewBinding.rightTv.text = rightStr

        if (textSize > 0) {
            mViewBinding.leftTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            mViewBinding.rightTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }

        if (isBold) {
            mViewBinding.leftTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            mViewBinding.rightTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }

        mViewBinding.root.onClick {
            if(isDisabled) return@onClick

            val newIsLeft = !isLeft
            setValue(newIsLeft)
            valueCallback?.invoke(newIsLeft)
        }

        if (textPaddingHorz >= 0) {
            mViewBinding.leftTv.setPadding(textPaddingHorz, 0, textPaddingHorz, 0)
            mViewBinding.rightTv.setPadding(textPaddingHorz, 0, textPaddingHorz, 0)
        }

        // 处理 paddingInner
        if (paddingInner == 0) {
            mViewBinding.padding.visibility = GONE
        } else if (paddingInner > 0) {
            mViewBinding.padding.layoutParams = mViewBinding.padding.layoutParams.apply {
                layoutParams.width = paddingInner
            }
        }

        post {
            // 确保初始状态颜色和位置正确
            if (!isLeft) {
                // 如果初始是右边，需要确保位置正确
                // 由于我们重写了 onLayout，位置会自动修正，这里只需要确保颜色正确
                // 但为了保险起见，或者如果 onLayout 还没触发（post 在 layout 之后），我们可以手动触发一次
                updateSliderPosition()
            }
            isPost = true
            changeTextColor()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        // 动态调整根布局高度模式：
        // 1. 如果父布局给的是固定高度 (EXACTLY)，让子布局填满 (MATCH_PARENT)
        // 2. 如果父布局是 wrap_content (AT_MOST/UNSPECIFIED)，让子布局自适应 (WRAP_CONTENT)
        val rootParams = mViewBinding.root.layoutParams
        if (heightMode == MeasureSpec.EXACTLY) {
            if (rootParams.height != LayoutParams.MATCH_PARENT) {
                rootParams.height = LayoutParams.MATCH_PARENT
                mViewBinding.root.layoutParams = rootParams
            }
        } else {
            if (rootParams.height != LayoutParams.WRAP_CONTENT) {
                rootParams.height = LayoutParams.WRAP_CONTENT
                mViewBinding.root.layoutParams = rootParams
            }
        }

        // 1. 如果是 WRAP_CONTENT，先重置子 View 宽度为 WRAP_CONTENT 以便获取真实内容宽度
        // 避免之前被设置为固定宽度后无法回退到自适应大小
        if (widthMode != MeasureSpec.EXACTLY) {
            if (mViewBinding.leftTv.layoutParams.width != -2) mViewBinding.leftTv.layoutParams.width = -2
            if (mViewBinding.rightTv.layoutParams.width != -2) mViewBinding.rightTv.layoutParams.width = -2
        }

        // 2. 执行默认测量，让子 View 测量出自己的大小
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var targetW = 0
        val paddingW = if (mViewBinding.padding.visibility != GONE) mViewBinding.padding.measuredWidth else 0
        // 获取根布局（ConstraintLayout）的 padding，因为 TextViews 是在根布局内部
        val rootPaddingH = mViewBinding.root.paddingLeft + mViewBinding.root.paddingRight

        if (widthMode == MeasureSpec.EXACTLY) {
            // 3. 固定宽度或 MATCH_PARENT：计算可用空间并平分
            // 总宽度 - 中间分割线宽度 - 外层 padding - 内部根布局 padding
            val totalW = measuredWidth
            val availableW = totalW - paddingW - paddingLeft - paddingRight - rootPaddingH
            targetW = availableW / 2
        } else {
            // 4. WRAP_CONTENT：取左右文字的最大宽度，保持对称
            val leftW = mViewBinding.leftTv.measuredWidth
            val rightW = mViewBinding.rightTv.measuredWidth
            targetW = max(leftW, rightW)
        }

        // 5. 边界检查
        if (targetW < 0) targetW = 0

        // 6. 检查并应用新宽度，如果发生了变化则触发重新测量
        var remeasure = false

        if (mViewBinding.leftTv.measuredWidth != targetW) {
            mViewBinding.leftTv.layoutParams.width = targetW
            remeasure = true
        }
        if (mViewBinding.rightTv.measuredWidth != targetW) {
            mViewBinding.rightTv.layoutParams.width = targetW
            remeasure = true
        }
        // 背景 View 宽度也需同步
        if (mViewBinding.selectBgView.layoutParams.width != targetW) {
            mViewBinding.selectBgView.layoutParams.width = targetW
            remeasure = true
        }
        if (mViewBinding.selectBgViewDisable.layoutParams.width != targetW) {
            mViewBinding.selectBgViewDisable.layoutParams.width = targetW
            remeasure = true
        }

        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 布局变化时，确保滑块位置正确
        updateSliderPosition()
    }

    private fun updateSliderPosition() {
        val targetX = if (isLeft) {
            0f
        } else {
            (mViewBinding.selectBgView.width + mViewBinding.padding.width).toFloat()
        }
        mViewBinding.selectBgView.translationX = targetX
        mViewBinding.selectBgViewDisable.translationX = targetX
    }

    private fun changeTextColor() {
        if (isDisabled) {
            if(isLeft) {
                mViewBinding.leftTv.setTextColor(textSelectColorDisable)
                mViewBinding.rightTv.setTextColor(textColorDisable)
            } else {
                mViewBinding.rightTv.setTextColor(textSelectColorDisable)
                mViewBinding.leftTv.setTextColor(textColorDisable)
            }
        } else {
            if (isLeft) {
                mViewBinding.leftTv.setTextColor(textSelectColorLeft)
                mViewBinding.rightTv.setTextColor(textColor)
            } else {
                mViewBinding.leftTv.setTextColor(textColor)
                mViewBinding.rightTv.setTextColor(textSelectColorRight)
            }
        }

        if (isDisabled) {
            mViewBinding.selectBgViewDisable.visible()
            mViewBinding.selectBgView.invisible()
        } else {
            mViewBinding.selectBgViewDisable.invisible()
            mViewBinding.selectBgView.visible()
        }
    }

    fun initValue(isLeft:Boolean, disable:Boolean, leftRightStrs:Pair<String, String>? = null) {
        isInit = true
        this.isLeft = isLeft

        isDisabled = disable

        if (leftRightStrs != null) {
            mViewBinding.leftTv.text = leftRightStrs.first
            mViewBinding.rightTv.text = leftRightStrs.second
        }

        if (isPost) {
            updateSliderPosition()
            changeTextColor()
        }
    }

    fun setValue(isLeft: Boolean, ignoreAnimation: Boolean = false) {
        if (!isInit) throw kotlin.RuntimeException()
        if (isDisabled) return
        //后续也可能后台改动，进而触发notifyItemChange bindData，则动画
        this.isLeft = isLeft
        if (isPost) {
            handleAnimation(ignoreAnimation)
            changeTextColor()
        }
    }

    private fun handleAnimation(ignoreAnimation: Boolean) {
        val bgAnimator: ObjectAnimator
        val newIsLeftOn = isLeft
        bgAnimator = if (newIsLeftOn) {  //从 右边 -> 左边
            ObjectAnimator.ofFloat(mViewBinding.selectBgView, "translationX", 0f)
        } else { //从 true - false
            ObjectAnimator.ofFloat(
                mViewBinding.selectBgView, "translationX",
                0f, (mViewBinding.selectBgView.width + mViewBinding.padding.width).toFloat()
            )
        }
        bgAnimator.duration = if (ignoreAnimation) 0 else 160
        bgAnimator.start()
    }
}