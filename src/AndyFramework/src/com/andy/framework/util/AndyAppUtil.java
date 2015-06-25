package com.andy.framework.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @description: App相关工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-25  上午9:17:01
 */
public class AndyAppUtil {
	/**
	 * 获取客户端版本号
	 * @param ctx
	 * @return
	 */
	public static String getVersion(Context ctx) {
		PackageManager packageManager = ctx.getPackageManager();
		PackageInfo packInfo = null;
		String versionName = "-1";
		try {
			packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
			if (packInfo != null)
				versionName = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 用来判断服务是否运行.
	 * @param context
	 * @param className 判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 显示通知
	 * @param context
	 * @param id
	 * @param icon
	 * @param ticker
	 * @param title
	 * @param content
	 * @param intent
	 */
	public static void showNotification(Context context, int id, int icon, String ticker, String title, String content,
			Intent intent) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, ticker, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, title, content, pendingIntent);
		notificationManager.notify(id, notification);
	}
}
