package com.module.kotlin.filelog

import com.module.kotlin.BaseGlobalConst
import java.io.File

/**
@author: Zed.Qiu
@date: 2023/6/9
@description:
 */
object LogFileManager {
    // 是否需要永久打印
    var alwaysDebug = false
    private var _isNeedFileLog = false
    var isNeedLogFile:Boolean
        set(value) {
            _isNeedFileLog = value
        }
        get() {
            if (alwaysDebug) {
                return true
            }
            return _isNeedFileLog
        }
    private val goodFilesDir = BaseGlobalConst.app.getExternalFilesDir(null)?:BaseGlobalConst.app.filesDir

    //还是放在File下吧。FileLog内部有循环删除文件机制。不放在cache下了。
    private var defLogRoot = goodFilesDir.absolutePath + File.separator + "Log"

    fun getLogRoot(): String {
        return defLogRoot
    }

    fun setLogRoot(path: String){
        defLogRoot = path
    }
}