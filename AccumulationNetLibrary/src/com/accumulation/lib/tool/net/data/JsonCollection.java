package com.accumulation.lib.tool.net.data;

import java.io.Serializable;
import java.util.List;

public interface JsonCollection<T> extends Serializable{
	
	public List<T> getCollectionData();
	
	public T getDataAtIndex(int index);
	
	public int getCollectionCount();
	
	public int getCollectionTotalCount();

	public boolean isEmpty();
	
	public boolean isAuthenticated();
	
	public String getMessage();
	
}
