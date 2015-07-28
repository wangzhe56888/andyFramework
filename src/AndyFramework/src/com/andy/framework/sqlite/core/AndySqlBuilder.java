package com.andy.framework.sqlite.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.text.TextUtils;
import com.andy.framework.exception.AndySqliteException;
import com.andy.framework.sqlite.table.AndyId;
import com.andy.framework.sqlite.table.AndyKeyValue;
import com.andy.framework.sqlite.table.AndyManyToOne;
import com.andy.framework.sqlite.table.AndyProperty;
import com.andy.framework.sqlite.table.AndyTableInfo;

/**
 * @description: SQL创建工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-15  上午10:45:44
 */
public class AndySqlBuilder {

/**************************** sql of insert start  **************************************************/
	/**
	 * 获取插入语句 
	 * INSERT INTO table_name (column, column, ...) VALUES (?, ?, ...)
	 * @param entity 实体
	 * */
	public static AndySqlInfo buildInsertSql(Object entity) {
		if (entity == null) {
			return null;
		}
		
		List<AndyKeyValue> keyValueList = getSaveKeyValueListByEntity(entity);
		
		if (keyValueList != null && keyValueList.size() > 0) {
			StringBuffer strSQL = new StringBuffer();
			AndySqlInfo sqlInfo = new AndySqlInfo();
			
			strSQL.append("INSERT INTO ");
			strSQL.append(AndyTableInfo.get(entity.getClass()).getTableName());
			strSQL.append(" (");
			for (AndyKeyValue kv : keyValueList) {
				strSQL.append(kv.getKey()).append(",");
				sqlInfo.addValue(kv.getValue());
			}
			strSQL.deleteCharAt(strSQL.length() - 1);
			strSQL.append(") VALUES ( ");
			
			int length = keyValueList.size();
			for (int i = 0 ; i < length; i++) {
				strSQL.append("?,");
			}
			strSQL.deleteCharAt(strSQL.length() - 1);
			strSQL.append(")");
			
			sqlInfo.setSql(strSQL.toString());
			
			return sqlInfo;
		}
		return null;
	}

/**************************** sql of delete start  **************************************************/
	/**
	 * 获取删除语句 删除条件为表主键
	 * DELETE FROM table_name WHERE id.column = ?
	 * */
	public static AndySqlInfo buildDeleteSql(Object entity){
		AndyTableInfo table = AndyTableInfo.get(entity.getClass());
		
		AndyId id = table.getId();
		Object idvalue = id.getValue(entity);
		
		if (idvalue == null) {
			throw new AndySqliteException("getDeleteSQL:" + entity.getClass() + " id value is null");
		}
		StringBuffer strSQL = new StringBuffer(getDeleteSqlBytableName(table.getTableName()));
		strSQL.append(" WHERE ").append(id.getColumn()).append("=?");
		
		AndySqlInfo sqlInfo = new AndySqlInfo();
		sqlInfo.setSql(strSQL.toString());
		sqlInfo.addValue(idvalue);
		
		return sqlInfo;
	}
	
	/**
	 * 获取删除表数据Sql
	 * @param clazz 表
	 * @param idValue 删除条件  主键id值
	 * */
	public static AndySqlInfo buildDeleteSql(Class<?> clazz , Object idValue){
		AndyTableInfo table = AndyTableInfo.get(clazz);
		AndyId id = table.getId();
		
		if (null == idValue) {
			throw new AndySqliteException("getDeleteSQL:idValue is null");
		}
		
		StringBuffer strSQL = new StringBuffer(getDeleteSqlBytableName(table.getTableName()));
		strSQL.append(" WHERE ").append(id.getColumn()).append("=?");
		
		AndySqlInfo sqlInfo = new AndySqlInfo();
		sqlInfo.setSql(strSQL.toString());
		sqlInfo.addValue(idValue);
		
		return sqlInfo;
	}
	
