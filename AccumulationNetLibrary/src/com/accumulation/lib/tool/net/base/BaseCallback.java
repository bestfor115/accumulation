package com.accumulation.lib.tool.net.base;


public abstract class BaseCallback<T> {

	public abstract void onResultCallback(int code,String message,T result);
	
	public void onRequestStart(){
		
	}
	
	public void onRequestEnd(){
		
	}
	
}
