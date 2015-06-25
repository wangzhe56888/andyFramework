package com.andy.framework.exception;
/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:50:40
 */
public class AndyException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public AndyException() {
		super();
	}
	
	public AndyException(String msg) {
		super(msg);
	}
	
	public AndyException(Throwable ex) {
		super(ex);
	}
	
	public AndyException(String msg,Throwable ex) {
		super(msg,ex);
	}
}
