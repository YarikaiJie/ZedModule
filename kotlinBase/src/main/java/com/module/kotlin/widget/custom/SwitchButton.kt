package com.module.kotlin.widget.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.module.kotlin.R
import com.module.kotlin.databinding.ViewSwitchButtonBinding
import com.module.kotlin.utils.onClick
import com.tyiot.module.android.delegate.unsafeLazy


/**
 * @author allan
 * @date :2024/8/29 15:56
 * @description:
 */
class SwitchButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(
        context, attrs, defStyleAttr
    ), ISwitch{
    private var mViewBinding = ViewSwitchButtonBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * 默认就是关闭
     */
    private var _isClosed = true
    override val isClosed: Boolean
        get() = _isClosed

    private var _isInit = false
    override val isInit: Boolean
        get() = _isInit

    /**
     * 是否阻止
     */
    override var abort = false

    private val moveDistance by unsafeLazy {
        val width = width - context.resources.getDimension(R.dimen.switch_btn_padding) * 2
        val btnWidth = mViewBinding.selectBgView.width
        width - btnWidth
    }

    /**
     * 点击切换的回调函数。
     *
     */
    override var valueCallback : ((isClosed:Boolean)->Unit)? = null

    init {
        mViewBinding.root.onClick {
            if (!abort) {
                val newIsClosed = !_isClosed
                setValue(newIsClosed)
                valueCallback?.invoke(newIsClosed)
            }
        }
    }

    override fun initValue(close:Boolean) {
        _isInit = true
        if (!close) { //我们默认true。初始化为false。则需要特殊处理移动下block。
            this._isClosed = false
            post { //直接delay处理，初始化为非左边即可。
                mViewBinding.selectBgView.translationX = moveDistance
                mViewBinding.root.setBackgroundResource(R.drawable.switch_btn_opened)
            }
        }
    }

    override fun setValue(close: Boolean) {
        if (!_isInit) throw RuntimeException()
        if (_isClosed == close) {
            return
        }
        this._isClosed = close
        handleAnimal()
    }

    private fun handleAnimal() {
        val bgAnimator: ObjectAnimator
        val newIsClosed = _isClosed
        bgAnimator = if (newIsClosed) {  //从 右边 -> 左边
            ObjectAnimator.ofFloat(mViewBinding.selectBgView, "translationX", 0f)
        } else { //从 true - false
            ObjectAnimator.ofFloat(
                mViewBinding.selectBgView, "translationX",
                0f, moveDistance
            )
        }
        bgAnimator.duration = 160
        bgAnimator.start()
        if (!_isClosed) {
            mViewBinding.root.setBackgroundResource(R.drawable.switch_btn_opened)
        } else {
            mViewBinding.root.setBackgroundResource(R.drawable.switch_btn_closed)
        }
    }
}