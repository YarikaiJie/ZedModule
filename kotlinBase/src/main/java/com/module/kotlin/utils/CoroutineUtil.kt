package com.module.kotlin.utils


import android.os.Looper
import kotlinx.coroutines.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 协程工具类
 */
//yield()函数，让出执行权
/**
 * 创建一个协程作用域
 * context去下面的四个值中的一个
 * [Dispatchers.IO] 工作线程池，依赖于Dispatchers.Default，支持最大并行任务数。
 * [Dispatchers.Unconfined] 无指定派发线程，会根据运行时的上线文环境决定。
 * [Dispatchers.Main] 主线程，这个在不同平台定义不一样，所以需要引入相关的依赖，比如Android平台，需要使用包含MainLooper的handler来向主线程派发。
 * [Dispatchers.Default] 默认线程池，核心线程和最大线程数依赖cpu数量。
 * [Dispatchers.IO]：工作线程池，依赖于Dispatchers.Default，支持最大并行任务数。
 *
 * 感觉和[GlobalScope]创建方式一样
 */

/**
 * 是否是主线程
 */
val isMainThread: Boolean
    get() = Looper.getMainLooper() === Looper.myLooper()

/**
 * 监听协程取消
 * 也可使用waitAny 来监听取消
 */
class MvvmCoroutineExceptionContext(private val handleException: (context: CoroutineContext, exception: Throwable) -> Unit) :
    AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        handleException.invoke(context, exception)
    }
}
// 可以接收协程内部异常，但是无法监听取消异常
//createCoroutine(MvvmCoroutineException { context, exception ->
//            println()
//        })

fun createCoroutine(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope {
    //创建一个协程作用域，一旦被取消，此对象便不可再使用
    return CoroutineScope(context)
}

/**
 * 创建主线程协程
 */
fun createMainCoroutine(): CoroutineScope {
    return MainScope()
}

/**
 * 切换到主线程
 * 不创建新的协程
 *  withContext开销更低 类似于 async{..}.await()
 */
suspend inline fun <T> withMainThread(crossinline block: suspend () -> T): T {
    return if (!isMainThread) {
        withContext(Dispatchers.Main.immediate) {
            block.invoke()
        }
    } else {
        block.invoke()
    }
}

/**
 * 始终在io线程运行代码
 */
suspend inline fun <T> withIoThread(crossinline block: suspend () -> T): T {
    return if (isMainThread) {
        withContext(Dispatchers.IO) {
            block.invoke()
        }
    } else {
        block.invoke()
    }
}

/**
 * 启动新的协程，代码运行在主线程
 * 没有自定义返回值
 * launch和async区别，一个有返回值，一个没有
 * start可以指定执行时机
 * async是可控的（await()阻塞当前协程，不往下执行。
 *                在定义的时候async已经开始运行了，
 *                await()只是等待执行完毕再向下执行，否则会继续走下面的逻辑）
 */
fun CoroutineScope.launchOnUi(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(Dispatchers.Main.immediate, start = start, block = block)
}

/**
 * 启动新的协程，代码运行在主线程
 * 有自定义返回值
 *  start可以指定执行时机
 *  launch和async区别，一个有返回值，一个没有
 * async是可控的（await()阻塞当前协程，不往下执行。
 *                在定义的时候async已经开始运行了，
 *                await()只是等待执行完毕再向下执行，否则会继续走下面的逻辑）
 */
fun <T> CoroutineScope.asyncOnUi(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return async(Dispatchers.Main.immediate, start = start, block = block)
}

/**
 * 启动新的协程，代码运行在子线程
 * 没有自定义返回值
 *  start可以指定执行时机
 *  launch和async区别，一个有返回值，一个没有
 * async是可控的（await()阻塞当前协程，不往下执行。
 *                在定义的时候async已经开始运行了，
 *                await()只是等待执行完毕再向下执行，否则会继续走下面的逻辑）
 */
fun CoroutineScope.launchOnThread(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(Dispatchers.Default, start = start, block = block)
}

/**
 * 启动新的协程，代码运行在子线程
 * 有自定义返回值
 *  start可以指定执行时机
 *  launch和async区别，一个有返回值，一个没有
 * async是可控的（await()阻塞当前协程，不往下执行。
 *                在定义的时候async已经开始运行了，
 *                await()只是等待执行完毕再向下执行，否则会继续走下面的逻辑）
 */
fun <T> CoroutineScope.asyncOnThread(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return async(Dispatchers.Default, start = start, block = block)
}

/**
 * 将异步代码写成同步调用示例
 */
suspend inline fun <T> awaitAny(crossinline block: (CancellableContinuation<T>) -> Unit): T {
    return suspendCancellableCoroutine(block)
}

/**
 * 在io线程操作
 */
suspend inline fun <T> awaitOnIoThread(crossinline block: (CancellableContinuation<T>) -> Unit): T {
    return withIoThread {
        awaitAny(block)
    }
}

/**
 * 启动协程，忽略代码运行错误
 */
inline fun CoroutineScope.ignoreErrorLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.() -> Unit
) = launch(context, start) {
    ignoreError { block.invoke(this) }
}

/**
 * 启动协程，忽略代码运行错误
 */
inline fun <T> CoroutineScope.ignoreErrorAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.() -> T
) = async(context, start) {
    ignoreError { block.invoke(this) }
}


/**
 * 多个结果回调例子
 */
fun <T, T1> CoroutineScope.multipleAsync(
    run: suspend CoroutineScope.() -> T,
    run1: suspend CoroutineScope.() -> T1,
    result: Function2<T, T1, Unit>
) {
    launch {
        val t = async { run.invoke(this) }
        val t1 = async { run1.invoke(this) }
        val r = t.await()
        val r1 = t1.await()
        result.invoke(r, r1)
    }
}


