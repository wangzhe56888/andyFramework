package com.andy.framework.sqlite.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import com.andy.framework.sqlite.annotation.AndyId;
import com.andy.framework.sqlite.annotation.AndyManyToOne;
import com.andy.framework.sqlite.annotation.AndyOneToMany;
import com.andy.framework.sqlite.annotation.AndyProperty;
import com.andy.framework.sqlite.annotation.AndyTransient;

/**
 * @description: Field工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:18:53
 */
@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class AndyFieldUtils {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 获取属性的get方法
	 * */
	public static Method getFieldGetMethod(Class<?> clazz, Field f) {
		String fn = f.getName();
		Method m = null;
		if(f.getType() == boolean.class){
			m = getBooleanFieldGetMethod(clazz, fn);
		}
		if(m == null ){
			m = getFieldGetMethod(clazz, fn);
		}
		return m;
	}
	
	/**
	 * 获取布尔型属性的get方法
	 * */
	public static Method getBooleanFieldGetMethod(Class<?> clazz, String fieldName) {
		String mn = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		if(isISStart(fieldName)){
			mn = fieldName;
		}
		try {
			return clazz.getDeclaredMethod(mn);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取布尔型数据的set方法
	 * 
	 * */
	public static Method getBooleanFieldSetMethod(Class<?> clazz, Field f) {
		String fn = f.getName();
		String mn = "set" + fn.substring(0, 1).toUpperCase() + fn.substring(1);
		if(isISStart(f.getName())){
			mn = "set" + fn.substring(2, 3).toUpperCase() + fn.substring(3);
		}
		try {
			return clazz.getDeclaredMethod(mn, f.getType());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 判断属性是否以is开头，并且is后的第一个字母大写
	 * */
	private static boolean isISStart(String fieldName) {
		if (fieldName == null || fieldName.trim().length() < 3)
			return false;
		//is开头，并且is之后第一个字母是大写 比如 isAdmin
		return fieldName.startsWith("is") && !Character.isLowerCase(fieldName.charAt(2));
	}
	
	/**
	 * 获取普通属性的get方法
	 * get + 属性（首字母大写）
	 * */
	public static Method getFieldGetMethod(Class<?> clazz, String fieldName) {
		String mn = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		try {
			return clazz.getDeclaredMethod(mn);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据属性获取clazz 的 set方法
	 * set + field（首字母大写）
	 * */
	public static Method getFieldSetMethod(Class<?> clazz, Field f) {
		String fn = f.getName();
		String mn = "set" + fn.substring(0, 1).toUpperCase() + fn.substring(1);
		try {
			return clazz.getDeclaredMethod(mn, f.getType());
		} catch (NoSuchMethodException e) {
			if(f.getType() == boolean.class){
				return getBooleanFieldSetMethod(clazz, f);
			}
		}
		return null;
	}
	
	public static Method getFieldSetMethod(Class<?> clazz, String fieldName) {
		try {
			return getFieldSetMethod(clazz, clazz.getDeclaredField(fieldName));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object entity,Field field){
		Method method = getFieldGetMethod(entity.getClass(), field);
		return invoke(entity, method);
	}
	
	/**
	 * 获取某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object entity,String fieldName){
		Method method = getFieldGetMethod(entity.getClass(), fieldName);
		return invoke(entity, method);
	}
	
	/**
	 * 设置某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static void setFieldValue(Object entity,Field field,Object value){
		try {
			Method set = getFieldSetMethod(entity.getClass(), field);
			if (set != null) {
				set.setAccessible(true);
				Class<?> type = field.getType();
				if (type == String.class) {
					set.invoke(entity, value.toString());
				} else if (type == int.class || type == Integer.class) {
					set.invoke(entity, value == null ? (Integer) null : Integer.parseInt(value.toString()));
				} else if (type == float.class || type == Float.class) {
					set.invoke(entity, value == null ? (Float) null: Float.parseFloat(value.toString()));
				} else if (type == long.class || type == Long.class) {
					set.invoke(entity, value == null ? (Long) null: Long.parseLong(value.toString()));
				} else if (type == Date.class) {
					set.invoke(entity, value == null ? (Date) null: stringToDateTime(value.toString()));
				} else {
					set.invoke(entity, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByColumnName(Class<?> clazz,String columnName){
		Field field = null;
		if(columnName!=null){
			Field[] fields = clazz.getDeclaredFields();
			if(fields!=null && fields.length>0){
				if(columnName.equals(AndyClassUtils.getPrimaryKeyColumn(clazz)))
					field = AndyClassUtils.getPrimaryKeyField(clazz);
					
				if(field == null){
					for(Field f : fields){
						AndyProperty property = f.getAnnotation(AndyProperty.class);
						if(property!=null && columnName.equals(property.column())){
							field = f;
							break;
						}
						
						AndyManyToOne manyToOne = f.getAnnotation(AndyManyToOne.class);
						if(manyToOne!=null && manyToOne.column().trim().length()!=0){
							field = f;
							break;
						}
					}
				}
				
				if(field == null){
					field = getFieldByName(clazz, columnName);
				}
			}
		}
		return field;
	}
	
	
	/**
	 * 获取某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByName(Class<?> clazz,String fieldName){
		Field field = null;
		if(fieldName!=null){
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return field;
	}
	
	
	
	/**
	 * 获取某个属性对应的 表的列名称
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static String getColumnByField(Field field){
		AndyProperty property = field.getAnnotation(AndyProperty.class);
		if (property != null && property.column() != null && property.column().trim().length() != 0) {
			return property.column();
		}
		
		AndyManyToOne manyToOne = field.getAnnotation(AndyManyToOne.class);
		if (manyToOne!=null && manyToOne.column() != null && manyToOne.column().trim().length()!=0) {
			return manyToOne.column();
		}
		
		AndyOneToMany oneToMany = field.getAnnotation(AndyOneToMany.class);
		if (oneToMany!=null && oneToMany.manyColumn()!=null &&oneToMany.manyColumn().trim().length()!=0) {
			return oneToMany.manyColumn();
		}

		AndyId id = field.getAnnotation(AndyId.class);
		if (id != null && id.column() != null && id.column().trim().length() != 0) {
			return id.column();
		}
		return field.getName();
	}
	
	/**
	 * 获取属性的默认值
	 * */
	public static String getPropertyDefaultValue(Field field){
		AndyProperty property = field.getAnnotation(AndyProperty.class);
		if(property != null && property.defaultValue().trim().length() != 0){
			return property.defaultValue();
		}
		return null ;
	}

	/**
	 * 检测 字段是否已经被标注为 非数据库字段
	 * @param f
	 * @return
	 */
	public static boolean isTransient(Field f) {
		return f.getAnnotation(AndyTransient.class) != null;
	}
	
	/**
	 * 获取某个实体执行某个方法的结果
	 * @param obj
	 * @param method
	 * @return
	 */
	private static Object invoke(Object obj , Method method){
		if(obj == null || method == null) return null;
		try {
			return method.invoke(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 是否为多对一
	 * */
	public static boolean isManyToOne(Field field){
		return field.getAnnotation(AndyManyToOne.class)!=null;
	}
	
	/**
	 * 是否为一对多配置
	 * */
	public static boolean isOneToMany(Field field){
		return field.getAnnotation(AndyOneToMany.class)!=null;
	}
	
	/**
	 * 一对多或者多对一
	 * */
	public static boolean isManyToOneOrOneToMany(Field field){
		return isManyToOne(field) || isOneToMany(field);
	}
	
	/**
	 * 检查字段是否为数据库可保持的数据类型
	 * */
	public static boolean isBaseDateType(Field field){
		Class<?> clazz = field.getType();
		return   clazz.equals(String.class) ||  
		         clazz.equals(Integer.class)||  
		         clazz.equals(Byte.class) ||  
		         clazz.equals(Long.class) ||  
		         clazz.equals(Double.class) ||  
		         clazz.equals(Float.class) ||  
		         clazz.equals(Character.class) ||  
		         clazz.equals(Short.class) ||  
		         clazz.equals(Boolean.class) ||  
		         clazz.equals(Date.class) ||  
		         clazz.equals(java.util.Date.class) ||
		         clazz.equals(java.sql.Date.class) ||
		         clazz.isPrimitive();
	}
	
	public static Date stringToDateTime(String strDate) {
		if (strDate != null) {
			try {
				return SDF.parse(strDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
