package com.andy.framework.util.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

/**
 * @description: 网络状态工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-22  下午4:10:48
 */
public class AndyNetworkUtil {

	/**
	 * 当前网络状态
	 * WIFI ： WiFi连接
	 * CMNET ：net连接
	 * CMWAP ：WAP
	 * NONENET ：无网络连接
	 * */
	public static enum NetType {
		WIFI, CMNET, CMWAP, NONENET
	}
	
	/**
	 * 判断网络是否可用
	 * */
	public static boolean isNetworkAvailable(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context can't be null !");
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] infos = cm.getAllNetworkInfo();
		if (infos != null) {
			for (NetworkInfo info : infos) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否有网络连接
	 * */
	public static boolean isNetworkConnected(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context can't be null !");
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}
	
	/**
	 * 判断WIFI网络是否可用
	 * */
	public static boolean isWifiConnected(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context can't be null !");
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}
	
	/**
	 * 判断mobile网络是否可用
	 * */
	public static boolean isMobileConnected(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context can't be null !");
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
	
	/**
	 * 获取当前网络连接的类型
	 * @return  
	 * ConnectivityManager.TYPE_MOBILE
	 * ConnectivityManager.TYPE_WIFI
	 * ConnectivityManager.TYPE_WIMAX
	 * ConnectivityManager.TYPE_ETHERNET
	 * ConnectivityManager.TYPE_BLUETOOTH
	 * -1 : other types defined by ConnectivityManager
	 * */
	public static int getConnectedType(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context can't be null !");
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null && networkInfo.isAvailable()) {
			return networkInfo.getType();
		}
		return -1;
	}
	
	/**
	 * 获取APN类型
	 * @return netType
	 * netType.WIFI
	 * netType.CMNET
	 * netType.CMWAP
	 * netType.NONENET
	 * */
	@SuppressLint("DefaultLocale")
	public static NetType getAPNType(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context can't be null !");
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null) {
			return NetType.NONENET;
		}
		
		int type = networkInfo.getType();
		if (type == ConnectivityManager.TYPE_MOBILE) {
			if ("cmnet".equals(networkInfo.getExtraInfo().toLowerCase())) {
				return NetType.CMNET;
			} else {
				return NetType.CMWAP;
			}
		} else if (type == ConnectivityManager.TYPE_WIFI) {
			return NetType.WIFI;
		}
		return NetType.NONENET;
	}
	
	/**
	 * 打开gprs网络设置
	 */
	public static void gprsNetworkSetting(Activity mActivity) {
		Intent intent = null;
		if (Build.VERSION.SDK_INT > 10) {
			intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		} else {
			intent = new Intent(Intent.ACTION_VIEW);
			ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
			intent.setComponent(component);
		}
		mActivity.startActivity(intent);
	}

	/**
	 * 打开wifi网络设置
	 */
	public static void wifiNetworkSetting(Activity mActivity) {
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		mActivity.startActivity(intent);
	}
}