	/**
	 * 根据条件删除数据 ，条件为空的时候将会删除所有的数据
	 * @param clazz
	 * @param strWhere 删除条件
	 * @return
	 */
	public static String buildDeleteSql(Class<?> clazz , String strWhere){
		AndyTableInfo table = AndyTableInfo.get(clazz);
		StringBuffer strSQL = new StringBuffer(getDeleteSqlBytableName(table.getTableName()));
		
		if(!TextUtils.isEmpty(strWhere)){
			strSQL.append(" WHERE ");
			strSQL.append(strWhere);
		}
		
		return strSQL.toString();
	}
	
/**************************** sql of select start **************************************************/

	/**
	 * 查询语句
	 * SELECT * FROM table_name WHERE id.column = idValue
	 * @return String
	 * */
	public static String getSelectSQL(Class<?> clazz, Object idValue) {
		AndyTableInfo table = AndyTableInfo.get(clazz);
		
		StringBuffer strSQL = new StringBuffer(getSelectSqlByTableName(table.getTableName()));
		strSQL.append(" WHERE ");
		strSQL.append(getPropertyStrSql(table.getId().getColumn(), idValue));
		
		return strSQL.toString();
	}
	
	/**
	 * 查询语句
	 * SELECT * FROM table_name WHERE id.column = idValue
	 * @return sql info
	 * */
	public static AndySqlInfo getSelectSqlAsSqlInfo(Class<?> clazz, Object idValue) {
		AndyTableInfo table = AndyTableInfo.get(clazz);
		
		StringBuffer strSQL = new StringBuffer(getSelectSqlByTableName(table.getTableName()));
		strSQL.append(" WHERE ").append(table.getId().getColumn()).append("=?");
		
		AndySqlInfo sqlInfo = new AndySqlInfo();
		sqlInfo.setSql(strSQL.toString());
		sqlInfo.addValue(idValue);
		
		return sqlInfo;
	}
	
	
	public static String getSelectSQL(Class<?> clazz){
		return getSelectSqlByTableName(AndyTableInfo.get(clazz).getTableName());
	}
	
	public static String getSelectSQLByWhere(Class<?> clazz, String strWhere){
		AndyTableInfo table = AndyTableInfo.get(clazz);
		
		StringBuffer strSQL = new StringBuffer(getSelectSqlByTableName(table.getTableName()));
		
		if(!TextUtils.isEmpty(strWhere)){
			strSQL.append(" WHERE ").append(strWhere);
		}
		
		return strSQL.toString();
	}
	
	
/**************************** sql of update start  **************************************************/
	
	/**
	 * 更新表
	 * UPDATE table_name SET column = ? , column = ? ... WHERE id.column = ?
	 * @return sql info entity
	 * */
	public static AndySqlInfo getUpdateSqlAsSqlInfo(Object entity) {
		AndyTableInfo table = AndyTableInfo.get(entity.getClass());
		Object idvalue = table.getId().getValue(entity);
		
		//主键值不能为null，否则不能更新
		if (null == idvalue) {
			throw new AndySqliteException("this entity["+entity.getClass()+"]'s id value is null");
		}
		
		List<AndyKeyValue> keyValueList = new ArrayList<AndyKeyValue>();
		//添加属性
		Collection<AndyProperty> propertys = table.propertyMap.values();
		for(AndyProperty property : propertys){
			AndyKeyValue kv = property2KeyValue(property,entity) ;
			if (kv != null)
				keyValueList.add(kv);
		}
		
		//添加外键（多对一）
		Collection<AndyManyToOne> manyToOnes = table.manyToOneMap.values();
		for (AndyManyToOne many : manyToOnes) {
			AndyKeyValue kv = manyToOne2KeyValue(many,entity);
			if (kv!=null) keyValueList.add(kv);
		}
		
		if (keyValueList == null || keyValueList.size() == 0) return null ;
		
		AndySqlInfo sqlInfo = new AndySqlInfo();
		StringBuffer strSQL=new StringBuffer("UPDATE ");
		strSQL.append(table.getTableName());
		strSQL.append(" SET ");
		for (AndyKeyValue kv : keyValueList) {
			strSQL.append(kv.getKey()).append("=?,");
			sqlInfo.addValue(kv.getValue());
		}
		strSQL.deleteCharAt(strSQL.length() - 1);
		strSQL.append(" WHERE ").append(table.getId().getColumn()).append("=?");
		sqlInfo.addValue(idvalue);
		sqlInfo.setSql(strSQL.toString());
		return sqlInfo;
	}
	
