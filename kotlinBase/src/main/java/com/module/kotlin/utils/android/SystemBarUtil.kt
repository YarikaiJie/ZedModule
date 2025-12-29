package com.module.kotlin.utils.android

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.window.layout.WindowMetricsCalculator

enum class TransparentBarType {
    TEXT_WHITE,
    TEXT_DARK,
}

/**
 * 透明状态栏
 *
 * isAppearLightXXX，true表示让bar的文字是黑色的底是白的；false是bar文字是白色的。
 *
 * 谨慎使用：activity和fragment已经通过基础框架默认限定实现；现在只需要在Dialog或者特殊临时切换调用
 * 如果是Activity或者显示在FragmentContainer中的Fragment，
 * 子类覆盖isPaddingNavBar=false   则会让navBar透下去，
 * 子类覆盖isPaddingStatusBar=false则会让statusBar透上去。
 *
 */
fun Activity.transparentStatusBarNew(statusBarType: TransparentBarType?,
                                     navBarType: TransparentBarType?,
                                        insetsBlock: ((
                                       insets: WindowInsetsCompat,
                                       statusBarsHeight: Int,
                                       navigationBarHeight: Int
                                   ) -> WindowInsetsCompat)? = null
) {
    window.run {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowCompat.setDecorFitsSystemWindows(this, false)
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = Color.TRANSPARENT

//        if (Build.VERSION.SDK_INT >= 29) {
//            isStatusBarContrastEnforced = false
//            isNavigationBarContrastEnforced = true
//        }
        val controller = WindowInsetsControllerCompat(this, decorView)
        //当前就是暗黑模式，则无效
        //isAppearanceLightXXX true就表示文字就是黑色的。false就表示文字就是白色的。所以要传入正确的值。
        if(statusBarType != null) controller.isAppearanceLightStatusBars = statusBarType == TransparentBarType.TEXT_DARK
        if(navBarType != null) controller.isAppearanceLightNavigationBars = navBarType == TransparentBarType.TEXT_DARK

        //预留导航栏的空间
        insetsBlock?.let {
            ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
                insetsBlock.invoke(
                    insets,
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                )
            }
        }
    }
}

/**
 * 谨慎使用：activity和fragment已经通过基础框架默认限定实现；现在只需要在Dialog或者特殊临时切换调用
 * 如果是Activity或者Fragment，子类覆盖isPaddingNavBar=false则会让navBar透下去，isPaddingStatusBar=false则会让statusBar透上去。
 *
 */
fun Activity.transparentStatusBarNew(insetsBlock: ((
                                         insets: WindowInsetsCompat,
                                         statusBarsHeight: Int,
                                         navigationBarHeight: Int
                                     ) -> WindowInsetsCompat)? = null
) {
    window.run {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowCompat.setDecorFitsSystemWindows(this, false)
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = Color.TRANSPARENT

//        if (Build.VERSION.SDK_INT >= 29) {
//            isStatusBarContrastEnforced = false
//            isNavigationBarContrastEnforced = true
//        }
        val controller = WindowInsetsControllerCompat(this, decorView)

        //预留导航栏的空间
        insetsBlock?.let {
            ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
                insetsBlock.invoke(
                    insets,
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                )
            }
        }
    }
}

/**
 * 谨慎使用：activity和fragment已经通过基础框架默认限定实现；现在只需要在Dialog或者特殊临时切换调用
 * 如果是Activity或者Fragment，子类覆盖isPaddingNavBar=false则会让navBar透下去，isPaddingStatusBar=false则会让statusBar透上去。
 *
 * 透明状态栏, 必定做全屏；然后设置参数，修改文字颜色。
 * null 代码会自动检测app的uiMode。一般不要去传参，保持null。
 * true 显示黑色文字，即（light模式）。false显示白色文字（即dark模式）。
 */
fun DialogFragment.transparentStatusBarNew(insetsBlock: (
                                 insets: WindowInsetsCompat,
                                 statusBarsHeight: Int,
                                 navigationBarHeight: Int
                             ) -> WindowInsetsCompat = {insets, _, _ -> insets}
) {
    dialog?.window?.run {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowCompat.setDecorFitsSystemWindows(this, false)
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = Color.TRANSPARENT

//        if (Build.VERSION.SDK_INT >= 29) {
//            isStatusBarContrastEnforced = true
//            isNavigationBarContrastEnforced = true
//        }

        //预留导航栏的空间
        ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
            insetsBlock.invoke(
                insets,
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            )
        }
    }
}

@Deprecated("请删除；并阅读transparentStatusBarNew的描述")
inline fun Fragment.transparentStatusBar(
    isBlackStatusBarTextColor: Boolean? = null,
    isBlackNavigationBarTextColor: Boolean? = null,
    crossinline insetsBlock: (
        insets: WindowInsetsCompat,
        statusBarsHeight: Int,
        navigationBarHeight: Int
    ) -> WindowInsetsCompat = {insets, _, _ -> insets}
) {
    requireActivity().window.transparentStatusBar(isBlackStatusBarTextColor, isBlackNavigationBarTextColor, insetsBlock)
}

