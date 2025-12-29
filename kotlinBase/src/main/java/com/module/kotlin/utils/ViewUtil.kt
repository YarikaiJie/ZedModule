package com.module.kotlin.utils

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.LineHeightSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.ViewPropertyAnimator
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.module.kotlin.BaseGlobalConst
import java.lang.ref.WeakReference

/**
 * 设置点击事件
 * 支持设置点击间隔
 */
fun View.onClick(space: Long? = null, block: ((View) -> Unit)?) {
    if (block == null) {
        this.setOnClickListener(null)
        return
    }

    this.setOnClickListener {
        if (!IOnSingleClickListener.acceptClick(space)) {
            return@setOnClickListener
        }
        block.invoke(it)
    }
}

interface IOnSingleClickListener : View.OnClickListener {
    companion object {
        // 默认点击间隔
        var doubleClickSpace: Long = 300L
        /**
         * 上一次按钮点击时间
         */
        private var lastClickTime = 0L

        fun acceptClick(spaceTime:Long? = null) : Boolean {
            val space = spaceTime ?: doubleClickSpace
            val cur = System.currentTimeMillis()
            if (cur - lastClickTime < space) {
                return false
            }
            lastClickTime = cur
            return true
        }
    }

    /**
     * 统一处理快速点击的情况
     */
    override fun onClick(v: View?) {
        if (!acceptClick()) {
            return
        }
        v?.let {
            onSingleClick(it)
        }
    }

    /**
     * 点击事件监听
     */
    fun onSingleClick(view: View) {}
}


fun getStrRes(@StringRes res: Int): String {
    return BaseGlobalConst.app.getString(res)
}
fun getColorRes(@ColorRes res: Int): Int {
    return ContextCompat.getColor(BaseGlobalConst.app, res)
}

/**
 * 设置文字颜色
 */
fun TextView.setTextColorResource(@ColorRes id: Int) {
    setTextColor(ContextCompat.getColor(context, id))
}


/**
 * 描述：view扩展方法
 *    textClickListener非内存泄漏解法
 *     private class OnClickResendEmailListener(fragment: XXXFragment) : Function1<View, Unit> {
        private val fragmentRef:WeakReference<XXXFragment> = WeakReference(fragment)
            override fun invoke(p1: View) {
                fragmentRef.get()?.onClick(p1)
            }
        }
 */
//@Deprecated("请注意textClickListener的用法。并非过期。而是标记提醒。")
data class SpanBean(
    var text: String?,/*要改变的文本效果*/
    var textColor: Int? = null,/*文字颜色*/
    var textSize: Int? = null,/*文字大小*/
    @Deprecated("加上过期标记并非不准使用，注意事项：不得在函数体内引用外部类的变量或者context。" +
            "如需context，直接调用view.context；如果涉及其他变量，则必须采用自定义类和构造函数weakRef持有。")
    var textClickListener: Function1<View, Unit>? = null,/*文字点击*/
    var typeface: Int? = null,/*eg:[Typeface.BOLD_ITALIC]*/
    var isBold: Boolean? = null,/*是否加粗*/
    var lineHeight: Int? = null,/*行高*/
    var gravity: Int? = null,/*位置*/
    var url: Pair<String?, View.OnClickListener?>? = null,/*Pair<"https://baidu.com",listener>*/
    var drawable: Drawable? = null,/*设置图片的话，文字将会失效*/
    val drawableSize:Int? = null,
    var isUnderlineText: Boolean? = null,/*是否需要下划线*/
    var underlineColor: Int? = null,/*下划线颜色*/
    var strikethroughSpan: Boolean? = null,/*删除线*/
    var backgroundColor: Int? = null,/*背景色*/
    var spanWhatList: List<Any>? = null,/*支持其他未被定义的CharacterStyle,或者其他*/
)

//    tv.textFromSpanBean(mutableListOf(
//                SpanBean("这个"),
//                SpanBean("百度", url = Pair("https://baidu.com", null), textColor = Color.BLUE),
//                SpanBean("百123", drawable = getResDrawable(R.drawable.scroll_to_top)),
//                SpanBean("真的好垃圾", typeface = Typeface.BOLD_ITALIC),
//                SpanBean("下划线", typeface = Typeface.BOLD_ITALIC, textColor = Color.RED, underlineSpan = UnderlineSpan()),
//                SpanBean("删除线", typeface = Typeface.BOLD_ITALIC, textColor = Color.RED, underlineSpan = UnderlineSpan(),
//                        strikethroughSpan = StrikethroughSpan()),
//        ))

