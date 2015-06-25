package com.andy.framework.sqlite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.andy.framework.exception.AndySqliteException;
import com.andy.framework.sqlite.core.AndyCursorUtils;
import com.andy.framework.sqlite.core.AndyDBModel;
import com.andy.framework.sqlite.core.AndyManyToOneLazyLoader;
import com.andy.framework.sqlite.core.AndyOneToManyLazyLoader;
import com.andy.framework.sqlite.core.AndySqlBuilder;
import com.andy.framework.sqlite.core.AndySqlInfo;
import com.andy.framework.sqlite.table.AndyKeyValue;
import com.andy.framework.sqlite.table.AndyManyToOne;
import com.andy.framework.sqlite.table.AndyOneToMany;
import com.andy.framework.sqlite.table.AndyTableInfo;
import com.andy.framework.util.AndyLog;


/**
 * @description: 暴露给用户的sqlite使用类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午4:38:07
 */
public class AndyDB {
	private static HashMap<String, AndyDB> daoMap = new HashMap<String, AndyDB>();
	private SQLiteDatabase db;
	private AndyDBConfig config;
	
	/**
	 * 根据配置AndyDBConfig初始化AndyDB
	 * */
	private AndyDB(AndyDBConfig config) {
		
		if (config == null) {
			throw new AndySqliteException("AndyDBConfig is null !");
		}
		
		if (config.getContext() == null) {
			throw new AndySqliteException("the context of AndyDBConfig is null !");
		}
		
		// 数据库文件保存到sdcard
		if (config.getTargetDirectory() != null && config.getTargetDirectory().trim().length() != 0) {
			db = createDbFileOnSDCard(config.getTargetDirectory(), config.getDbName());
		} else {
			db = new AndySqliteHelper(config.getContext(), config.getDbName(), 
					config.getDbVersion(), config.dbUpdateListener)
					.getWritableDatabase();
		}
		this.config = config;
	}
	
	private synchronized static AndyDB getInstance(AndyDBConfig config) {
		AndyDB andyDB = daoMap.get(config.getDbName());
		if (andyDB == null) {
			andyDB = new AndyDB(config);
			daoMap.put(config.getDbName(), andyDB);
		}
		return andyDB;
	}
	
	
	
/******************************************* 暴露的外部调用方法   public method  *****************************************************/
	
	/**
	 * 创建AndyDB实例
	 * */
	public static AndyDB create(AndyDBConfig config) {
		return getInstance(config);
	}
	
	public static AndyDB create(Context context) {
		return create(context, true);
	}
	
	public static AndyDB create(Context context, boolean isDebug) {
		return create(context, AndyDBConfig.DEFAULT_DB_NAME, isDebug);

	}

	public static AndyDB create(Context context, String dbName) {
		return create(context, dbName, true);
	}

	public static AndyDB create(Context context, String dbName, boolean isDebug) {
		return create(context, null, dbName, isDebug);
	}

	public static AndyDB create(Context context, String targetDirectory, String dbName) {
		return create(context, targetDirectory, dbName, true);
	}

	public static AndyDB create(Context context, String targetDirectory,
			String dbName, boolean isDebug) {
		return create(context, targetDirectory, dbName, isDebug, AndyDBConfig.DEFAULT_DB_VERSION, null);
	}

	public static AndyDB create(Context context, String dbName,
			boolean isDebug, int dbVersion, AndyDbUpdateListener dbUpdateListener) {
		return create(context, null, dbName, isDebug, dbVersion, dbUpdateListener);
	}

	/**
	 * 
	 * @param context 上下文
	 * @param targetDirectory db文件路径，可以配置为sdcard的路径
	 * @param dbName 数据库名字
	 * @param isDebug 是否是调试模式：调试模式会log出sql信息
	 * @param dbVersion 数据库版本信息
	 * @param dbUpdateListener数据库升级监听器 ：如果监听器为null，升级的时候将会清空所所有的数据
	 * @return
	 */
	public static AndyDB create(Context context, String targetDirectory,
			String dbName, boolean isDebug, int dbVersion,
			AndyDbUpdateListener dbUpdateListener) {
		AndyDBConfig config = new AndyDBConfig();
		if (context != null) {
			config.setContext(context);
		}
		if (targetDirectory != null) {
			config.setTargetDirectory(targetDirectory);
		}
		if (dbName != null) {
			config.setDbName(dbName);
		}
		if (dbUpdateListener != null) {
			config.setDbUpdateListener(dbUpdateListener);
		}
		config.setDebug(isDebug);
		config.setDbVersion(dbVersion);
		return create(config);
	}
	
	
	/**
	 * 保存entity到数据库，速度要比save快
	 * @param entity
	 */
	public void save(Object entity) {
		checkTableExist(entity.getClass());
		exeSqlInfo(AndySqlBuilder.buildInsertSql(entity));
	}
	
