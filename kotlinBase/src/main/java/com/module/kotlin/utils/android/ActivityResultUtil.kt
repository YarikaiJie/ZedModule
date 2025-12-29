package com.module.kotlin.utils.android

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.module.kotlin.BaseGlobalConst

/**
 *StartActivityForResult: 通用的Contract,不做任何转换，Intent作为输入，ActivityResult作为输出，这也是最常用的一个协定。
 *RequestMultiplePermissions： 用于请求一组权限。
 *RequestPermission: 用于请求单个权限。
 *TakePicturePreview: 调用MediaStore.ACTION_IMAGE_CAPTURE拍照，返回值为Bitmap图片。
 *TakePicture: 调用MediaStore.ACTION_IMAGE_CAPTURE拍照，并将图片保存到给定的Uri地址，返回true表示保存成功。
 *TakeVideo: 调用MediaStore.ACTION_VIDEO_CAPTURE 拍摄视频，保存到给定的Uri地址，返回一张缩略图。
 *PickContact: 从通讯录APP获取联系人。
 *GetContent: 提示用选择一条内容，返回一个通过。ContentResolver#openInputStream(Uri)访问原生数据的Uri地址（content://形式） 。默认情况下，它增加了Intent#CATEGORY_OPENABLE, 返回可以表示流的内容。
 *CreateDocument: 提示用户选择一个文档，返回一个(file:/http:/content:)开头的Uri。
 *OpenMultipleDocuments: 提示用户选择文档（可以选择多个），分别返回它们的Uri，以List的形式。
 *OpenDocumentTree: 提示用户选择一个目录，并返回用户选择的作为一个Uri返回，应用程序可以完全管理返回目录中的文档。
 *上面这些预定义的Contract中，除了StartActivityForResult和RequestMultiplePermissions之外，基本都是处理的与其他APP交互，返回数据的场景，比如，拍照，选择图片，选择联系人，打开文档等等。使用最多的就是StartActivityForResult和RequestMultiplePermissions了。
 */

/**
 * 多个权限请求
 */
fun LifecycleOwner.multiplePermissionsForResult() =
    activityResultHelper(lifecycle, ActivityResultContracts.RequestMultiplePermissions())

/**
 * 单个权限请求
 */
fun LifecycleOwner.permissionForResult() =
    activityResultHelper(lifecycle, ActivityResultContracts.RequestPermission())

/**
 * activity跳转结果回调
 */
fun LifecycleOwner.activityForResult() =
    activityResultHelper(lifecycle, ActivityResultContracts.StartActivityForResult())

/**
 * 注册activityResultHelper，必须在生命周期的[onCreate]之前调用，建议在构造函数调用
 */
fun <I, O> activityResultHelper(
    lifecycle: Lifecycle,
    resultContract: ActivityResultContract<I, O>
) =
    ActivityResultHelper(resultContract).also {
        lifecycle.addObserver(it)
    }

/**
 * ActivityResult辅助类
 */
open class ActivityResultHelper<I, O>(val resultContract: ActivityResultContract<I, O>) :
    DefaultLifecycleObserver, ActivityResultCallback<O> {

    private var launcher: ActivityResultLauncher<I>? = null

    val resultLauncher
        get() = launcher

    var onResult: ((O) -> Unit)? = null
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        createLauncher(owner)
    }

    private fun createLauncher(owner: LifecycleOwner) {
        launcher = when (owner) {
            is AppCompatActivity ->{
                owner.registerForActivityResult(resultContract, this)
            }
            is ComponentActivity -> {
                owner.registerForActivityResult(resultContract, this)
            }

            is Fragment -> {
                owner.registerForActivityResult(resultContract, this)
            }

            else -> {
                null
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        onResult = null
        launcher?.unregister()
        launcher = null
    }

    override fun onActivityResult(result: O) {
        onResult?.invoke(result)
    }

    /**
     * 启动activity
     * 通常ActivityOptionsCompat 用于设置进入和退出的动画。
     * ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
     *                 this,
     *                 R.anim.slide_in_right,  // 进入动画
     *                 R.anim.slide_out_left   // 退出动画
     *             );
     */
    open fun launch(
        intent: I,
        option: ActivityOptionsCompat? = null,
        block: (O) -> Unit
    ) {
        this.onResult = block
        launcher?.launch(intent, option)
    }
}

/**
 * 检查是否有权限
 */
fun String.hasPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        BaseGlobalConst.app,
        this
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * 检查是否有权限
 * 返回值，没有权限的集合
 */
fun List<String>.checkPermission(): MutableList<String> {
    val noPermission = mutableListOf<String>()
    forEach {
        if (!it.hasPermission()) {
            noPermission.add(it)
        }
    }
    return noPermission
}

/**
 * 检查是否有权限
 */
fun List<String>.hasPermission(): Boolean {
    return checkPermission().isEmpty()
}

/**
 * 检查是否有权限
 * 返回值，没有权限的集合
 */
fun Array<String>.checkPermission(): MutableList<String> {
    val noPermission = mutableListOf<String>()
    forEach {
        if (!it.hasPermission()) {
            noPermission.add(it)
        }
    }
    return noPermission
}

/**
 * 检查是否有权限
 */
fun Array<String>.hasPermission(): Boolean {
    return checkPermission().isEmpty()
}

/**
 * 跳转到本程序的详情设置处。
 * afterBackAppBlock 表示跳转系统app详情后，回来以后，check权限使用，用户又会点击。所以一般不用管。
 *
 * 一个标准的操作流程：参考EditProfileFragment
 */
fun ActivityResultHelper<Intent, ActivityResult>.jumpToAppDetail(
    appContext: Context,
    afterBackAppBlock: ((ActivityResult) -> Unit)? = null
) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", appContext.packageName, null)
    launch(intent) {
        afterBackAppBlock?.invoke(it)
    }
}