fun TextView?.textFromSpanBean(from:String, vararg spanBeans: SpanBean) {
    textFromSpanBean(from, spanBeans.asList())
}

fun TextView?.textFromSpanBean(from:String, textList: List<SpanBean>) {
    this ?: return
    val ssb = SpannableStringBuilder()
    textList.forEach {
        if (it.text != null) {
            val sbText = SpannableStringBuilder(it.text)
            /**
             * 设置url
             */
            it.url?.first?.let { url ->
                movementMethod = LinkMovementMethod.getInstance()
                val second = it.url?.second
                val listenerRef = if(second != null) WeakReference(second) else null
                sbText.setSpan(CustomUrlSpan(listenerRef, url), it)
            }
            /**
             * 设置图片
             */
            it.drawable?.let { drawable ->
                drawable.setBounds(0, 0, it.drawableSize ?: drawable.intrinsicWidth, it.drawableSize ?: drawable.intrinsicHeight)
                val span = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ImageSpan(
                        drawable, when (it.gravity) {
                            Gravity.TOP -> ImageSpan.ALIGN_BASELINE
                            Gravity.BOTTOM -> ImageSpan.ALIGN_BOTTOM
                            else -> ImageSpan.ALIGN_CENTER
                        }
                    )
                } else {
                    CenterImageSpan(drawable)
                }
                it.text = "0"
                sbText.clear()
                sbText.append(it.text)
                sbText.setSpan(span, it)
            }
            /**
             * 如果点击不为null，则按照下面方式这是文字颜色和下划线
             */
            if (it.textClickListener != null) {
                it.textClickListener?.let { click ->
                    //这个一定要记得设置，不然点击不生效
                    movementMethod = LinkMovementMethod.getInstance()
                    sbText.setSpan(CustomClickableSpan(click,
                        underLineColor = it.underlineColor,
                        textColor = it.textColor,
                        isUnderlineText = it.isUnderlineText)
                        , it)
                }
            } else {
                it.textColor?.let { textColor ->
                    sbText.setSpan(CustomForegroundColorSpan(textColor=textColor,
                        underlineColor = it.underlineColor,
                        isUnderlineText = it.isUnderlineText), it)
                }
            }

            /**
             * 文字大小
             */
            it.textSize?.let { textSize ->
                sbText.setSpan(AbsoluteSizeSpan(textSize, true), it)
            }
            /**
             * 文字样式
             */
            it.typeface?.let { typeface ->
                sbText.setSpan(StyleSpan(typeface), it)
            }
            /**
             * 删除线
             */
            if (it.strikethroughSpan == true) {
                sbText.setSpan(StrikethroughSpan(), it)
            }
            /**
             * 背景色
             */
            it.backgroundColor?.let { backgroundColor ->
                sbText.setSpan(BackgroundColorSpan(backgroundColor), it)
            }
            /**
             * 其他span
             */
            it.spanWhatList?.forEach { what ->
                sbText.setSpan(what, it)
            }
            /**
             * 是否加粗
             */
            it.isBold?.let { isBold ->
                sbText.setSpan(CustomCharacterStyle(isBold), it)
            }
            /**
             * 行高
             */
            it.lineHeight?.let { lineHeight ->
                sbText.setSpan(TextHeightSpan(lineHeight, it.gravity), it)
            }

            ssb.append(sbText)
        }
    }
    text = ssb

    //想去掉点击后文字背景，设置一下HighlightColor即可
    highlightColor = Color.TRANSPARENT
}

class CustomForegroundColorSpan(private val textColor: Int, private val underlineColor:Int?, private val isUnderlineText:Boolean?) : ForegroundColorSpan(textColor) {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        (underlineColor ?: textColor).let { color ->
            textPaint.color = color
        }
        isUnderlineText?.let {
            textPaint.isUnderlineText = it
        }
    }
}

