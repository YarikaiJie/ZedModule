package com.module.kotlin.mvvm

import com.google.gson.reflect.TypeToken
import com.module.kotlin.BaseGlobalConst
import java.lang.reflect.Type


/**
 * -------------------------------------腾讯替代sp的库-----------------------------------------
 */
private val kv
    get() = BaseGlobalConst.mmkv

/**
 * 移除key
 */
fun mmkvRemoveKey(keyName: String) = kv.removeValueForKey(keyName)

/**
 *  移除key
 */
fun mmkvRemoveKey(vararg keys: String) = kv.removeValuesForKeys(keys)

/**
 * 是否包含key
 */
fun mmkvContainsKey(keyName: String): Boolean = kv.containsKey(keyName)

/**
 * 保存数据
 */
fun mmkvPutValue(key: String, value: Any?) = kvEncode(key, value)

/**
 * 获取数据。强制要求是非空。避免内部null判断出错。
 * 对于纯String?。使用mmkvGetValueStr
 */
fun <T:Any> mmkvGetValue(key: String, defaultValue: T) = kvDecode(key, defaultValue)

/**
 * 对于纯String?。使用这个接口。
 */
fun mmkvGetValueStr(key:String, defaultValue:String?) = kv.decodeString(key, defaultValue)

/**
 * 数据清除
 */
fun mmkvClearAll() = kv.clearAll()

/**
 * 数据清除
 */
fun mmkvClearMemoryCache() = kv.clearMemoryCache()

/**
 * 所有的key
 */
fun mmkvAllKeys() = kv.allKeys()

/**
 * 保存数据
 */
@Suppress("UNCHECKED_CAST")
fun kvEncode(keyName: String, keyValue: Any?): Boolean {
    if (keyValue == null) {
        if (mmkvContainsKey(keyName)) {
            mmkvRemoveKey(keyName)
        }
        return true
    }
    return when (keyValue) {
        is String -> {
            kv.encode(keyName, keyValue)
        }
        is Boolean -> {
            kv.encode(keyName, keyValue)
        }
        is Int -> {
            kv.encode(keyName, keyValue)
        }
        is Float -> {
            kv.encode(keyName, keyValue)
        }
        is Set<*> -> {
            kv.encode(keyName, keyValue as Set<String>)
        }
        is Long -> {
            kv.encode(keyName, keyValue)
        }
        is Double -> {
            kv.encode(keyName, keyValue)
        }
        is ByteArray -> {
            kv.encode(keyName, keyValue)
        }
        else -> {
            kv.encode(keyName, BaseGlobalConst.gson.toJson(keyValue))
        }
    }
}

/**
 * 解析数据
 */
@Suppress("UNCHECKED_CAST")
fun <T> kvDecode(keyName: String, default: T): T {
    if (!mmkvContainsKey(keyName)) {
        return default
    }
    @Suppress("IMPLICIT_CAST_TO_ANY")
    val result = when (default) {
        is String -> {
            kv.decodeString(keyName, default)
        }
        is Boolean -> {
            kv.decodeBool(keyName, default)
        }
        is Int -> {
            kv.decodeInt(keyName, default)
        }
        is Float -> {
            kv.decodeFloat(keyName, default)
        }
        is Set<*> -> {
            kv.decodeStringSet(keyName, default as Set<String>)
        }
        is Long -> {
            kv.decodeLong(keyName, default)
        }
        is Double -> {
            kv.decodeDouble(keyName, default)
        }
        is ByteArray -> {
            kv.decodeBytes(keyName, default)
        }
        else -> {
            val json = kv.decodeString(keyName, null)
            if (json == null) {
                default
            } else {
                BaseGlobalConst.gson.fromJson(json, default!!::class.java) ?: default
            }
        }
    }
    return result as T
}

/**
 * E是列表item的类型
 */
fun <E> mmkvSetArrayList(key:String, value:List<E>) {
    val json = BaseGlobalConst.gson.toJson(value)
    kv.putString(key, json)
}

/**
 * E是列表item的类型
 */
inline fun <reified E> mmkvGetArrayList(key:String) : ArrayList<E> {
    return mmkvGetArrayList(key, E::class.java)
}

/**
 * E是列表item的类型
 */
fun <E> mmkvGetArrayList(key:String, elementClass:Class<E>) : ArrayList<E> {
    val json = BaseGlobalConst.mmkv.getString(key, "")
    if (!json.isNullOrEmpty()) {
        //return gson.fromJson(strJson, TypeToken<List<T>>() {}.getType());
        //改为下面的方法，clazz传入实际想要解析出来的类
        //return BaseGlobalConst.gson.fromJson(json, object : TypeToken<List<T>>() {}.type)
        val listType :Type = TypeToken.getParameterized(ArrayList::class.java, elementClass).type
        return BaseGlobalConst.gson.fromJson(json, listType)
    }

    return ArrayList()
}