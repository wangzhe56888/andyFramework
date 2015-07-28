package com.andy.myself.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * 项目名称 ： 
 * 类名称 ： InstallActivity
 * 类描述 ： 
 * 创建时间 ： 2014-9-22下午05:53:32
 * email : win58@qq.com
 * nickname : Andy
 * @author wangys
 */
public class InstallActivity extends BaseHeaderActivity {

	private TextView apkStates;
	String filenameTemp = "/station/station.txt";
	String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_apk_install);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		apkStates = (TextView) findViewById(R.id.apk_states);
		
		path = this.getApplicationContext().getFilesDir().getAbsolutePath();
        filenameTemp = path + filenameTemp;
        
        Log.e(LOG_TAG, "filenameTemp = " + filenameTemp);
        
		apkStates.setText(readFileByLines(filenameTemp));
	}
	
	 public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
        	Log.e("APK", "以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                Log.e("APK", "line " + line + ": " + tempString);
                sb.append(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString();
    }
}