class CustomClickableSpan(private val click:((View) -> Unit)?,
                          private val underLineColor:Int?,
                          private val textColor:Int?,
                          private val isUnderlineText:Boolean?) : ClickableSpan() {
    override fun onClick(widget: View) {
        click?.invoke(widget)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        (underLineColor ?: textColor)?.let { color ->
            ds.color = color
        }
        isUnderlineText?.let {
            ds.isUnderlineText = it
        }
    }
}

class CustomUrlSpan(private val listenerRef:WeakReference<View.OnClickListener>?, url:String) : URLSpan(url) {
    override fun onClick(widget: View) {
        super.onClick(widget)
        val listener = listenerRef?.get()
        listener?.onClick(widget)
    }
}

class CustomCharacterStyle(private val isFakeBoldText:Boolean) : CharacterStyle() {
    override fun updateDrawState(tp: TextPaint?) {
        tp?.isFakeBoldText = isFakeBoldText
    }
}

private fun SpannableStringBuilder.setSpan(what: Any, spanBean: SpanBean) {
    val text = spanBean.text ?: return
    if (text.isNotEmpty()){
        setSpan(what, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

open class CenterImageSpan : ImageSpan {
    constructor(context: Context, bitmap: Bitmap) : super(context, bitmap)
    constructor(context: Context, bitmap: Bitmap, verticalAlignment: Int) : super(
        context,
        bitmap,
        verticalAlignment
    )

    constructor(drawable: Drawable) : super(drawable)
    constructor(drawable: Drawable, verticalAlignment: Int) : super(drawable, verticalAlignment)
    constructor(drawable: Drawable, source: String) : super(drawable, source)
    constructor(drawable: Drawable, source: String, verticalAlignment: Int) : super(
        drawable,
        source,
        verticalAlignment
    )

    constructor(context: Context, uri: Uri) : super(context, uri)
    constructor(context: Context, uri: Uri, verticalAlignment: Int) : super(
        context,
        uri,
        verticalAlignment
    )

    constructor(context: Context, resourceId: Int) : super(context, resourceId)
    constructor(context: Context, resourceId: Int, verticalAlignment: Int) : super(
        context,
        resourceId,
        verticalAlignment
    )

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val b = drawable
        val fm = paint.fontMetricsInt
        val transY = ((y + fm.descent + y + fm.ascent) / 2
                - b.bounds.bottom / 2)
        canvas.save()
        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }
}

/**
 * 文本高度span,针对单行，多行可能有问题
 */
class TextHeightSpan(private val height: Int, private val gravity: Int? = null) : LineHeightSpan {
    override fun chooseHeight(
        text: CharSequence, start: Int, end: Int,
        spanstartv: Int, lineHeight: Int,
        fm: FontMetricsInt
    ) {
        val originHeight = fm.descent - fm.ascent
        if (originHeight <= 0) {
            return
        }
        when (gravity) {
            Gravity.TOP -> {
                fm.descent += (height - originHeight)
            }
            Gravity.BOTTOM -> {
                fm.ascent -= (height - originHeight)
            }
            else -> {
                fm.ascent -= height / 2
                fm.descent += (height / 2)
            }
        }
    }
}

/**
 * view隐藏
 */
fun View?.gone() {
    this ?: return
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

/**
 * 通过变量控制是否隐藏
 */
fun View?.setGone(visible: Boolean) {
    this ?: return
    if (visible) {
        visible()
    } else {
        gone()
    }
}

/**
 * 通过变量控制是否隐藏
 */
fun View?.setVisible(visible: Boolean) {
    this ?: return
    if (visible) {
        visible()
    } else {
        invisible()
    }
}

/**
 * 通过变量控制是否隐藏
 */
fun View?.setInvisible(visible: Boolean) {
    this ?: return
    if (visible) {
        visible()
    } else {
        invisible()
    }
}

/**
 * view可见
 */
fun View?.visible() {
    this ?: return
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

/**
 * view隐藏
 */
fun View?.invisible() {
    this ?: return
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

/**
 * view是否可见
 */
fun View?.isVisible(): Boolean {
    this ?: return false
    return visibility == View.VISIBLE
}

/**
 * view是否可见
 */
fun View?.isInVisible(): Boolean {
    this ?: return true
    return visibility == View.INVISIBLE
}

/**
 * view是否隐藏
 */
fun View?.isGone(): Boolean {
    this ?: return true
    return visibility == View.GONE
}

fun getDisplayMetrics(): DisplayMetrics {
    val appDensity = BaseGlobalConst.app.resources.displayMetrics
    return appDensity
}

/**
 * dp2px
 */
fun dip(value: Int): Int {
    val appDensity = getDisplayMetrics().density
    return (value * appDensity).toInt()
}

fun dip(value: Double): Double {
    val appDensity = getDisplayMetrics().density
    return value * appDensity
}

fun sp(value: Float): Float {
    val scaledDensity = getDisplayMetrics().scaledDensity
    return (value * scaledDensity)
}

/**
 * dp2px
 */
fun dip(value: Float): Float {
    val appDensity = getDisplayMetrics().density
    return value * appDensity
}

val Int.dp: Int
    get() = dip(this)

val Float.dp: Float
    get() = dip(this)

val Double.dp: Double
    get() = dip(this)

val Int.sp: Int
    get() = sp(this.toFloat()).toInt()

val Float.sp: Float
    get() = sp(this)

val Double.sp: Double
    get() = sp(this.toFloat()).toDouble()

fun Any?.sp2Px(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, requireDisplayMetrics())
}

fun Any?.dp2Px(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, requireDisplayMetrics())
}

fun Any?.dp2Px(value: Int): Int {
    return dp2Px(value.toFloat()).toInt()
}

fun Any?.requireDisplayMetrics(): DisplayMetrics {
    return when (this) {
        is Context -> {
            this.resources.displayMetrics
        }
        is View -> {
            this.resources.displayMetrics
        }
        is Fragment -> {
            this.resources.displayMetrics
        }
        is PopupWindow -> {
            (this.contentView?.resources ?: BaseGlobalConst.app.resources).displayMetrics
        }
        else -> {
            BaseGlobalConst.app.resources.displayMetrics
        }
    }
}

fun ViewGroup.inflate(@LayoutRes id: Int, isAttach: Boolean = false): View {
    return LayoutInflater.from(this.context).inflate(id, this, isAttach)
}

/**
 * 设置textView从资源文件
 */
fun TextView?.setIcon(
    @DrawableRes drawableRes: Int?,
    gravity: Int = Gravity.START,
    drawablePaddingDip: Int = -1
) {
    this ?: return
    val drawable = if (drawableRes == null) {
        null
    } else {
        ContextCompat.getDrawable(context, drawableRes)
    }
    setIcon(drawable, gravity, drawablePaddingDip)
}

fun TextView?.setIcon(
    drawable: Drawable?,
    gravity: Int = Gravity.START,
    drawablePaddingDip: Int = -1
) {
    this ?: return
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    if (drawablePaddingDip >= 0) {
        this.context
        compoundDrawablePadding = dip(drawablePaddingDip)
    }
    when (gravity) {
        Gravity.TOP -> {
            setCompoundDrawables(
                compoundDrawables[0],
                drawable,
                compoundDrawables[2],
                compoundDrawables[3]
            )
        }
        Gravity.BOTTOM -> {
            setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                compoundDrawables[2],
                drawable
            )
        }
        Gravity.END, Gravity.RIGHT -> {
            setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                drawable,
                compoundDrawables[3]
            )
        }
        else -> {
            setCompoundDrawables(
                drawable,
                compoundDrawables[1],
                compoundDrawables[2],
                compoundDrawables[3]
            )
        }
    }
}

/**
 * 监听文本输入之后
 */
fun EditText?.afterTextChanged(onAfterTextChanged: Function1<Editable?, Unit>) {
    this ?: return
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onAfterTextChanged.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

/**
 * 文本输入之前监听
 */
fun EditText?.beforeTextChanged(beforeTextChanged: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit) {
    this ?: return
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

/**
 * 文本改变监听
 */
fun EditText?.onTextChanged(onTextChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit) {
    this ?: return
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s, start, before, count)
        }

    })
}