	/**
	 * 保存数据到数据库
	 * 保存成功后，entity的主键将被赋值（或更新）为数据库的主键， 只针对自增长的id有效
	 * @param entity 要保存的数据
	 * @return ture： 保存成功 false:保存失败
	 */
	public boolean saveBindId(Object entity) {
		checkTableExist(entity.getClass());
		List<AndyKeyValue> entityKvList = AndySqlBuilder.getSaveKeyValueListByEntity(entity);
		if (entityKvList != null && entityKvList.size() > 0) {
			AndyTableInfo tf = AndyTableInfo.get(entity.getClass());
			ContentValues cv = new ContentValues();
			insertContentValues(entityKvList, cv);
			Long id = db.insert(tf.getTableName(), null, cv);
			if (id == -1)
				return false;
			tf.getId().setValue(entity, id);
			return true;
		}
		return false;
	}
	
	/**
	 * 更新数据 （主键ID必须不能为空）
	 * @param entity
	 */
	public void update(Object entity) {
		checkTableExist(entity.getClass());
		exeSqlInfo(AndySqlBuilder.getUpdateSqlAsSqlInfo(entity));
	}
	
	/**
	 * 根据条件更新数据
	 * @param entity
	 * @param strWhere 条件为空的时候，将会更新所有的数据
	 */
	public void update(Object entity, String strWhere) {
		checkTableExist(entity.getClass());
		exeSqlInfo(AndySqlBuilder.getUpdateSqlAsSqlInfo(entity, strWhere));
	}
	
	/**
	 * 删除数据
	 * @param entity
	 * entity的主键不能为空
	 */
	public void delete(Object entity) {
		checkTableExist(entity.getClass());
		exeSqlInfo(AndySqlBuilder.buildDeleteSql(entity));
	}

	/**
	 * 根据主键删除数据
	 * @param clazz 要删除的实体类
	 * @param id 主键值
	 */
	public void deleteById(Class<?> clazz, Object id) {
		checkTableExist(clazz);
		exeSqlInfo(AndySqlBuilder.buildDeleteSql(clazz, id));
	}

	/**
	 * 根据条件删除数据
	 * @param clazz
	 * @param strWhere 条件为空的时候 将会删除所有的数据
	 */
	public void deleteByWhere(Class<?> clazz, String strWhere) {
		checkTableExist(clazz);
		String sql = AndySqlBuilder.buildDeleteSql(clazz, strWhere);
		debugSql(sql);
		db.execSQL(sql);
	}

	/**
	 * 删除表的所有数据
	 * @param clazz
	 */
	public void deleteAll(Class<?> clazz) {
		checkTableExist(clazz);
		String sql = AndySqlBuilder.buildDeleteSql(clazz, null);
		debugSql(sql);
		db.execSQL(sql);
	}

	/**
	 * 删除指定的表
	 * @param clazz
	 */
	public void dropTable(Class<?> clazz) {
		checkTableExist(clazz);
		AndyTableInfo table = AndyTableInfo.get(clazz);
		String sql = "DROP TABLE " + table.getTableName();
		debugSql(sql);
		db.execSQL(sql);
	}

