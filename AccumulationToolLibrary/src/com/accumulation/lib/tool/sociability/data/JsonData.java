package com.accumulation.lib.tool.sociability.data;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

public class JsonData<T> extends JsonBase<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	public T data;

	@Override
	public List<T> getCollectionData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCollectionCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : 1;
	}

	@Override
	public int getCollectionTotalCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : 1;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return data == null;
	}

	@Override
	public T getDataAtIndex(int index) {
		// TODO Auto-generated method stub
		return data;
	}
}