/**
 * 指定搜索键并支持触发
 */
inline fun EditText.onSearch(crossinline search: (text: String) -> Unit) {
    this.onAction(EditorInfo.IME_ACTION_SEARCH, search)
}

inline fun EditText.onAction(
    imeOptions: Int,
    crossinline search: (text: String) -> Unit
) {
    this.imeOptions = imeOptions
    this.setSingleLine()
    this.setOnEditorActionListener { v, actionId, event ->
        if (actionId == imeOptions) {
            search.invoke(v.text.toString())
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener true
    }
}

/**
 * 设置最大长度
 */
fun EditText.setMaxLength(max: Int) {
    addFilters(InputFilter.LengthFilter(max))
}

/**
 * 所有字母大写
 */
fun EditText?.allCaps() {
    addFilters(InputFilter.AllCaps())
}

/**
 * 在之前的基础上，新增一个或者多个InputFilter
 */
fun EditText?.addFilters(vararg filter: InputFilter) {
    this ?: return
    if (filter.isNullOrEmpty()) {
        return
    }
    val old = this.filters
    val new = arrayOf<InputFilter>(*old, *filter)
    this.filters = new
}

/**
 * 有时候，我们的需求，需要收入手机号码的时候，
 * 需用空格分开(133 3333 3333)
 */
fun EditText?.phoneNumberFormattingTextWatcher() {
    this?.addTextChangedListener(PhoneNumberFormattingTextWatcher())
}


/**
 * 密码可见不可见
 */
fun EditText?.visiblePassword(visible: Boolean) {
    this ?: return
    transformationMethod = if (visible) {
        //选择状态 显示明文--设置为可见的密码
        //mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        HideReturnsTransformationMethod.getInstance()
    } else {
        //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
        //mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        PasswordTransformationMethod.getInstance()
    }
    setSelection(text.count())
}

/**
 * 是否已经滚动到顶部
 */
fun View?.hadScrollToTop(): Boolean {
    this ?: return false
    return !this.canScrollVertically(-1)
}

/**
 * 是否已经滚动到底部
 */
fun View?.hadScrollToBottom(): Boolean {
    this ?: return false
    return !this.canScrollVertically(1)
}

/**
 * 是否已经滚动到左边
 */
fun View?.hadScrollToStart(): Boolean {
    this ?: return false
    return !this.canScrollHorizontally(-1)
}

/**
 * 是否已经滚动到右边
 */
fun View?.hadScrollToEnd(): Boolean {
    this ?: return false
    return !this.canScrollHorizontally(1)
}

/**
 * 查找view所依赖的fragment
 */
fun <F : Fragment> View.findFragment(): F? {
    return ignoreError { FragmentManager.findFragment(this) }
}

/**
 * 获取与FragmentContainerView绑定的fg
 */
fun <F : Fragment> FragmentContainerView?.getBindFragment(fragment: Fragment?): F? {
    this ?: return null
    return fragment?.childFragmentManager.findFragment(this.id)
}

fun <F : Fragment> FragmentContainerView?.getBindFragment(activity: AppCompatActivity?): F? {
    this ?: return null
    return activity?.supportFragmentManager.findFragment(this.id)
}

/**
 * 根据id查找fragment
 */
fun <F : Fragment> FragmentManager?.findFragment(@IdRes id: Int): F? {
    return this?.findFragmentById(id) as? F
}

@SuppressLint("ClickableViewAccessibility")
fun View.scaleAnimByTouchListener() {
    setOnTouchListener { v, event ->
        v.scaleAnimByMotionEvent(event)
        false
    }
}

fun View.scaleAnimByMotionEvent(
    ev: MotionEvent?,
    smallScale: Float = 0.8f,
    bigScale: Float = 1f,
    build: ((ViewPropertyAnimator) -> Boolean)? = null
) {
    when (ev?.action) {
        MotionEvent.ACTION_DOWN -> {
            startScaleAnim(smallScale, build)
        }
        MotionEvent.ACTION_UP,
        MotionEvent.ACTION_CANCEL -> {
            startScaleAnim(bigScale, build)
        }
    }
}

fun View.startScaleAnim(
    endScale: Float,
    build: ((ViewPropertyAnimator) -> Boolean/*返回true，需要手动调用start()*/)?
) {
    animate().cancel()
    pivotX = width.toFloat() / 2
    pivotY = height.toFloat() / 2
    val anim = animate()
        .scaleX(endScale)
        .scaleY(endScale)
        .setDuration(200)
    if (build?.invoke(anim) != true) {
        anim.start()
    }
}

/**
 * 移除recyclerView光晕效果
 */
fun ViewPager2.overScrollNever() {
    this.children.forEach {
        if (it is RecyclerView) {
            it.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }
}

/**
 * 设置角标
 */
@ExperimentalBadgeUtils
fun View.attachBadgeDrawable(
    badgeDrawable: BadgeDrawable? = null,
    customBadgeParent: FrameLayout? = null
): BadgeDrawable {
    val drawable = badgeDrawable ?: BadgeDrawable.create(context)
    drawable.isVisible = true
    BadgeUtils.attachBadgeDrawable(drawable, this, customBadgeParent)
    return drawable
}

/**
 * 设置角标
 */
@ExperimentalBadgeUtils
fun Toolbar.attachBadgeDrawable(
    @IdRes menuItemId: Int,
    badgeDrawable: BadgeDrawable? = null,
    customBadgeParent: FrameLayout? = null
): BadgeDrawable {
    val drawable = badgeDrawable ?: BadgeDrawable.create(context)
    drawable.isVisible = true
    BadgeUtils.attachBadgeDrawable(drawable, this, menuItemId, customBadgeParent)
    return drawable
}

/**
 * 移除角标
 */
@ExperimentalBadgeUtils
fun Toolbar.detachBadgeDrawable(
    @IdRes menuItemId: Int,
    badgeDrawable: BadgeDrawable?,
) {
    BadgeUtils.detachBadgeDrawable(badgeDrawable, this, menuItemId)
}

/**
 * 移除角标
 */
@ExperimentalBadgeUtils
fun View.detachBadgeDrawable(
    badgeDrawable: BadgeDrawable?,
) {
    BadgeUtils.detachBadgeDrawable(badgeDrawable, this)
}

/**
 * 给view设置圆角
 */
fun View.setViewRadius(radius: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            view?.let {
                outline?.setRoundRect(0, 0, it.width, it.height, radius)
            }
        }
    }
}

