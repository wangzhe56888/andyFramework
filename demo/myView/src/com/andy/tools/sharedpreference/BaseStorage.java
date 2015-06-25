package com.andy.tools.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 项目名称 ： 
 * 类名称 ： BaseStorage
 * 类描述 ： 
 * 创建时间 ： 2014-8-28上午11:24:23
 * email : win58@qq.com
 * nickname : Andy
 * @author wangys
 */
public abstract class BaseStorage implements IStorage{

	private Context mContext;
	private SharedPreferences sp;
	
	public BaseStorage (Context context) {
		mContext = context;
		if (getIdentifer() != null) {
			sp = mContext.getSharedPreferences(getIdentifer(), Context.MODE_PRIVATE);
		}
	}
	@Override
	public abstract void serialize(SharedPreferences sp);

	@Override
	public abstract void unSerialize(SharedPreferences sp);

	@Override
	public abstract void delete(SharedPreferences sp);

	@Override
	public abstract String getIdentifer();

	/**
	 * 加载数据
	 **/
	public void loadData() {
		if (sp != null) {
			this.unSerialize(sp);
		}
	}
	
	/**
	 * 更新数据
	 * */
	
	public void updateData() {
		if (sp != null) {
			this.serialize(sp);
		}
	}
	
	/**
	 * 删除数据
	 * */
	public void deleteData() {
		if (sp != null) {
			
		}
	}
	
}
