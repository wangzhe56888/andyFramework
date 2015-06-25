package com.andy.framework.util;

import android.util.Log;

/**
 * @description: 日志工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  上午9:18:00
 */
public class AndyLog {

	private static final String ANDY_TAG_LOG = "ANDY";
	private static final String ANDY_LOG_PREFIX_BITMAP = "andy_framework_bitmap : ";
	private static final String ANDY_LOG_PREFIX_HTTP = "andy_framework_http : ";
	private static boolean isDebug = true;
	
	public static void info(String msg) {
		info(ANDY_TAG_LOG, msg);
	}
	
	public static void warn(String msg) {
		warn(ANDY_TAG_LOG, msg);
	}
	
	public static void error(String msg) {
		error(ANDY_TAG_LOG, msg);
	}
	
	public static void info(String tag, String msg) {
		Log.i(tag, msg);
	}
	
	public static void warn(String tag, String msg) {
		Log.w(tag, msg);
	}
	
	public static void error(String tag, String msg) {
		Log.e(tag, msg);
	}
	
	public static void info(String tag, String msg, Throwable t) {
		Log.i(tag, msg, t);
	}
	
	public static void warn(String tag, String msg, Throwable t) {
		Log.w(tag, msg, t);
	}
	
	public static void error(String tag, String msg, Throwable t) {
		Log.e(tag, msg, t);
	}
	
	/**
	 * 图片缓存相关日志
	 * */
	public static void info_bitmap(String msg) {
		if (isDebug) {
			info(ANDY_LOG_PREFIX_BITMAP + msg);
		}
	}
	
	public static void warn_bitmap(String msg) {
		if (isDebug) {
			warn(ANDY_LOG_PREFIX_BITMAP + msg);
		}
	}
	public static void warn_bitmap(String msg, Throwable t) {
		if (isDebug) {
			warn(ANDY_TAG_LOG, ANDY_LOG_PREFIX_BITMAP + msg, t);
		}
	}
	
	public static void error_bitmap(String msg) {
		if (isDebug) {
			error(ANDY_LOG_PREFIX_BITMAP + msg);
		}
	}
	
	public static void error_bitmap(String msg, Throwable t) {
		if (isDebug) {
			error(ANDY_TAG_LOG, ANDY_LOG_PREFIX_BITMAP + msg, t);
		}
	}
	
	/**
	 * 网络框架相关日志
	 * */
	public static void info_http(String msg) {
		if (isDebug) {
			info(ANDY_LOG_PREFIX_HTTP + msg);
		}
	}
	
	public static void warn_http(String msg) {
		if (isDebug) {
			warn(ANDY_LOG_PREFIX_HTTP + msg);
		}
	}
	
	public static void error_http(String msg) {
		if (isDebug) {
			error(ANDY_LOG_PREFIX_HTTP + msg);
		}
	}
	
}
