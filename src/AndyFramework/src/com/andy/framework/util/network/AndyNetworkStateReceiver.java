package com.andy.framework.util.network;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.andy.framework.util.network.AndyNetworkUtil.NetType;

/**
 * @description: 网络状态监听
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-22  下午4:08:33
 * 
 * 使用配置
 * <receiver
 * 	android:name="com.andy.framework.network.AndyNetworkStateReceiver" >
 *  	<intent-filter> 
 *  		<action android:name="android.net.conn.CONNECTIVITY_CHANGE" /> 
 *  		<action android:name="android.gzcpc.conn.CONNECTIVITY_CHANGE" />
 *  	</intent-filter> 
 *  </receiver>
 * 
 * 	需要开启权限
 *  <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 *  <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 *  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 */
public class AndyNetworkStateReceiver extends BroadcastReceiver {

	private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public final static String ANDY_ANDROID_NET_CHANGE_ACTION = "andy.android.net.conn.CONNECTIVITY_CHANGE";
	// 网络可用状态
	private static boolean networkAvailable = false;
	// 网络连接类型
	private static NetType netType;
	private static BroadcastReceiver receiver;
	// 监听者列表
	private static ArrayList<AndyNetworkStateChangeListener> listenerList = new ArrayList<AndyNetworkStateChangeListener>();
	
	private static BroadcastReceiver getReceiver() {
		if (receiver == null) {
			receiver = new AndyNetworkStateReceiver();
		}
		return receiver;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		receiver = this;
		if (ANDROID_NET_CHANGE_ACTION.equalsIgnoreCase(intent.getAction()) ||
				ANDY_ANDROID_NET_CHANGE_ACTION.equalsIgnoreCase(intent.getAction())) {
			networkAvailable = AndyNetworkUtil.isNetworkAvailable(context);
			if (networkAvailable) {
				netType = AndyNetworkUtil.getAPNType(context);
			}
			notifyListener();
		}
	}

	/**
	 * 网络状态改变时通知回调
	 * */
	private void notifyListener() {
		for (AndyNetworkStateChangeListener listener : listenerList) {
			if (listener != null) {
				if (networkAvailable) {
					listener.onConnect(netType);
				} else {
					listener.onDisConnect();
				}
			}
		}
	}
	
	/**
	 * 注册网络监听广播
	 * */
	public static void registerNetworkStateReceiver(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ANDROID_NET_CHANGE_ACTION);
		filter.addAction(ANDY_ANDROID_NET_CHANGE_ACTION);
		context.getApplicationContext().registerReceiver(getReceiver(), filter);
	}
	
	/**
	 * 发送检查网络状态广播
	 * */
	public static void sendCheckNetworkState(Context context) {
		Intent intent = new Intent();
		intent.setAction(ANDY_ANDROID_NET_CHANGE_ACTION);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 注销网络状态广播
	 * */
	public static void unRegisterNetworkStateReceiver(Context context) {
		if (receiver != null) {
			context.getApplicationContext().unregisterReceiver(receiver);
		}
	}
	
	/**
	 * 注册网络状态监听者
	 * */
	public static void registerLisener(AndyNetworkStateChangeListener listener) {
		if (listener != null) {
			listenerList.add(listener);
		}
	}
	
	/**
	 * 注销网络状态监听者
	 * */
	public static void unRegisterLisener(AndyNetworkStateChangeListener listener) {
		if (listener != null) {
			listenerList.remove(listener);
		}
	}
}
