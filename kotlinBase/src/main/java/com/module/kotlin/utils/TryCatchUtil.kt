package com.module.kotlin.utils

import com.module.kotlin.filelog.logD

/**
 * 忽略错误,默认不提示异常
 */
inline fun <T> ignoreError(
    errorBlock: ((Throwable) -> T?) = { null },
    finallyBlock: (() -> Unit) = {},
    block: () -> T?
): T? {
    return try {
        block.invoke()
    } catch (e: Throwable) {
        e.printStackTrace()
        errorBlock.invoke(e)
    } finally {
        finallyBlock.invoke()
    }
}


/**
 * 忽略错误,返回true或者false
 */
inline fun ignoreErrorBoolean(
    errorBlock: ((Throwable) -> Boolean) = { false },
    finallyBlock: (() -> Unit) = {},
    block: () -> Unit
): Boolean {
    return try {
        block.invoke()
        true
    } catch (e: Throwable) {
        e.printStackTrace()
        errorBlock.invoke(e)
    } finally {
        finallyBlock.invoke()
    }
}

suspend fun <T> retrySuspend(
    maxRetries: Int = 3,
    delayMs: Long = 1000,
    isSuc: (T?) -> Boolean,
    tag: String? = null,
    block: suspend () -> T
): Result<T?> {
    require(maxRetries > 0) { "最大重试次数必须大于0" }

    var lastException: Throwable? = null

    for (attempt in 1..maxRetries) {
        try {
            // 此处挂起
            val result = block()
            if (isSuc.invoke(result)) {
                logD("第 $attempt 次尝试成功", tag ?: "")
                return Result.success(result)
            } else {
                continue
            }
            val exception = Exception("error result = ${result?.toJsonString()}")
            logD("第 $attempt 次尝试失败: ${exception.message}", tag ?: "")
            return Result.failure(exception)
        } catch (e: Exception) {
            lastException = e
            logD("第 $attempt 次尝试异常: ${e.message}", tag ?: "")
            if (attempt < maxRetries) {
                Thread.sleep(delayMs)
            }
        }
    }

    return Result.failure(lastException ?: Exception("未知错误"))
}

fun <T> retry(
    maxRetries: Int = 3,
    delayMs: Long = 1000,
    isSuc: (T?) -> Boolean,
    tag: String? = null,
    block: () -> T
): Result<T?> {
    require(maxRetries > 0) { "最大重试次数必须大于0" }

    var lastException: Throwable? = null

    for (attempt in 1..maxRetries) {
        try {
            val result = block()
            if (isSuc.invoke(result)) {
                logD("第 $attempt 次尝试成功", tag ?: "")
                return Result.success(result)
            } else {
                continue
            }
            val exception = Exception("error result = ${result?.toJsonString()}")
            logD("第 $attempt 次尝试失败: ${exception.message}", tag ?: "")
            return Result.failure(exception)
        } catch (e: Exception) {
            lastException = e
            logD("第 $attempt 次尝试异常: ${e.message}", tag ?: "")
            if (attempt < maxRetries) {
                Thread.sleep(delayMs)
            }
        }
    }

    return Result.failure(lastException ?: Exception("未知错误"))
}