	/**
	 * 更新表
	 * UPDATE table_name SET column = ? , column = ? ... WHERE strWhere
	 * @return sql info entity
	 * */
	public static AndySqlInfo getUpdateSqlAsSqlInfo(Object entity, String strWhere) {
		
		AndyTableInfo table = AndyTableInfo.get(entity.getClass());
		
		List<AndyKeyValue> keyValueList = new ArrayList<AndyKeyValue>();
		
		//添加属性
		Collection<AndyProperty> propertys = table.propertyMap.values();
		for(AndyProperty property : propertys){
			AndyKeyValue kv = property2KeyValue(property,entity) ;
			if(kv!=null) keyValueList.add(kv);
		}
		
		//添加外键（多对一）
		Collection<AndyManyToOne> manyToOnes = table.manyToOneMap.values();
		for(AndyManyToOne many : manyToOnes){
			AndyKeyValue kv = manyToOne2KeyValue(many,entity);
			if (kv != null) keyValueList.add(kv);
		}
		
		if (keyValueList == null || keyValueList.size() == 0) {
			throw new AndySqliteException("this entity["+entity.getClass()+"] has no property"); 
		}
		
		AndySqlInfo sqlInfo = new AndySqlInfo();
		StringBuffer strSQL=new StringBuffer("UPDATE ");
		strSQL.append(table.getTableName());
		strSQL.append(" SET ");
		for(AndyKeyValue kv : keyValueList){
			strSQL.append(kv.getKey()).append("=?,");
			sqlInfo.addValue(kv.getValue());
		}
		strSQL.deleteCharAt(strSQL.length() - 1);
		if(!TextUtils.isEmpty(strWhere)){
			strSQL.append(" WHERE ").append(strWhere);
		}
		sqlInfo.setSql(strSQL.toString());	
		return sqlInfo;
	}
/************************** create table start *************************************************/
	/**
	 * 建表语句
	 * CREATE TABLE IF NOT EXISTS table_name (id PRIMARY KEY, column textType, column textType, ...)
	 * */
	public static String getCreatTableSQL(Class<?> clazz){
		AndyTableInfo table = AndyTableInfo.get(clazz);
		
		AndyId id=table.getId();
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("CREATE TABLE IF NOT EXISTS ");
		strSQL.append(table.getTableName());
		strSQL.append(" ( ");
		
		Class<?> primaryClazz = id.getDataType();
		if (primaryClazz == int.class || primaryClazz == Integer.class 
				|| primaryClazz == long.class || primaryClazz == Long.class) {
			strSQL.append(id.getColumn()).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		} else {
			strSQL.append(id.getColumn()).append(" TEXT PRIMARY KEY,");
		}
			
		Collection<AndyProperty> propertys = table.propertyMap.values();
		for (AndyProperty property : propertys) {
			strSQL.append(property.getColumn());
			Class<?> dataType =  property.getDataType();
			if( dataType== int.class || dataType == Integer.class 
			   || dataType == long.class || dataType == Long.class){
				strSQL.append(" INTEGER");
			}else if(dataType == float.class ||dataType == Float.class 
					||dataType == double.class || dataType == Double.class){
				strSQL.append(" REAL");
			}else if (dataType == boolean.class || dataType == Boolean.class) {
				strSQL.append(" NUMERIC");
			}
			strSQL.append(",");
		}
		
		Collection<AndyManyToOne> manyToOnes = table.manyToOneMap.values();
		for (AndyManyToOne manyToOne : manyToOnes){
			strSQL.append(manyToOne.getColumn())
			.append(" INTEGER")
			.append(",");
		}
		strSQL.deleteCharAt(strSQL.length() - 1);
		strSQL.append(" )");
		return strSQL.toString();
	}
	

/********************************* 其他工具方法  ***********************************************************/
	/**
	 * 将实体entity转换成key-value
	 * */
	public static List<AndyKeyValue> getSaveKeyValueListByEntity(Object entity){
		if (entity == null) {
			return null;
		}
		List<AndyKeyValue> keyValueList = new ArrayList<AndyKeyValue>();
		
		AndyTableInfo table = AndyTableInfo.get(entity.getClass());
		Object idvalue = table.getId().getValue(entity);
		
		//用了非自增长,添加id , 采用自增长就不需要添加id了
		if (!(idvalue instanceof Integer)) {
			if (idvalue instanceof String && idvalue != null) {
				AndyKeyValue kv = new AndyKeyValue(table.getId().getColumn(),idvalue);
				keyValueList.add(kv);
			}
		}
		
		//添加属性
		Collection<AndyProperty> propertys = table.propertyMap.values();
		for(AndyProperty property : propertys){
			AndyKeyValue kv = property2KeyValue(property, entity) ;
			if (kv != null)
				keyValueList.add(kv);
		}
		
		//添加外键（多对一）
		Collection<AndyManyToOne> manyToOnes = table.manyToOneMap.values();
		for(AndyManyToOne many:manyToOnes){
			AndyKeyValue kv = manyToOne2KeyValue(many,entity);
			if(kv!=null) keyValueList.add(kv);
		}
		
		return keyValueList;
	}
	
/******************************************** private method start *****************************************************/
	
