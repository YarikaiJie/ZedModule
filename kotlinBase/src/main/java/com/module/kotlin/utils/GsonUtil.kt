package com.module.kotlin.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 对象转为json格式字符串
 */

val gson: Gson by lazy {
    GsonBuilder().create()
}
val gsonWithNull: Gson by lazy {
    GsonBuilder().serializeNulls().create()
}

fun Any.toJsonString(): String {
    if (this is String) {
        return this
    }
    return gson.toJson(this)
}

fun Any.toJsonStringWithNull(): String {
    if (this is String) {
        return this
    }
    return gsonWithNull.toJson(this)
}

/**
 * json格式字符串，转对象。
 * warning: 该方法存在缺陷，只能解析普通泛型，不能解析List，或者其他二级嵌套。
 * 如果你的T有二级嵌套泛型，请调用formJsonStringLv2One或formJsonStringLv2Two。
 * 或者参考别的代码，使用createGsonParameterizedType。目前实现来讲，formJsonStringLv2One或formJsonStringLv2Two更为简单。
 *
 *         val jsonList = text?.formJsonString<List<ApiDeviceModel>>(createGsonParameterizedType {
 *             arrayOf(ApiDeviceModel::class.java)
 *         })
 *         //新版写法，更为简洁。
 *         val jsonList2 = text?.formJsonStringLv2One<List<ApiDeviceModel>, ApiDeviceModel>()
 *
 *
 *         apiRequest<TyiotApiBean.Page<T>>(
 *             createGsonParameterizedType {
 *                 arrayOf(T::class.java)
 *             }
 */
inline fun <reified T> String.formJsonString(customType: GsonParameterizedType<T>? = null): T {
    if (T::class.java == String::class.java) {
        return this as T
    }
    return gson.fromJson(this, customType ?: object : TypeToken<T>() {}.type)
}

/**
 * 解析接口返回中的 data 列表，兼容多种结构：
 * - data 为数组：直接解析为 List<T>
 * - data 为对象，且包含 list 数组：解析 list 为 List<T>
 * - data 为对象，单对象：解析为 T 并包装为单元素 List
 * - code != 0：返回空列表
 */
inline fun <reified T> String.parseApiListFromData(listKey: String = "list"): List<T> {
    return try {
        val root = JSONObject(this)
        val code = root.optInt("code", -1)
        if (code != 0) return emptyList()
        val dataAny = root.opt("data")
        when (dataAny) {
            is JSONArray -> {
                val type = object : TypeToken<List<T>>() {}.type
                gson.fromJson<List<T>>(dataAny.toString(), type) ?: emptyList()
            }
            is JSONObject -> {
                val listArr = dataAny.optJSONArray(listKey)
                if (listArr != null) {
                    val type = object : TypeToken<List<T>>() {}.type
                    gson.fromJson<List<T>>(listArr.toString(), type) ?: emptyList()
                } else {
                    gson.fromJson<T>(dataAny.toString(), T::class.java)?.let { listOf(it) } ?: emptyList()
                }
            }
            else -> emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }
}
/**
 * json格式字符串，转对象。嵌套的解析方案。二级嵌套1个参数。
 * T为外层bean类，TLv2为嵌套的类。
 */
inline fun <reified T, reified TLv2> String.formJsonStringLv2One(): T {
    if (T::class.java == String::class.java) {
        return this as T
    }
    val typeToken = TypeToken.getParameterized(T::class.java, TLv2::class.java).type
    return gson.fromJson(this, typeToken)
}

fun <T> String.formJsonByClass(t: Class<T>?): T {
    return GsonBuilder().create().fromJson(this, t)
}

///**
// * json格式字符串，转对象。嵌套的解析方案。二级嵌套2个参数。
// * T为外层bean类，TLv2One和TLvTwo为嵌套的二级类型。
// */
//inline fun <reified T, reified TLv2One, reified TLv2Two> String.formJsonStringLv2Two(): T {
//    if (T::class.java == String::class.java) {
//        return this as T
//    }
//    val typeToken = TypeToken.getParameterized(T::class.java, TLv2One::class.java, TLv2Two::class.java).type
//    return gson.fromJson(this, typeToken)
//}

/**
 * 传入实际的泛型类型
 */
inline fun <reified T> createGsonParameterizedType(
    crossinline ownerType: (() -> Type?) = { null },
    crossinline actualTypeArguments: () -> Array<Type>,/*返回T类上的泛型，具体类型*/
): GsonParameterizedType<T> {
    return object : GsonParameterizedType<T>() {
        override fun getActualTypeArguments(): Array<Type> {
            return actualTypeArguments.invoke()
        }

        override fun getRawType(): Type {
            return T::class.java
        }

        override fun getOwnerType(): Type? {
            return ownerType.invoke()
        }
    }
}

//JSONArray扩展函数
fun JSONArray.foreachJSONObject(block:(JSONObject)->Unit) {
    val len = this.length()
    for (i in 0 until len) {
        val one = this.getJSONObject(i)
        block(one)
    }
}

/**
 * 这里泛型只做约束使用
 */
abstract class GsonParameterizedType<T> : ParameterizedType
