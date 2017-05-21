package com.accumulation.lib.tool.sociability.data;

import java.io.Serializable;

public abstract class JsonBase<T> implements
		JsonCollection<T> {
	public boolean success;
	public String msg;
	public boolean isAuthenticated;
	
	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return isAuthenticated;
	}
	
	public boolean isSuccess(){
		return success;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return msg==null?"":msg;
	}
	
	@Override
	public T getDataAtIndex(int index) {
		// TODO Auto-generated method stub
		return getCollectionData().get(index);
	}
}
