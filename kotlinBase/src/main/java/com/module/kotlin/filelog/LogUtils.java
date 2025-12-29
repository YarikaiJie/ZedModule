package com.module.kotlin.filelog;

import android.util.Log;

// 日志打印工具类，为解决打印不全问题
public class LogUtils {
    private static final int MAX_LOG_LENGTH = 4000; // Logcat单条日志最大长度

    // 错误日志完整打印
    public static void eFull(String tag, String message) {
        logFull("e", tag, message);
    }

    // 带异常信息的完整打印
    public static void eFull(String tag, String message, Throwable throwable) {
        String fullMessage = message + "\n" + getFullStackTrace(throwable);
        eFull(tag, fullMessage);
    }

    // 错误日志完整打印
    public static void iFull(String tag, String message) {
        logFull("i", tag, message);
    }

    // 带异常信息的完整打印
    public static void iFull(String tag, String message, Throwable throwable) {
        String fullMessage = message + "\n" + getFullStackTrace(throwable);
        iFull(tag, fullMessage);
    }


    // 错误日志完整打印
    public static void dFull(String tag, String message) {
        logFull("d", tag, message);
    }

    // 带异常信息的完整打印
    public static void dFull(String tag, String message, Throwable throwable) {
        String fullMessage = message + "\n" + getFullStackTrace(throwable);
        dFull(tag, fullMessage);
    }

    // 错误日志完整打印
    public static void wFull(String tag, String message) {
        logFull("w", tag, message);
    }

    // 带异常信息的完整打印
    public static void wFull(String tag, String message, Throwable throwable) {
        String fullMessage = message + "\n" + getFullStackTrace(throwable);
        wFull(tag, fullMessage);
    }


    public static void logFull(String type, String tag, String message) {
        if (message.length() <= MAX_LOG_LENGTH) {
            logInfo(type, tag, message);
            return;
        }

        // 分割长消息
        int start = 0;
        while (start < message.length()) {
            int end = Math.min(start + MAX_LOG_LENGTH, message.length());
            String chunk = message.substring(start, end);
            logInfo(type, tag, chunk);
            start = end;
        }
    }

    public static void logInfo(String type, String tag, String message) {
        switch (type) {
            case "e":
                Log.e(tag, message);
                break;
            case "i":
                Log.i(tag, message);
                break;
            case "d":
                Log.d(tag, message);
                break;
            case "w":
                Log.w(tag, message);
                break;
            case "v":
                Log.v(tag, message);
                break;
        }
    }

    // 获取完整堆栈信息
    public static String getFullStackTrace(Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }
}