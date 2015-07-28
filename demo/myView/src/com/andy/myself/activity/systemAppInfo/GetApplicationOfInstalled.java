package com.andy.myself.activity.systemAppInfo;

import java.util.ArrayList;
import java.util.List;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GetApplicationOfInstalled extends BaseHeaderActivity implements OnItemClickListener {
    
    private ListView mListView;
    private InstalledPackageAdapter maAdapter;
    private List<PackageInfo> mApps;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_installed);
        initView(savedInstanceState);
    }
    
    @Override
    protected void initView(Bundle savedInstanceState) {
    	super.initView(savedInstanceState);
    	
    	mListView = (ListView) findViewById(R.id.mylist);
        mListView.setOnItemClickListener(this);
        mApps = loadPackageInfo(this);
        maAdapter = new InstalledPackageAdapter(this, mApps);
        mListView.setAdapter(maAdapter);
    }
    
    private List<PackageInfo> loadPackageInfo(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageList = pm.getInstalledPackages(0);
        for(int i=0; i<packageList.size(); i++) {
            PackageInfo info = packageList.get(i);
            if((info.applicationInfo.flags & info.applicationInfo.FLAG_SYSTEM) <= 0) {
                apps.add(info);
            }
        }
        return apps;
    }

    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
        PackageInfo packageInfo = mApps.get(position);
        doStartApplicationWithPackageName(packageInfo.packageName);
    }
    
	private void doStartApplicationWithPackageName(String packagename) {

		PackageInfo packageinfo = null;
		try {
			packageinfo = getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			startActivity(intent);
		}
	}
}
