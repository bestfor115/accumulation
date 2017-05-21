package com.accumulation.lib.tool.net.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class JsonList<T> extends JsonBase<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	public Data<T> data;

	@SuppressWarnings("hiding")
	public class Data<T> {
		public ArrayList<T> list;
		public int recordCount;
	}

	@Override
	public List<T> getCollectionData() {
		// TODO Auto-generated method stub
		return data == null ? null : data.list;
	}

	@Override
	public int getCollectionCount() {
		// TODO Auto-generated method stub
		return data != null && data.list != null ? data.list.size() : 0;
	}

	@Override
	public int getCollectionTotalCount() {
		// TODO Auto-generated method stub
		return data != null ? data.recordCount : 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return data != null && data.list != null && data.list.isEmpty();
	}

}
