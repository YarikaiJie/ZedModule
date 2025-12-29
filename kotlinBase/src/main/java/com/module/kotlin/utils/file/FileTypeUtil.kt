package com.module.kotlin.utils.file

import com.module.kotlin.utils.ignoreError
import java.io.FileInputStream
import kotlin.collections.iterator

data class FileTypeInfo(
    var name: String,/*后缀名*/
    var mimeType: String,/*文件类型*/
    var typeHead: String?,/*文件类型的文件头*/
)

/**
 * 获取文件类型
 */
fun getFileType(path: String?): FileTypeInfo? {
    path ?: return null
    val lastDot = path.lastIndexOf(".")
    if (lastDot < 0) {
        return getFileTypeByHead(path)
    }
    val name = path.substring(lastDot + 1).lowercase()
    var info: FileTypeInfo? = null

    fileAllType.let loopBreak@{ allType->
        allType.forEach{
            if (it.name == name) {
                info = it
                return@loopBreak
            }
        }
    }

    return info ?: getFileTypeByHead(path)
}

fun getFileTypeByHead(filePath: String?): FileTypeInfo? {
    filePath ?: return null
    val head = getFileHeader(filePath) ?: return null
    fileAllType.forEach {
        val typeHead = it.typeHead
        if (typeHead != null && head.startsWith(typeHead, true)) {
            return it
        }
    }

    return null
}

/**
 * 获取文件头信息
 *
 * @param filePath
 * @return
 */
private fun getFileHeader(filePath: String?): String? {
    return ignoreError {
        FileInputStream(filePath).use {
            val b = ByteArray(10)
            it.read(b, 0, b.size)
            FileHeadUtil.bytesToHexString(b)
        }
    }
}

val fileAllType by lazy {
    val list = mutableListOf(
        //application
        FileTypeInfo("apk", "application/vnd.android.package-archive", null),
        FileTypeInfo("jar", "application/java-archive", null),
        FileTypeInfo("js", "application/x-javascript", null),
        FileTypeInfo("bin", "application/octet-stream", null),
        FileTypeInfo("class", "application/octet-stream", null),
        FileTypeInfo("exe", "application/octet-stream", null),
        FileTypeInfo("doc", "application/msword", "D0CF11E0"),
        FileTypeInfo("wps", "application/vnd.ms-works", null),
        FileTypeInfo(
            "docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "D0CF11E0"
        ),
        FileTypeInfo("xls", "application/vnd.ms-excel", null),
        FileTypeInfo(
            "xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            null
        ),
        FileTypeInfo("ppt", "application/vnd.ms-powerpoint", null),
        FileTypeInfo(
            "pptx",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            null
        ),
        FileTypeInfo("pdf", "application/pdf", "255044462D312E"),
        FileTypeInfo("gtar", "application/x-gtar", null),
        FileTypeInfo("gz", "application/x-gzip", null),
        FileTypeInfo("tgz", "application/x-compressed", "1F8B08"),
        FileTypeInfo("mpc", "application/vnd.mpohun.certificate", null),
        FileTypeInfo("msg", "application/vnd.ms-outlook", null),
        FileTypeInfo("ogg", "application/ogg", null),
        FileTypeInfo("pps", "application/vnd.ms-powerpoint", null),
        FileTypeInfo("rtf", "application/rtf", "7B5C727466"),
        FileTypeInfo("tar", "application/x-tar", null),
        FileTypeInfo("z", "application/x-compress", null),
        FileTypeInfo("zip", "application/x-zip-compressed", "504B0304"),
        FileTypeInfo("rar", "application/x-rar-compressed", "52617221"),

        //text
        FileTypeInfo("c", "text/plain", null),
        FileTypeInfo("log", "text/plain", null),
        FileTypeInfo("h", "text/plain", null),
        FileTypeInfo("java", "text/plain", null),
        FileTypeInfo("htm", "text/html", "68746D6C3E"),
        FileTypeInfo("html", "text/html", "68746D6C3E"),
        FileTypeInfo("cpp", "text/plain", null),
        FileTypeInfo("conf", "text/plain", null),
        FileTypeInfo("prop", "text/plain", null),
        FileTypeInfo("rc", "text/plain", null),
        FileTypeInfo("sh", "text/plain", null),
        FileTypeInfo("txt", "text/plain", null),
        FileTypeInfo("xml", "text/plain", "3C3F786D6C"),
        //image
        FileTypeInfo("bmp", "image/x-ms-bmp", "424D"),
        FileTypeInfo("gif", "image/gif", "47494638"),
        FileTypeInfo("jpg", "image/jpeg", "FFD8FF"),
        FileTypeInfo("jpeg", "image/jpeg", "FFD8FF"),
        FileTypeInfo("png", "image/png", "89504E47"),
        FileTypeInfo("webp", "image/vnd.wap.wbmp", null),
        //audio
        FileTypeInfo("m3u", "audio/x-mpegurl", null),
        FileTypeInfo("m4a", "audio/mp4a-latm", null),
        FileTypeInfo("m4b", "audio/mp4a-latm", null),
        FileTypeInfo("m4p", "audio/mp4a-latm", null),
        FileTypeInfo("mp2", "audio/x-mpeg", null),
        FileTypeInfo("mp3", "audio/x-mpeg", null),
        FileTypeInfo("mpga", "audio/mpeg", null),
        FileTypeInfo("rmvb", "audio/x-pn-realaudio", null),
        FileTypeInfo("wav", "audio/x-wav", "57415645"),
        FileTypeInfo("wma", "audio/x-ms-wma", null),
        FileTypeInfo("wmv", "audio/x-ms-wmv", null),
        FileTypeInfo("amr", "audio/amr", null),
        FileTypeInfo("awb", "audio/amr-wb", null),
        FileTypeInfo("mid", "audio/midi", "4D546864"),
        FileTypeInfo("xmf", "audio/midi", null),
        FileTypeInfo("rtttl", "audio/midi", null),
        FileTypeInfo("smf", "audio/sp-midi", null),
        FileTypeInfo("imy", "audio/imelody", null),
        FileTypeInfo("pls", "audio/x-scpls", null),
        FileTypeInfo("wpl", "audio/vnd.ms-wpl", null),

        //video
        FileTypeInfo("3gp", "video/3gpp", null),
        FileTypeInfo("3gpp", "video/3gpp", null),
        FileTypeInfo("3g2", "video/3gpp2", null),
        FileTypeInfo("3gpp2", "video/3gpp2", null),
        FileTypeInfo("asf", "video/x-ms-asf", "3026B2758E66CF11"),
        FileTypeInfo("avi", "video/x-msvideo", "41564920"),
        FileTypeInfo("m4u", "video/vnd.mpegurl", null),
        FileTypeInfo("m4v", "video/x-m4v", null),
        FileTypeInfo("mp4", "video/mp4", null),
        FileTypeInfo("mpg4", "video/mp4", null),
        FileTypeInfo("mov", "video/quicktime", "6D6F6F76"),
        FileTypeInfo("mpe", "video/mpeg", null),
        FileTypeInfo("mpeg", "video/mpeg", null),
        FileTypeInfo("mpg", "video/mpeg", null),
    )
    //region 校验一次文件头
    val checkMap = FileHeadUtil.getFileTypeMap()
    list.forEach {
        for ((head, name) in checkMap) {
            if (name.equals(it.name, true)) {
                //更新head
                it.typeHead = head
                break
            }
        }
    }
    //endregion
    list
}