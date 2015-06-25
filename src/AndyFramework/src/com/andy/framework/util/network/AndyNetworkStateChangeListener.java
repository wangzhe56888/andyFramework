package com.andy.framework.util.network;

import com.andy.framework.util.network.AndyNetworkUtil.NetType;


/**
 * @description: 网络状态改变监听
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-22  下午5:11:52
 */
public class AndyNetworkStateChangeListener {
	/**
	 * 网络连接时调用
	 */
	public void onConnect(NetType type) {}

	/**
	 * 网络连接断开时调用
	 */
	public void onDisConnect() {}
}