@Deprecated("请删除；并阅读transparentStatusBarNew的描述")
inline fun Activity.transparentStatusBar(
    isBlackStatusBarTextColor: Boolean? = null,
    isBlackNavigationBarTextColor: Boolean? = null,
    crossinline insetsBlock: (
        insets: WindowInsetsCompat,
        statusBarsHeight: Int,
        navigationBarHeight: Int
    ) -> WindowInsetsCompat = {insets, _, _ -> insets}
) {
    window.transparentStatusBar(isBlackStatusBarTextColor, isBlackNavigationBarTextColor, insetsBlock)
}

@Deprecated("请删除；并阅读transparentStatusBarNew的描述")
inline fun Window.transparentStatusBar(
    isBlackStatusBarTextColor: Boolean? = null,
    isBlackNavigationBarTextColor: Boolean? = null,
    crossinline insetsBlock: (
        insets: WindowInsetsCompat,
        statusBarsHeight: Int,
        navigationBarHeight: Int
    ) -> WindowInsetsCompat = {insets, _, _ -> insets}
) {
    //预留导航栏的空间
    ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
        val bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        insetsBlock.invoke(
            insets,
            top,
            bottom
        )
    }
    //设置系统不要给状态栏和导航栏预留空间，否则无法透明状态栏 全屏传入false
    WindowCompat.setDecorFitsSystemWindows(this, false)
    //设置状态栏颜色透明
    statusBarColor = Color.TRANSPARENT
    //处理状态栏文字颜色
    if (isBlackStatusBarTextColor != null || isBlackNavigationBarTextColor != null) {
        WindowCompat.getInsetsController(this, decorView).apply {
            if (isBlackStatusBarTextColor != null) {
                isAppearanceLightStatusBars = isBlackStatusBarTextColor
            }
            if (isBlackNavigationBarTextColor != null) {
                isAppearanceLightNavigationBars = isBlackNavigationBarTextColor
            }
        }
    }
}

/**
 * 全屏activity监听
 */
fun Window?.fullScreen(
    isFullScreen: Boolean,
    enableCutoutEdges: Boolean = true,/*允许绘制到耳朵区域*/
    statusBar: Boolean = true,/*隐藏状态栏*/
    navigationBar: Boolean = true,/*隐藏导航栏*/
    fitsSystemWindows: Boolean = !isFullScreen
) {
    val window = this ?: return
    val insetsController = WindowCompat.getInsetsController(this, decorView)
    val lp = window.attributes
    if (isFullScreen) {
        // 延伸显示区域到耳朵区
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = if (enableCutoutEdges) {
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            } else {
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            }
            window.attributes = lp
        }
        insetsController.apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (statusBar) {
                hide(WindowInsetsCompat.Type.statusBars())
            }
            if (navigationBar) {
                hide(WindowInsetsCompat.Type.navigationBars())
            }
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            window.attributes = lp
        }
        insetsController.apply {
            if (statusBar) {
                show(WindowInsetsCompat.Type.statusBars())
            }
            if (navigationBar) {
                show(WindowInsetsCompat.Type.navigationBars())
            }
        }
    }
    WindowCompat.setDecorFitsSystemWindows(this@fullScreen, fitsSystemWindows)
}

/**
 * 无需等待界面渲染成功，即在onCreate就可以调用，而且里面已经做了低版本兼容，感谢jetpack window库
 * 获取的就是整个屏幕的高度。包含了statusBar，navigationBar的高度一起。与wm size一致。
 * 这个方法100%可靠。虽然我们看api上描述说低版本是近似值，但是也是最接近最合理的值，不会是0的。
 */
fun Activity.getScreenFullSize() : Pair<Int, Int> {
    val m = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    //computeMaximumWindowMetrics(this) 区别就是多屏，类似华为推上去的效果。不分屏就是一样的。
    return m.bounds.width() to m.bounds.height()
}

/**
 * 必须在activity已经完全渲染之后，一般地，我们是通过
 * ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
 *         val navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
 *         val statusHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
 *
 *  来得到结果的。但是它并不一定会回调，必须调用WindowCompat.setDecorFitsSystemWindows(this, false)。
 *
 *  想要获取，要么，如上，使用transparentStatusBar的方法。
 *  要么，同View.post，再调用本函数获取。
 */
fun Activity.currentStatusBarAndNavBarHeight() : Pair<Int, Int>? {
    val insets = ViewCompat.getRootWindowInsets(window.decorView) ?: return null
    val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
    val sta = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
    return sta to nav
}

fun Activity.myHideSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.myShowSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
}