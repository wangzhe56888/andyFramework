package com.andy.framework.sqlite.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.andy.framework.exception.AndySqliteException;
import com.andy.framework.sqlite.annotation.AndyId;
import com.andy.framework.sqlite.annotation.AndyTable;
import com.andy.framework.sqlite.table.AndyManyToOne;
import com.andy.framework.sqlite.table.AndyOneToMany;
import com.andy.framework.sqlite.table.AndyProperty;

/**
 * @description:
 * @author: andy
 * @mail: win58@qq.com
 * @date: 2015-5-14 下午5:21:47
 */
public class AndyClassUtils {
	/**
	 * 根据实体类 获得 实体类对应的表名
	 * 
	 * @param entity
	 * @return
	 */
	public static String getTableName(Class<?> clazz) {
		AndyTable table = clazz.getAnnotation(AndyTable.class);
		if (table == null || table.name().trim().length() == 0) {
			// 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
			return clazz.getName().replace('.', '_');
		}
		return table.name();
	}

	public static Object getPrimaryKeyValue(Object entity) {
		return AndyFieldUtils.getFieldValue(entity,
				AndyClassUtils.getPrimaryKeyField(entity.getClass()));
	}

	/**
	 * 根据实体类 获得 实体类对应的表名
	 * 
	 * @param entity
	 * @return
	 */
	public static String getPrimaryKeyColumn(Class<?> clazz) {
		String primaryKey = null;
		Field[] fields = clazz.getDeclaredFields();
		if (fields != null) {
			AndyId idAnnotation = null;
			Field idField = null;

			for (Field field : fields) { // 获取ID注解
				idAnnotation = field.getAnnotation(AndyId.class);
				if (idAnnotation != null) {
					idField = field;
					break;
				}
			}

			if (idAnnotation != null) { // 有ID注解
				primaryKey = idAnnotation.column();
				if (primaryKey == null || primaryKey.trim().length() == 0)
					primaryKey = idField.getName();
			} else { // 没有ID注解,默认去找 _id 和 id 为主键，优先寻找 _id
				for (Field field : fields) {
					if ("_id".equals(field.getName()))
						return "_id";
				}

				for (Field field : fields) {
					if ("id".equals(field.getName()))
						return "id";
				}
			}
		} else {
			throw new RuntimeException("this model[" + clazz + "] has no field");
		}
		return primaryKey;
	}

	/**
	 * 根据实体类 获得 实体类对应的主键名 如果配置了注解AndyId则该属性为主键字段 否则查找属性id或者_id为主键
	 * 
	 * @param entity
	 * @return
	 */
	public static Field getPrimaryKeyField(Class<?> clazz) {
		Field primaryKeyField = null;
		Field[] fields = clazz.getDeclaredFields();
		if (fields != null) {
			for (Field field : fields) { // 获取ID注解
				if (field.getAnnotation(AndyId.class) != null) {
					primaryKeyField = field;
					break;
				}
			}

			if (primaryKeyField == null) { // 没有ID注解
				for (Field field : fields) {
					if ("_id".equals(field.getName())) {
						primaryKeyField = field;
						break;
					}
				}
			}

			if (primaryKeyField == null) { // 如果没有_id的字段
				for (Field field : fields) {
					if ("id".equals(field.getName())) {
						primaryKeyField = field;
						break;
					}
				}
			}

		} else {
			throw new AndySqliteException("this model[" + clazz
					+ "] has no id field");
		}
		return primaryKeyField;
	}

	/**
	 * 根据实体类 获得 实体类对应表的主键名称
	 * 
	 * @param entity
	 * @return
	 */
	public static String getPrimaryKeyFieldName(Class<?> clazz) {
		Field f = getPrimaryKeyField(clazz);
		return f == null ? null : f.getName();
	}

	/**
	 * 获取所有的属性列表
	 * 
	 * @param clazz
	 */
	public static List<AndyProperty> getPropertyList(Class<?> clazz) {

		List<AndyProperty> plist = new ArrayList<AndyProperty>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
			for (Field f : fs) {
				// 必须是基本数据类型和没有标瞬时态的字段
				if (!AndyFieldUtils.isTransient(f)) {
					if (AndyFieldUtils.isBaseDateType(f)) {
						// 过滤主键
						if (f.getName().equals(primaryKeyFieldName))
							continue;

						AndyProperty property = new AndyProperty();

						property.setColumn(AndyFieldUtils.getColumnByField(f));
						property.setFieldName(f.getName());
						property.setDataType(f.getType());
						property.setDefaultValue(AndyFieldUtils
								.getPropertyDefaultValue(f));
						property.setSet(AndyFieldUtils.getFieldSetMethod(clazz,
								f));
						property.setGet(AndyFieldUtils.getFieldGetMethod(clazz,
								f));
						property.setField(f);

						plist.add(property);
					}
				}
			}
			return plist;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 获取多对一属性列表
	 */
	public static List<AndyManyToOne> getManyToOneList(Class<?> clazz) {
		List<AndyManyToOne> mList = new ArrayList<AndyManyToOne>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				if (!AndyFieldUtils.isTransient(f)
						&& AndyFieldUtils.isManyToOne(f)) {

					AndyManyToOne mto = new AndyManyToOne();
					// 如果类型为ManyToOneLazyLoader则取第二个参数作为manyClass（一方实体）
					if (f.getType() == AndyManyToOneLazyLoader.class) {
						Class<?> pClazz = (Class<?>) ((ParameterizedType) f
								.getGenericType()).getActualTypeArguments()[1];
						if (pClazz != null)
							mto.setManyClass(pClazz);
					} else {
						mto.setManyClass(f.getType());
					}
					mto.setColumn(AndyFieldUtils.getColumnByField(f));
					mto.setFieldName(f.getName());
					mto.setDataType(f.getType());
					mto.setSet(AndyFieldUtils.getFieldSetMethod(clazz, f));
					mto.setGet(AndyFieldUtils.getFieldGetMethod(clazz, f));

					mList.add(mto);
				}
			}
			return mList;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 获取一对多属性列表
	 * 
	 * @return
	 */
	public static List<AndyOneToMany> getOneToManyList(Class<?> clazz) {

		List<AndyOneToMany> oList = new ArrayList<AndyOneToMany>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				if (!AndyFieldUtils.isTransient(f)
						&& AndyFieldUtils.isOneToMany(f)) {

					AndyOneToMany otm = new AndyOneToMany();

					otm.setColumn(AndyFieldUtils.getColumnByField(f));
					otm.setFieldName(f.getName());

					Type type = f.getGenericType();

					if (type instanceof ParameterizedType) {
						ParameterizedType pType = (ParameterizedType) f
								.getGenericType();
						// 如果类型参数为2则认为是LazyLoader
						if (pType.getActualTypeArguments().length == 1) {
							Class<?> pClazz = (Class<?>) pType
									.getActualTypeArguments()[0];
							if (pClazz != null)
								otm.setOneClass(pClazz);
						} else {
							Class<?> pClazz = (Class<?>) pType
									.getActualTypeArguments()[1];
							if (pClazz != null)
								otm.setOneClass(pClazz);
						}
					} else {
						throw new AndySqliteException(
								"getOneToManyList Exception:" + f.getName()
										+ "'s type is null");
					}
					/* 修正类型赋值错误的bug，f.getClass返回的是Filed */
					otm.setDataType(f.getType());
					otm.setSet(AndyFieldUtils.getFieldSetMethod(clazz, f));
					otm.setGet(AndyFieldUtils.getFieldGetMethod(clazz, f));

					oList.add(otm);
				}
			}
			return oList;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