	/**
	 * 删除所有数据表
	 */
	public void dropDb() {
		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence'", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				db.execSQL("DROP TABLE " + cursor.getString(0));
			}
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	
	
	
	/**
	 * 根据主键查找数据（默认不查询多对一或者一对多的关联数据）
	 * @param id
	 * @param clazz
	 */
	public <T> T findById(Object id, Class<T> clazz) {
		checkTableExist(clazz);
		AndySqlInfo sqlInfo = AndySqlBuilder.getSelectSqlAsSqlInfo(clazz, id);
		if (sqlInfo != null) {
			debugSql(sqlInfo.getSql());
			Cursor cursor = db.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStringArray());
			try {
				if (cursor.moveToNext()) {
					return AndyCursorUtils.getEntity(cursor, clazz, this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * 根据主键查找，同时查找“多对一”的数据（如果有多个“多对一”属性，则查找所有的“多对一”属性）
	 * @param id
	 * @param clazz
	 */
	public <T> T findWithManyToOneById(Object id, Class<T> clazz) {
		checkTableExist(clazz);
		String sql = AndySqlBuilder.getSelectSQL(clazz, id);
		debugSql(sql);
		AndyDBModel dbModel = findDbModelBySQL(sql);
		if (dbModel != null) {
			T entity = AndyCursorUtils.dbModel2Entity(dbModel, clazz);
			return loadManyToOne(dbModel, entity, clazz);
		}
		return null;
	}

	/**
	 * 根据条件查找，同时查找“多对一”的数据（只查找findClass中的类的数据）
	 * @param id
	 * @param clazz
	 * @param findClass 要查找的类
	 */
	public <T> T findWithManyToOneById(Object id, Class<T> clazz,
			Class<?>... findClass) {
		checkTableExist(clazz);
		String sql = AndySqlBuilder.getSelectSQL(clazz, id);
		debugSql(sql);
		AndyDBModel dbModel = findDbModelBySQL(sql);
		if (dbModel != null) {
			T entity = AndyCursorUtils.dbModel2Entity(dbModel, clazz);
			return loadManyToOne(dbModel, entity, clazz, findClass);
		}
		return null;
	}

	/**
	 * 将entity中的“多对一”的数据填充满 如果是懒加载填充，则dbModel参数可为null
	 * @param clazz
	 * @param entity
	 * @param <T>
	 * @return
	 */
	public <T> T loadManyToOne(AndyDBModel dbModel, T entity, Class<T> clazz,
			Class<?>... findClass) {
		if (entity != null) {
			try {
				Collection<AndyManyToOne> manys = AndyTableInfo.get(clazz).manyToOneMap.values();
				for (AndyManyToOne many : manys) {
					Object id = null;
					if (dbModel != null) {
						id = dbModel.get(many.getColumn());
					} else if (many.getValue(entity).getClass() == AndyManyToOneLazyLoader.class
							&& many.getValue(entity) != null) {
						id = ((AndyManyToOneLazyLoader) many.getValue(entity))
								.getFieldValue();
					}

					if (id != null) {
						boolean isFind = false;
						if (findClass == null || findClass.length == 0) {
							isFind = true;
						}
						for (Class<?> mClass : findClass) {
							if (many.getManyClass() == mClass) {
								isFind = true;
								break;
							}
						}
						if (isFind) {

							@SuppressWarnings("unchecked")
							T manyEntity = (T) findById(Integer.valueOf(id.toString()), many.getManyClass());
							if (manyEntity != null) {
								if (many.getValue(entity).getClass() == AndyManyToOneLazyLoader.class) {
									if (many.getValue(entity) == null) {
										many.setValue(entity, new AndyManyToOneLazyLoader(entity, clazz, many.getManyClass(), this));
									}
									((AndyManyToOneLazyLoader) many.getValue(entity)).set(manyEntity);
								} else {
									many.setValue(entity, manyEntity);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	/**
	 * 根据主键查找，同时查找“一对多”的数据（如果有多个“一对多”属性，则查找所有的一对多”属性）
	 * @param id
	 * @param clazz
	 */
	public <T> T findWithOneToManyById(Object id, Class<T> clazz) {
		checkTableExist(clazz);
		String sql = AndySqlBuilder.getSelectSQL(clazz, id);
		debugSql(sql);
		AndyDBModel dbModel = findDbModelBySQL(sql);
		if (dbModel != null) {
			T entity = AndyCursorUtils.dbModel2Entity(dbModel, clazz);
			return loadOneToMany(entity, clazz);
		}

		return null;
	}

	/**
	 * 根据主键查找，同时查找“一对多”的数据（只查找findClass中的“一对多”）
	 * @param id
	 * @param clazz
	 * @param findClass
	 */
	public <T> T findWithOneToManyById(Object id, Class<T> clazz,
			Class<?>... findClass) {
		checkTableExist(clazz);
		String sql = AndySqlBuilder.getSelectSQL(clazz, id);
		debugSql(sql);
		AndyDBModel dbModel = findDbModelBySQL(sql);
		if (dbModel != null) {
			T entity = AndyCursorUtils.dbModel2Entity(dbModel, clazz);
			return loadOneToMany(entity, clazz, findClass);
		}

		return null;
	}

	/**
	 * 将entity中的“一对多”的数据填充满
	 * @param entity
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public <T> T loadOneToMany(T entity, Class<T> clazz, Class<?>... findClass) {
		if (entity != null) {
			try {
				Collection<AndyOneToMany> ones = AndyTableInfo.get(clazz).oneToManyMap
						.values();
				Object id = AndyTableInfo.get(clazz).getId().getValue(entity);
				for (AndyOneToMany one : ones) {
					boolean isFind = false;
					if (findClass == null || findClass.length == 0) {
						isFind = true;
					}
					for (Class<?> mClass : findClass) {
						if (one.getOneClass() == mClass) {
							isFind = true;
							break;
						}
					}

					if (isFind) {
						List<?> list = findAllByWhere(one.getOneClass(),
                                one.getColumn() + "=" + id);
						if (list != null) {
							/* 如果是OneToManyLazyLoader泛型，则执行灌入懒加载数据 */
							if (one.getDataType() == AndyOneToManyLazyLoader.class) {
								AndyOneToManyLazyLoader oneToManyLazyLoader = one.getValue(entity);
								oneToManyLazyLoader.setList(list);
							} else {
								one.setValue(entity, list);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	/**
	 * 查找所有的数据
	 * 
	 * @param clazz
	 */
	public <T> List<T> findAll(Class<T> clazz) {
		checkTableExist(clazz);
		return findAllBySql(clazz, AndySqlBuilder.getSelectSQL(clazz));
	}

	/**
	 * 查找所有数据
	 * @param clazz
	 * @param orderBy 排序的字段
	 */
	public <T> List<T> findAll(Class<T> clazz, String orderBy) {
		checkTableExist(clazz);
		return findAllBySql(clazz, AndySqlBuilder.getSelectSQL(clazz)
				+ " ORDER BY " + orderBy);
	}

	/**
	 * 根据条件查找所有数据
	 * @param clazz
	 * @param strWhere  条件为空的时候查找所有数据
	 */
	public <T> List<T> findAllByWhere(Class<T> clazz, String strWhere) {
		checkTableExist(clazz);
		return findAllBySql(clazz,
				AndySqlBuilder.getSelectSQLByWhere(clazz, strWhere));
	}

	/**
	 * 根据条件查找所有数据
	 * @param clazz
	 * @param strWhere 条件为空的时候查找所有数据
	 * @param orderBy 排序字段
	 */
	public <T> List<T> findAllByWhere(Class<T> clazz, String strWhere,
			String orderBy) {
		checkTableExist(clazz);
		return findAllBySql(clazz,
				AndySqlBuilder.getSelectSQLByWhere(clazz, strWhere) + " ORDER BY "
						+ orderBy);
	}

	/**
	 * 根据条件查找所有数据
	 * @param clazz
	 * @param strSQL
	 */
	private <T> List<T> findAllBySql(Class<T> clazz, String strSQL) {
		checkTableExist(clazz);
		debugSql(strSQL);
		Cursor cursor = db.rawQuery(strSQL, null);
		try {
			List<T> list = new ArrayList<T>();
			while (cursor.moveToNext()) {
				T t = AndyCursorUtils.getEntity(cursor, clazz, this);
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}
		return null;
	}

	/**
	 * 根据sql语句查找数据，这个一般用于数据统计
	 * @param strSQL
	 */
	public AndyDBModel findDbModelBySQL(String strSQL) {
		debugSql(strSQL);
		Cursor cursor = db.rawQuery(strSQL, null);
		try {
			if (cursor.moveToNext()) {
				return AndyCursorUtils.getDbModel(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}

	public List<AndyDBModel> findDbModelListBySQL(String strSQL) {
		debugSql(strSQL);
		Cursor cursor = db.rawQuery(strSQL, null);
		List<AndyDBModel> dbModelList = new ArrayList<AndyDBModel>();
		try {
			while (cursor.moveToNext()) {
				dbModelList.add(AndyCursorUtils.getDbModel(cursor));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return dbModelList;
	}
	
	
	
	
/******************************************* 私有方法   private method  *****************************************************/
	/**
	 * 在SD卡的指定目录上创建文件
	 * @param sdcardPath
	 * @param dbfilename
	 * @return
	 */
	private SQLiteDatabase createDbFileOnSDCard(String sdcardPath, String dbfilename) {
		File dbf = new File(sdcardPath, dbfilename);
		if (!dbf.exists()) {
			try {
				if (dbf.createNewFile()) {
					return SQLiteDatabase.openOrCreateDatabase(dbf, null);
				}
			} catch (IOException ioex) {
				throw new AndySqliteException("数据库文件创建失败", ioex);
			}
		} else {
			return SQLiteDatabase.openOrCreateDatabase(dbf, null);
		}
		return null;
	}
	
	/**
	 * 检查表是否存在，不存在则创建
	 * */
	private void checkTableExist(Class<?> clazz) {
		if (!tableIsExist(AndyTableInfo.get(clazz))) {
			String sql = AndySqlBuilder.getCreatTableSQL(clazz);
			debugSql(sql);
			db.execSQL(sql);
		}
	}
	
	/**
	 * 检查表是否存在
	 * */
	private boolean tableIsExist(AndyTableInfo table) {
		if (table.isCheckDatabese())
			return true;

		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='"
					+ table.getTableName() + "' ";
			debugSql(sql);
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					table.setCheckDatabese(true);
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}

		return false;
	}
	
	/**
	 * 执行Sql info
	 * */
	private void exeSqlInfo(AndySqlInfo sqlInfo) {
		if (sqlInfo != null) {
			debugSql(sqlInfo.getSql());
			db.execSQL(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray());
		} else {
			AndyLog.error("sava error:sqlInfo is null");
		}
	}
	/**
	 * 把List<AndyKeyValue>数据存储到ContentValues
	 * @param list
	 * @param cv
	 */
	private void insertContentValues(List<AndyKeyValue> list, ContentValues cv) {
		if (list != null && cv != null) {
			for (AndyKeyValue kv : list) {
				cv.put(kv.getKey(), kv.getValue().toString());
			}
		} else {
			AndyLog.warn("insertContentValues: List<KeyValue> is empty or ContentValues is empty!");
		}

	}
	/**
	 * 日志打印
	 * */
	private void debugSql(String sql) {
		if (config != null && config.isDebug()) {
			AndyLog.info("DEBUG SQL : " + sql);
		}
	}
	
	
	
	
/**************************************** 内部内 ***************************************************/
	/**
	 * 数据库版本更新回调接口
	 * */
	public interface AndyDbUpdateListener {
		void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
	
	/**
	 * DB配置类
	 * */
	public static class AndyDBConfig {
		public static final String DEFAULT_DB_NAME = "andy_framework.db";
		public static final int DEFAULT_DB_VERSION = 1;
		
		private Context mContext = null; // android上下文
		private String mDbName = DEFAULT_DB_NAME; // 数据库名字
		private int dbVersion = DEFAULT_DB_VERSION; // 数据库版本
		private boolean debug = true; // 是否是调试模式（调试模式 增删改查的时候显示SQL语句）
		private AndyDbUpdateListener dbUpdateListener;
		private String targetDirectory;// 数据库文件在sd卡中的目录

		public Context getContext() {
			return mContext;
		}

		public void setContext(Context context) {
			this.mContext = context;
		}

		public String getDbName() {
			return mDbName;
		}

		public void setDbName(String dbName) {
			this.mDbName = dbName;
		}

		public int getDbVersion() {
			return dbVersion;
		}

		public void setDbVersion(int dbVersion) {
			this.dbVersion = dbVersion;
		}

		public boolean isDebug() {
			return debug;
		}

		public void setDebug(boolean debug) {
			this.debug = debug;
		}

		public AndyDbUpdateListener getDbUpdateListener() {
			return dbUpdateListener;
		}

		public void setDbUpdateListener(AndyDbUpdateListener dbUpdateListener) {
			this.dbUpdateListener = dbUpdateListener;
		}

		public String getTargetDirectory() {
			return targetDirectory;
		}

		public void setTargetDirectory(String targetDirectory) {
			this.targetDirectory = targetDirectory;
		}
	}

	/**
	 * Sqlite helper
	 * */
	public class AndySqliteHelper extends SQLiteOpenHelper {
		private AndyDbUpdateListener mDbUpdateListener;

		public AndySqliteHelper(Context context, String name, int version,
				AndyDbUpdateListener dbUpdateListener) {
			super(context, name, null, version);
			this.mDbUpdateListener = dbUpdateListener;
		}

		@Override
        public void onCreate(SQLiteDatabase db) {
		}

		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (mDbUpdateListener != null) {
				mDbUpdateListener.onUpgrade(db, oldVersion, newVersion);
			} else { 
				// 清空所有的数据信息
				dropDb();
			}
		}
		
	}
}
