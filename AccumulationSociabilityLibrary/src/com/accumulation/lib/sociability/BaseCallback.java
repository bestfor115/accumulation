package com.accumulation.lib.sociability;


public abstract class BaseCallback<T> {

	public abstract void onResultCallback(int code,String message,T result);
	
	public void onRequestStart(){
		
	}
	
	public void onRequestEnd(){
		
	}
	
}