	/**
	 * 将实体entity的property转换成key - value
	 * @param property AndyProperty
	 * @param entity Object
	 * */
	private static AndyKeyValue property2KeyValue(AndyProperty property , Object entity) {
		AndyKeyValue kv = null ;
		String pcolumn = property.getColumn();
		Object value = property.getValue(entity);
		if (value != null) {
			kv = new AndyKeyValue(pcolumn, value);
		} else {
			if (property.getDefaultValue() != null && property.getDefaultValue().trim().length() != 0)
				kv = new AndyKeyValue(pcolumn, property.getDefaultValue());
		}
		return kv;
	}
	
	/**
	 * manyToOne转换成keyvalue
	 * */
	@SuppressWarnings("rawtypes")
	private static AndyKeyValue manyToOne2KeyValue(AndyManyToOne many , Object entity){
		AndyKeyValue kv = null ;
		String manycolumn = many.getColumn();
		Object manyobject = many.getValue(entity);
		if (manyobject != null) {
			Object manyvalue;
            if (manyobject.getClass() == AndyManyToOneLazyLoader.class){
                manyvalue = AndyTableInfo.get(many.getManyClass()).getId().getValue(((AndyManyToOneLazyLoader)manyobject).get());
            } else {
                manyvalue = AndyTableInfo.get(manyobject.getClass()).getId().getValue(manyobject);
            }
			if (manycolumn != null && manyvalue != null) {
				kv = new AndyKeyValue(manycolumn, manyvalue);
			}
		}
		return kv;
	}
	
	/**
	 * 获取条件  key = value
	 * @param key
	 * @param value
	 */
	private static String getPropertyStrSql(String key, Object value) {
		StringBuffer sbSQL = new StringBuffer(key).append("=");
		if (value instanceof String || value instanceof java.util.Date || value instanceof java.sql.Date){
			sbSQL.append("'").append(value).append("'");
		} else {
			sbSQL.append(value);
		}
		return sbSQL.toString();
	}
	
	/**
	 * 根据表名获取删除表数据语句
	 * */
	private static String getDeleteSqlBytableName(String tableName) {
		return "DELETE FROM "+ tableName;
	}
	/**
	 * 查询语句前缀
	 * SELECT * FROM table_name ....
	 * */
	private static String getSelectSqlByTableName(String tableName) {
		return new StringBuffer("SELECT * FROM ").append(tableName).toString();
	}
/******************************************** private method end *******************************************************/
}
