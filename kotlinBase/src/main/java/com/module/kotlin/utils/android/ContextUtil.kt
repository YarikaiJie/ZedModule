package com.module.kotlin.utils.android

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import com.module.kotlin.BaseGlobalConst
import com.module.kotlin.utils.GsonParameterizedType
import com.module.kotlin.utils.formJsonString
import com.module.kotlin.utils.file.getFileType
import com.module.kotlin.utils.ignoreErrorBoolean
import java.io.File
import java.security.MessageDigest
import java.util.Locale
import kotlin.system.exitProcess


/**
 *
 * 描述：
 *
 * 创建人：jiale.wei
 * 创建时间：2021/11/25 9:19 上午
 *
 */

/**
 * 回到桌面，不退出app
 */
fun Context.goHome(): Boolean = ignoreErrorBoolean {
    val home = Intent(Intent.ACTION_MAIN)
    home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    home.addCategory(Intent.CATEGORY_HOME)
    startActivityFix(home)
}


/**
 * 跳转到系统浏览器
 */
fun Context.goToWeb(url: String): Boolean = ignoreErrorBoolean {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivityFix(intent)
}

/**
 * 系统分享
 */
fun Context.goToShare(title: String, text: String) = ignoreErrorBoolean {
    var shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    //切记需要使用Intent.createChooser，否则会出现别样的应用选择框，您可以试试
    shareIntent = Intent.createChooser(shareIntent, title)
    startActivityFix(shareIntent)
}

/**
 * 将电话号码显示在拨号盘上
 */
fun Context.goCallPhone(number: String) = ignoreErrorBoolean {
    val intent = Intent(Intent.ACTION_DIAL)
    val data = Uri.parse("tel:$number")
    intent.data = data
    startActivityFix(intent)
}

/**
 * 跳转到应用详情
 */
fun Activity.goAppSettings() = ignoreErrorBoolean {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", packageName, null);
    startActivityFix(intent)
}

/**
 * 跳转到权限设置页面
 */
fun Activity.goAppNotificationSettings() {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
//                intent.putExtra("app_package", getPackageName());
//                intent.putExtra("app_uid", getApplicationInfo().uid);
            startActivityFix(intent)
        } else {
            goAppSettings()
        }
    } catch (e: Exception) {
        goAppSettings()
    }
}

/**
 * 通过appTasks结束所有app
 */
fun Context.finishAllAppTasks(isForeClose: Boolean = false) {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    am.appTasks?.forEach {
        it.finishAndRemoveTask()
    }
    if (isForeClose) {
        exitProcess(-1)
    }
}

/**
 * 结束所有app
 */
fun finishAllActivity(isForeClose: Boolean = false) {
    BaseGlobalConst.activityList.forEach {
        it.finish()
    }
    if (isForeClose) {
        exitProcess(-1)
    }
}

/**
 * 结束某些activity
 */
fun finishActivityOrFragment(vararg classes: Class<*>) {
    BaseGlobalConst.activityList.forEach { activity ->
        classes.forEach { cls ->
            if (activity.javaClass == cls) {
                activity.finish()
            }
//            if (activity is FragmentContainerActivity && activity.fragmentClass == cls) {
//                activity.finish()
//            }
        }
    }
}

/**
 * 清理所有某个子类Fragment的activity。
 */
fun finishFragmentSimilar(vararg classes: Class<*>) {
    BaseGlobalConst.activityList.forEach { activity ->
        classes.forEach { cls ->
//            if (activity is FragmentContainerActivity && activity.fragmentClass != null && cls.isAssignableFrom(activity.fragmentClass)) {
//                activity.finish()
//            }
        }
    }
}

/**
 * 获取打开其他app的intent
 */
fun getAppIntent(packageName: String? = null): Intent? {
    val context = BaseGlobalConst.app
    val intent =
        context.packageManager.getLaunchIntentForPackage(packageName ?: context.packageName)
            ?: return null
    intent.flags =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
    return intent
}

