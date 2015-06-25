package com.andy.framework.sqlite.core;

import java.util.ArrayList;
import java.util.List;

import com.andy.framework.sqlite.AndyDB;

/**
 * @description: 一对多延迟加载类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-15  上午10:15:15
 * @param <O> 宿主实体的class
 * @param <M> 多放实体class
 */
public class AndyOneToManyLazyLoader<O, M> {

	public O ownerEntity;
	public Class<O> ownerClazz;
	public Class<M> listItemClazz;
	
	public List<M> entities;
	
	public AndyDB db;
	
	/**
	 * @param ownerEntity 宿主实体
	 * @param ownerClazz 宿主Class
	 * @param listItemclazz 多实体
	 * @param db
	 * */
	public AndyOneToManyLazyLoader(O ownerEntity, Class<O> ownerClazz, Class<M> listItemclazz,AndyDB db){
        this.ownerEntity = ownerEntity;
        this.ownerClazz = ownerClazz;
        this.listItemClazz = listItemclazz;
        this.db = db;
    }
	
	/**
     * 如果数据未加载，则调用loadOneToMany填充数据
     * @return
     */
    public List<M> getList() {
        if (entities == null) {
            db.loadOneToMany((O)ownerEntity, ownerClazz, listItemClazz);
        }
        if (entities == null) {
            entities = new ArrayList<M>();
        }
        return entities;
    }
    public void setList(List<M> value){
        entities = value;
    }
}
