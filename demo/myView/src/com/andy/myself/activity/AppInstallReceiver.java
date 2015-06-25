package com.andy.myself.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.zip.ZipException;

import com.andy.myself.util.ZipUtils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 项目名称 ： 
 * 类名称 ： AppInstallReceiver
 * 类描述 ： 
 * 创建时间 ： 2014-9-22下午05:43:43
 * email : win58@qq.com
 * nickname : Andy
 * @author wangys
 */
public class AppInstallReceiver extends BroadcastReceiver {
	private final String LOG_TAG = "APK";
	String filenameTemp = "/stationId.txt";
	String path;
	@Override
    public void onReceive(Context context, Intent intent) {
////        PackageManager manager = context.getPackageManager();
////        
////        Bundle bundle = intent.getExtras();
////        Set<String> keySet = bundle.keySet();
////        
////        for (String s : keySet) {
////        	Log.e(LOG_TAG, s + " = " + bundle.get(s));
////        }
//		
//        path = context.getApplicationContext().getFilesDir().getAbsolutePath();
//        filenameTemp = path + filenameTemp;
//        
//		Log.e(LOG_TAG, "文件目录：" + filenameTemp);
//		Log.e(LOG_TAG, "安装目录：" + context.getApplicationContext().getPackageResourcePath());
//		
//		
//		String apkStr = context.getApplicationContext().getPackageResourcePath();
//		
//		try {
//			ZipUtils.upZipFile(new File(apkStr), path);
//		} catch (ZipException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//		String packageName = "";
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
//            packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
//            
//            Log.e(LOG_TAG, "安装成功" + packageName);
//            
//        }
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
//            packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "卸载成功了   嘻嘻嘻" + packageName, Toast.LENGTH_LONG).show();
//            Log.e(LOG_TAG, "卸载成功" + packageName);
//        }
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
//            packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();
//            Log.e(LOG_TAG, "替换成功" + packageName);
//        }
//        
//        createText();
//        print(packageName);
    }
	
	//创建文件夹及文件  
	public void createText() {  
//		File file = new File(filenameTemp);  
//		if (!file.exists()) {  
//			try {  
//				file.mkdirs();  
//			} catch (Exception e) {  
//			}  
//		}  
		File dir = new File(filenameTemp);  
		try { 
			if (!dir.exists()) {  
				dir.createNewFile();
			} else {
				dir.delete();
				dir.createNewFile();
			}
		} catch (Exception e) {  
		}  
		
	}  
	public void print(String str) {  
		FileWriter fw = null;  
		BufferedWriter bw = null;  
		try {  
			fw = new FileWriter(filenameTemp, true);
			// 创建FileWriter对象，用来写入字符流  
			bw = new BufferedWriter(fw); // 将缓冲对文件的输出  
			String myreadline = str;  
			bw.write(myreadline); // 写入文件  
			bw.newLine();  
			bw.flush(); // 刷新该流的缓冲  
			bw.close();  
			fw.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
			try {  
				bw.close();  
				fw.close();  
			} catch (IOException e1) {  
			}  
		}  
	}
}
