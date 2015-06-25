package com.andy.myself.util;

import android.util.Log;

/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  下午4:31:37
 */
public class AndyLog {

	private static final String ANDY_TAG_LOG = "ANDY";
	
	public static void info(String msg) {
		Log.i(ANDY_TAG_LOG, msg);
	}
}
