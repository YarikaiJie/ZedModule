package com.module.kotlin.crash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.database.getStringOrNull
import com.module.kotlin.BaseGlobalConst
import com.module.kotlin.databinding.ActivityUncaughtExceptionBinding
import com.module.kotlin.utils.file.SaveFileType
import com.module.kotlin.utils.android.getAppIntent
import com.module.kotlin.utils.android.hasPermission
import com.module.kotlin.utils.android.openFileByOtherApp
import com.module.kotlin.utils.android.startActivityFix
import com.module.kotlin.utils.ignoreError
import com.module.kotlin.utils.millisToStr
import com.module.kotlin.utils.onClick
import com.module.kotlin.utils.file.saveByteArrayToPublicDirectory
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Field
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 捕获全局异常
 */
class UncaughtExceptionActivity : AppCompatActivity() {
    companion object {
        private const val KEY_INFO = "errorInfo"
        private const val KEY_VERSION = "version"
        fun start(context: Context, t: Thread, e: Throwable) {
            BaseGlobalConst.activityList.forEach {
                it.finish()
            }
            val version = Array(1) {""}
            context.startActivity(Intent(context, UncaughtExceptionActivity::class.java).apply {
                putExtra(KEY_INFO, getErrorInfo(context, e, version))
                putExtra(KEY_VERSION, version[0])
            })
        }

        private fun getErrorInfo(context: Context, e: Throwable, version:Array<String>): String {
            //用于存储设备信息
            val mInfo: MutableMap<String, String> = HashMap()
            val pm: PackageManager = context.packageManager
            val info: PackageInfo =
                pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            // 获取版本信息
            val versionName =
                (if (TextUtils.isEmpty(info.versionName)) "未设置版本名称" else info.versionName) ?: ""
            version[0] = versionName
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode.toString() + ""
            } else {
                info.versionCode.toString() + ""
            }
            mInfo["versionName"] = versionName
            mInfo["versionCode"] = versionCode
            // 获取设备信息
            val fields: Array<Field> = Build::class.java.fields
            if (fields.isNotEmpty()) {
                for (field in fields) {
                    field.isAccessible = true
                    mInfo[field.name] = field.get(null).toString()
                }
            }
            return getErrorStackTrace(mInfo, e)
        }

        private fun getErrorStackTrace(mInfo: MutableMap<String, String>, e: Throwable): String {
            val stringBuffer = StringBuffer()
            stringBuffer.append(
                "${
                    System.currentTimeMillis().millisToStr("yyyy年MM月dd HH:mm:ss")
                }<br><br>"
            )
            stringBuffer.append("------------错误堆栈信息---------<br>")
            val stringWriter = StringWriter()
            val writer = PrintWriter(stringWriter)
            e.printStackTrace(writer)
            var cause = e.cause
            while (cause != null) {
                cause.printStackTrace(writer)
                val nextCause = e.cause
                cause = if (nextCause != cause) {
                    nextCause
                } else {
                    null
                }
            }
            writer.close()
            val string: String = stringWriter.toString()
            stringBuffer.append(string)
            stringBuffer.append("<br><br>------------设备信息---------<br>")
            for ((keyName, value) in mInfo) {
                stringBuffer.append("<b>$keyName：</b>$value<br>")
            }
            return stringBuffer.toString()
        }
    }

    private lateinit var mBinding: ActivityUncaughtExceptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUncaughtExceptionBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        window.statusBarColor = Color.parseColor("#333333")
        window.navigationBarColor = Color.parseColor("#333333")
        initView()
    }

    fun initView() {
        val errorInfo = Html.fromHtml(
            intent.getStringExtra(KEY_INFO)
        )
        val version = intent.getStringExtra(KEY_VERSION)
        mBinding.versionName.text = version
        mBinding.tvInfo.text = errorInfo
        mBinding.btReStart.onClick {
            getAppIntent()?.component?.className?.let {
                startActivityFix(Intent(this, Class.forName(it)))
                finish()
            }
        }

        saveLog(mBinding.btSave, errorInfo.toString())
    }

    private fun saveLog(it: TextView, errorInfo: String) {
        it.setOnClickListener { _ ->
            if (!Manifest.permission.WRITE_EXTERNAL_STORAGE.hasPermission()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                return@setOnClickListener
            }

            it.isEnabled = false
            //格式化时间，作为Log文件名
            val dateFormat: DateFormat =
                SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒", Locale.getDefault())

            val uri = saveByteArrayToPublicDirectory(
                errorInfo.toByteArray(),
                SaveFileType.Download,
                "text/plain",
                "${dateFormat.format(Date())}.txt",
                "${SaveFileType.Download.path}/崩溃日志"
            )
            it.isEnabled = true
            if (uri != null) {
                Toast.makeText(this, "日志保存成功", Toast.LENGTH_SHORT).show()
                it.text = "第三方应用打开"
                it.setOnClickListener {
                    ignoreError {
                        val coursor =
                            this.contentResolver.query(uri, null, null, null, null)
                        if (coursor?.moveToFirst() == true) {
                            var path =
                                coursor.getStringOrNull(coursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                            if (path.isNullOrBlank()) {
                                path =
                                    coursor.getStringOrNull(coursor.getColumnIndex(MediaStore.Downloads.RELATIVE_PATH))
                            }
                            if (!path.isNullOrBlank()) {
                                this.openFileByOtherApp(path)
                            }
                        }
                        coursor?.close()
                    }
                }
            } else {
                Toast.makeText(this, "日志保存失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}