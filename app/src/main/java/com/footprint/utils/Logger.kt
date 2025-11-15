package com.footprint.utils

import android.util.Log

/**
 * 日志工具类
 */
object Logger {
    private const val TAG = "Footprint"

    fun d(message: String, tag: String = TAG) {
        Log.d(tag, message)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    fun w(message: String, tag: String = TAG) {
        Log.w(tag, message)
    }

    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }

    fun v(message: String, tag: String = TAG) {
        Log.v(tag, message)
    }
}

/**
 * 错误处理扩展函数
 */
inline fun <T> runCatchingWithLog(
    tag: String = "Footprint",
    block: () -> T
): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Logger.e("Operation failed", e, tag)
        Result.failure(e)
    }
}

/**
 * 在协程中安全执行操作
 */
suspend inline fun <T> safeCall(
    tag: String = "Footprint",
    crossinline block: suspend () -> T
): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Logger.e("Async operation failed", e, tag)
        Result.failure(e)
    }
}
