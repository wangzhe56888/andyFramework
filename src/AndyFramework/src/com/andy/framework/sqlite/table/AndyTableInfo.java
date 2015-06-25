package com.andy.framework.sqlite.table;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import com.andy.framework.exception.AndySqliteException;
import com.andy.framework.sqlite.core.AndyClassUtils;
import com.andy.framework.sqlite.core.AndyFieldUtils;

/**
 * @description: 表实现类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:42:35
 */
public class AndyTableInfo {

	private String className; // 类名
	private String tableName; // 表名
	private AndyId id; // 主键ID
	
	public final HashMap<String, AndyProperty> propertyMap = new HashMap<String, AndyProperty>();
	public final HashMap<String, AndyOneToMany> oneToManyMap = new HashMap<String, AndyOneToMany>();
	public final HashMap<String, AndyManyToOne> manyToOneMap = new HashMap<String, AndyManyToOne>();
	//在对实体进行数据库操作的时候查询是否已经有表了，只需查询一遍，用此标示
	private boolean checkDatabese;
	
	private static final HashMap<String, AndyTableInfo> tableInfoMap = new HashMap<String, AndyTableInfo>();

	private AndyTableInfo(){}

	/**
	 * 根据Class获取相应的table info 
	 * */
	public static AndyTableInfo get(Class<?> clazz) {
		if (clazz == null) {
			throw new AndySqliteException("table info get error because clazz is null !");
		}
		
		// 从tableinfoMap中获取tableinfo
		AndyTableInfo tableInfo = tableInfoMap.get(clazz.getName());
		
		if (tableInfo == null) {
			tableInfo = new AndyTableInfo();
			
			tableInfo.setTableName(AndyClassUtils.getTableName(clazz));
			tableInfo.setClassName(clazz.getName());
			
			Field idField = AndyClassUtils.getPrimaryKeyField(clazz);
			
			if (idField != null) {
				AndyId id = new AndyId();
				id.setColumn(AndyFieldUtils.getColumnByField(idField));
				id.setFieldName(idField.getName());
				id.setDataType(idField.getType());
				id.setSet(AndyFieldUtils.getFieldSetMethod(clazz, idField));
				id.setGet(AndyFieldUtils.getFieldGetMethod(clazz, idField));
				
				tableInfo.setId(id);
			} else {
				// 主键为空 抛出异常
				throw new AndySqliteException("the class[" + clazz + "]'s idField is null , \n you can define _id,id property or use annotation @AndyId to solution this exception");
			}
			
			// 属性列表
			List<AndyProperty> properties = AndyClassUtils.getPropertyList(clazz);
			for (AndyProperty property : properties) {
				tableInfo.propertyMap.put(property.getColumn(), property);
			}
			
			// 多对一
			List<AndyManyToOne> mList = AndyClassUtils.getManyToOneList(clazz);
			for (AndyManyToOne andyManyToOne : mList) {
				tableInfo.manyToOneMap.put(andyManyToOne.getColumn(), andyManyToOne);
			}
			
			// 一对多列表
			List<AndyOneToMany> oList = AndyClassUtils.getOneToManyList(clazz);
			for (AndyOneToMany otm : oList) {
				tableInfo.oneToManyMap.put(otm.getColumn(), otm);
			}
			
			tableInfoMap.put(clazz.getName(), tableInfo);
		}
		
		return tableInfo;
	}
	
	public static AndyTableInfo get(String classnameString) {
		try {
			return get(Class.forName(classnameString));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public AndyId getId() {
		return id;
	}

	public void setId(AndyId id) {
		this.id = id;
	}

	public boolean isCheckDatabese() {
		return checkDatabese;
	}

	public void setCheckDatabese(boolean checkDatabese) {
		this.checkDatabese = checkDatabese;
	}
}
