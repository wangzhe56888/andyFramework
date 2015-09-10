package com.andy.myself;

import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import android.app.Activity;
import android.app.Application;

/**
 * 项目名称 ： 
 * 类名称 ： AndyApplication
 * 类描述 ： 
 * 创建时间 ： 2014-8-25下午05:13:16
 * email : win58@qq.com
 * nickname : Andy
 * @author wangys
 */
public class AndyApplication extends Application {

	private List<Activity> activities = new LinkedList<Activity>();
//	private static AndyApplication instance = null;
//
//	public static AndyApplication getInstance() {
//		if (instance == null) {
//			instance = new AndyApplication();
//		}
//		return instance;
//	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
	}
	
	public void addActivity(Activity activity) {
		if (activities != null && !activities.contains(activity)) {
			activities.add(activity);
		}
	}
	
	public void exitApp() {
		if (activities != null) {
			for (Activity activity : activities) {
				activity.finish();
			}
		}
		System.exit(0);
	}
}
