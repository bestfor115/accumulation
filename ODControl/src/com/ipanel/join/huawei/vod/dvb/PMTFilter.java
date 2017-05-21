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
public class PMTFilter extends Filter {

	public PMTFilter(SectionFilter filter) {
		super(filter);
		setTableID(PMT.TABLE_ID);
	}

	public void setPMTPid(int pid) {
		setTablePID(pid);
	}

	@Override
	protected Table createTable(byte[] buf) {
		// TODO Auto-generated method stub
		return new PMT(buf);
	}
}