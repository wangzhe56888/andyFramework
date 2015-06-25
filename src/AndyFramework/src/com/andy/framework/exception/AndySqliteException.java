package com.andy.framework.exception;
/**
 * @description: db异常
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:52:28
 */
public class AndySqliteException extends AndyException {
	private static final long serialVersionUID = 1L;

	public AndySqliteException() {}
	
	
	public AndySqliteException(String msg) {
		super(msg);
	}
	
	public AndySqliteException(Throwable ex) {
		super(ex);
	}
	
	public AndySqliteException(String msg,Throwable ex) {
		super(msg,ex);
	}
}
