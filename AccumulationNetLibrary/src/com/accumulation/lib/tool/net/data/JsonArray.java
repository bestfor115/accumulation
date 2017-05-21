package com.accumulation.lib.tool.net.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public  class JsonArray<T> extends JsonBase<T> {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	public ArrayList<T> data;

	@Override
	public List<T> getCollectionData() {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	public int getCollectionCount() {
		// TODO Auto-generated method stub
		return data==null?0:data.size();
	}
	
	@Override
	public int getCollectionTotalCount() {
		// TODO Auto-generated method stub
		return data==null?0:data.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return data!=null&&data.isEmpty();
	}


	
}