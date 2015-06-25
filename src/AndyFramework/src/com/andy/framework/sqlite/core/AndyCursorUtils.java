package com.andy.framework.sqlite.core;

import java.util.HashMap;
import java.util.Map.Entry;
import com.andy.framework.sqlite.AndyDB;
import com.andy.framework.sqlite.table.AndyManyToOne;
import com.andy.framework.sqlite.table.AndyOneToMany;
import com.andy.framework.sqlite.table.AndyProperty;
import com.andy.framework.sqlite.table.AndyTableInfo;

import android.database.Cursor;

/**
 * @description: 游标工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-15  上午11:57:57
 */
@SuppressWarnings("unchecked")
public class AndyCursorUtils {
	public static <T> T getEntity(Cursor cursor, Class<T> clazz, AndyDB db) {
		try {
			if(cursor != null) {
				AndyTableInfo table = AndyTableInfo.get(clazz);
				int columnCount = cursor.getColumnCount();
				if (columnCount > 0) {
					T  entity = (T) clazz.newInstance();
					for (int i = 0; i < columnCount; i++) {
						
						String column = cursor.getColumnName(i);
						
						AndyProperty property = table.propertyMap.get(column);
						if (property != null) {
							property.setValue(entity, cursor.getString(i));
						} else {
							if (table.getId().getColumn().equals(column)) {
								table.getId().setValue(entity, cursor.getString(i));
							}
						}

					}
                    /**
                     * 处理OneToMany的lazyLoad形式
                     */
                    for (AndyOneToMany oneToManyProp : table.oneToManyMap.values()) {
                        if (oneToManyProp.getDataType() == AndyOneToManyLazyLoader.class){
                        	AndyOneToManyLazyLoader oneToManyLazyLoader = new AndyOneToManyLazyLoader(entity,clazz,oneToManyProp.getOneClass(),db);
                            oneToManyProp.setValue(entity,oneToManyLazyLoader);
                        }
                    }

                    /**
                     * 处理ManyToOne的lazyLoad形式
                     */
                    for (AndyManyToOne manyToOneProp : table.manyToOneMap.values()) {
                        if (manyToOneProp.getDataType() == AndyManyToOneLazyLoader.class){
                        	AndyManyToOneLazyLoader manyToOneLazyLoader = new AndyManyToOneLazyLoader(entity,clazz,manyToOneProp.getManyClass(),db);
                            manyToOneLazyLoader.setFieldValue(cursor.getInt(cursor.getColumnIndex(manyToOneProp.getColumn())));
                            manyToOneProp.setValue(entity,manyToOneLazyLoader);
                        }
                    }
					return entity;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static AndyDBModel getDbModel(Cursor cursor) {
		if (cursor != null && cursor.getColumnCount() > 0) {
			AndyDBModel model = new AndyDBModel();
			int columnCount = cursor.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				model.set(cursor.getColumnName(i), cursor.getString(i));
			}
			return model;
		}
		return null;
	}
	
	
	public static <T> T dbModel2Entity(AndyDBModel dbModel, Class<?> clazz) {
		if (dbModel != null) {
			HashMap<String, Object> dataMap = dbModel.getDataMap();
			try {
				@SuppressWarnings("unchecked")
				T  entity = (T) clazz.newInstance();
				for (Entry<String, Object> entry : dataMap.entrySet()) {
					String column = entry.getKey();
					AndyTableInfo table = AndyTableInfo.get(clazz);
					AndyProperty property = table.propertyMap.get(column);
					if (property != null) {
						property.setValue(entity, entry.getValue()==null?null:entry.getValue().toString());
					} else {
						if (table.getId().getColumn().equals(column)) {
							table.getId().setValue(entity, entry.getValue()==null?null:entry.getValue().toString());
						}
					}
					
				}
				return entity;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