/**
 * 遍历所有子view
 */
fun View?.forEachChild(action: ((View) -> Unit)) {
    if (this == null) {
        return
    }
    action.invoke(this)
    if (this is ViewGroup) {
        this.forEach {
            it.forEachChild(action)
        }
    }
}

/**
 * 查找所有的parent,
 * 返回true，就不在向上查找
 */
tailrec fun View?.forEachParent(action: ((View) -> Boolean)) {
    val p = this?.parent ?: return
    if (p is View) {
        if (action.invoke(p)) {
            return
        }
        p.forEachParent(action)
    }
}

/**
 * 从资源文件获取颜色值
 */
fun getColorCompat(@ColorRes colorRes: Int, context: Context? = null) =
    ContextCompat.getColor(context ?:  BaseGlobalConst.app, colorRes)

/**
 * 从资源文件获取文字
 */
fun getStringCompat(@StringRes stringRes: Int, context: Context? = null) =
    (context ?: BaseGlobalConst.app).getString(stringRes)

/**
 * 从资源文件获取文字
 */
fun getTextCompat(@StringRes stringRes: Int, context: Context? = null) =
    (context ?: BaseGlobalConst.app).getText(stringRes)

/**
 * 获取图片
 */
fun getDrawableCompat(@DrawableRes drawableRes: Int, context: Context? = null) =
    ContextCompat.getDrawable(context ?: BaseGlobalConst.app, drawableRes)

/**
 * 获取内容布局
 */
val Window.contentView: FrameLayout
    get() = decorView.findViewById(R.id.content)

/**
 * 通过outlineProvider和setClipToOutline来给View设置圆角。
 */
fun View.setOutlineProviderRoundCorner(radius:Float) {
    val provider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            val leftMargin = 0
            val topMargin = 0
            val selfRect = Rect(
                leftMargin, topMargin,
                rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin
            )
            outline.setRoundRect(selfRect, radius)
        }
    }

    this.outlineProvider = provider
    this.setClipToOutline(true)
}