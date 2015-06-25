package com.andy.framework.sqlite.core;

import com.andy.framework.sqlite.AndyDB;


/**
 * @description: 多对一延迟加载类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-15  上午10:24:57
 */
public class AndyManyToOneLazyLoader<M, O> {
	public M manyEntity;
	public Class<M> manyClazz;
	public Class<O> oneClazz;
	public AndyDB db;
    public O oneEntity;
    public boolean hasLoaded = false;
    private Object fieldValue;
    
    public AndyManyToOneLazyLoader(M manyEntity, Class<M> manyClazz, Class<O> oneClazz, AndyDB db){
        this.manyEntity = manyEntity;
        this.manyClazz = manyClazz;
        this.oneClazz = oneClazz;
        this.db = db;
    }

    /**
     * 如果数据未加载，则调用loadManyToOne填充数据
     * @return
     */
    public O get(){
        if(oneEntity==null && !hasLoaded){
            db.loadManyToOne(null,this.manyEntity,this.manyClazz,this.oneClazz);
            hasLoaded = true;
        }
        return oneEntity;
    }
    public void set(O value){
        oneEntity = value;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }
}