/**
 * 打开应用市场
 */
fun Context.goAppMarket(appPkg: String = packageName, marketPkg: String? = null): Boolean {
    return ignoreErrorBoolean {
        val uri = Uri.parse("market://details?id=$appPkg")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (!TextUtils.isEmpty(marketPkg)) {
            intent.setPackage(marketPkg)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityFix(intent)
    }
}

/**
 * 打开谷歌应用商店
 */
fun Context.goGooglePlay(appPkg: String = packageName) = goAppMarket(appPkg, "com.android.vending")

fun Context.goSamSungPlay(appPkg: String = packageName) {
    val uri = Uri.parse("samsungapps://ProductDetail/$appPkg")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.sec.android.app.samsungapps")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivityFix(intent)
}

/**
 * 是否竖屏
 */
fun Resources.isScreenPortrait(): Boolean {
    return displayMetrics.widthPixels < displayMetrics.heightPixels
}

/**
 * 是否竖屏
 */
fun View.isScreenPortrait(): Boolean {
    return resources.isScreenPortrait()
}

/**
 * 是否竖屏
 */
fun Activity.isScreenPortrait(): Boolean {
    return resources.isScreenPortrait()
}

/**
 * 读取assets的文件
 */
fun Context.readAssetsFileToText(fileName: String?): String? {
    fileName ?: return null
    return try {
        val reader = assets.open(fileName).bufferedReader()
        val text = reader.readText()
        reader.close()
        text
    } catch (e: Throwable) {
       e.printStackTrace()
        null
    }
}

/**
 * 读取assets的文件
 * 转换为对象
 */
inline fun <reified T> Context.readAssetsFileFromJson(
    fileName: String?,
    customType: GsonParameterizedType<T>? = null
): T? {
    val text = readAssetsFileToText(fileName) ?: return null
    return try {
        text.formJsonString(customType)
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

/**
 * 代码获取attr
 * 例如：xml中我们这样写         android:background="?selectableItemBackground"
 *  代码中   context.getAttrValue(intArrayOf(R.attr.selectableItemBackground)){
 *          it.getDrawable(R.attr.selectableItemBackground)
 *          }
 */

fun Context.getAttrValue(
    attrsArray: IntArray,
    onGetTypeArray: Function3<
            @ParameterName("typedArray") TypedArray,
            @ParameterName("attr") Int,
            @ParameterName("index") Int,
            Unit>
) {
    val typedArray: TypedArray = obtainStyledAttributes(attrsArray)
    repeat(attrsArray.count()) {
        onGetTypeArray.invoke(typedArray, attrsArray[it], it)
    }
    typedArray.recycle()
}

/**
 * 是否为夜间模式
 */
fun Resources.isUiNightModel(): Boolean {
    val nightModeFlags: Int = configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
//    when (nightModeFlags) {
//        Configuration.UI_MODE_NIGHT_YES,
//        Configuration.UI_MODE_NIGHT_NO,
//        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
//        }
//    }
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}

/**
 * 是否为夜间模式
 */
fun Activity.isUiNightModel(): Boolean {
    return resources.isUiNightModel()
}

/**
 * 是否为夜间模式
 */
fun View.isUiNightModel(): Boolean {
    return resources.isUiNightModel()
}

/**
 * 设置为夜间模式
 * isNightMode null, 代表模式自动
 */
fun AppCompatActivity.setUiNightModel(isNightMode: Boolean?, canReCreate: Boolean = true) {
    if (isNightMode == null) {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        if (canReCreate) {
            recreate()
        }
        return
    }
    if (isNightMode) {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
    } else {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
    }
    if (canReCreate) {
        recreate()
    }
}

/**
 * 获取openSsl haskKey
 * 比如facebook的login需要这个
 */
fun Context.getAppKeyHash(): MutableList<String> {
    val keyHashList = mutableListOf<String>()
    try {
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo?.apkContentsSigners
        } else {
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
        }
        signatures?.forEach { signature ->
            val md = MessageDigest.getInstance("SHA1")
            md.update(signature.toByteArray())
            val keyHash =
                Base64.encodeToString(md.digest(), Base64.DEFAULT)
            keyHashList.add(keyHash)
        }
        return keyHashList
    } catch (error: Throwable) {
        error.printStackTrace()
        return keyHashList
    }
}

/**
 *获取签名的sha1
 * 比如百度地图就需要这个
 */
fun Context.getAppSHA1(): MutableList<String>? {
    return getMessageDigest("SHA1")
}

/**
 *获取签名的sha256
 */
fun Context.getAppSHA256(): MutableList<String>? {
    return getMessageDigest("SHA256")
}

fun Context.getMessageDigest(algorithm: String): MutableList<String>? {
    val shals = mutableListOf<String>()
    try {
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo?.apkContentsSigners
        } else {
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
        }
        signatures?.forEach {
            val cert: ByteArray = it.toByteArray()
            val md: MessageDigest = MessageDigest.getInstance(algorithm)
            val publicKey: ByteArray = md.digest(cert)
            val hexString = StringBuffer()
            for (i in publicKey.indices) {
                val appendString = Integer.toHexString(0xFF and publicKey[i].toInt())
                    .uppercase(Locale.US)
                if (appendString.length == 1) hexString.append("0")
                hexString.append(appendString)
                hexString.append(":")
            }
            val result = hexString.toString()
            val sha1 = result.substring(0, result.length - 1)
            shals.add(sha1)
        }
        return shals
    } catch (e: Throwable) {
        e.printStackTrace()
        return shals
    }
}
//
///**
// * 安装App
// */
//fun Context?.installApk(oldFile: File?, allowsCreateTemp: Boolean = false/*不论文件是否存在，总是创建临时文件来操作*/) {
//    openFileByOtherApp(oldFile, "application/vnd.android.package-archive", allowsCreateTemp)
//}
//
///**
// * 安装App
// */
//fun Context?.installApk(uri: Uri?) {
//    openUriByOtherApp(uri, "application/vnd.android.package-archive")
//}
//
///**
// * 通过第三方app，打开uri
// */
//fun Context?.openUriByOtherApp(
//    uri: Uri?,
//    type: String? = null
//) {
//    uri ?: return
//    this ?: return
//    val intent = Intent(Intent.ACTION_VIEW)
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    //判读版本是否在7.0以上
//    if (Build.VERSION.SDK_INT >= 24) {
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        intent.setDataAndType(uri, type)
//    } else {
//        intent.setDataAndType(uri, type)
//    }
//    startActivityFix(intent)
//}

/**
 * 通过第三方app，打开文件
 */
fun Context?.openFileByOtherApp(
    filePath: String?,
    type: String? = getFileType(filePath)?.mimeType,
    allowsCreateTemp: Boolean = false/*不论文件是否存在，总是创建临时文件来操作*/
) {
    filePath ?: return
    openFileByOtherApp(File(filePath), type, allowsCreateTemp)
}

/**
 * 通过第三方app，打开文件
 */
fun Context?.openFileByOtherApp(
    oldFile: File?,
    type: String? = getFileType(oldFile?.path)?.mimeType,
    allowsCreateTemp: Boolean = false/*不论文件是否存在，总是创建临时文件来操作*/
) {
    oldFile ?: return
    this ?: return
    val file = if (!allowsCreateTemp && oldFile.exists()) {
        oldFile
    } else {
        val temp =
            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path + "/openfilebyotherapp_tmep/${oldFile.name}")
        if (!temp.exists()) {
            temp.createNewFile()
        }
        temp.writeBytes(oldFile.readBytes())
        temp
    }
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    //判读版本是否在7.0以上
    if (Build.VERSION.SDK_INT >= 24) {
        val apkUri =
            FileProvider.getUriForFile(this, "${packageName}.kotlin.fileprovider", file)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, type)
    } else {
        intent.setDataAndType(Uri.fromFile(file), type)
    }
    startActivityFix(intent)
}

