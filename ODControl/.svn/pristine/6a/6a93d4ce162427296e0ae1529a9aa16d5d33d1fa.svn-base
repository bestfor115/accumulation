/**
 * PATFilter.java
 * TODO
 */
package com.ipanel.join.huawei.vod.dvb;

import android.net.telecast.SectionFilter;

/**
 * @author Zengk
 * 2012-9-14 ионГ10:41:37
 * TODO
 */
public class PATFilter extends Filter {

	public PATFilter(SectionFilter filter) {
		super(filter);
		setTableID(PAT.TABLE_ID);
		setTablePID(PAT.TABLE_PID);
	}

	public void setAcceptionMode(int accept) {
		mFilter.setAcceptionMode(accept);
	}
	
	@Override
	protected Table createTable(byte[] buf) {
		// TODO Auto-generated method stub
		return new PAT(buf);
	}
}