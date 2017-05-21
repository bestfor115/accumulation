/**
 * Table.java
 * TODO
 */
package com.ipanel.join.huawei.vod.dvb;

/**
 * @author Zengk
 * 2012-9-20 ÉÏÎç10:26:17
 * TODO
 */
public abstract class Table {

	protected byte[] mData;
	protected int mTableID;
	protected int mSectionSyntaxIndicator;
	protected int mSectionLength;
	protected int mVersionNumber;
	protected int mCurrentNextIndicator;
	protected int mSectionNumber;
	protected int mLastSectionNumber;
	protected int mCRC32;

	protected Table(byte[] data) {
		mData = data;
	}

	public int getTableId() {
		return mTableID;
	}

	public int getSectionSyntaxIndicator() {
		return mSectionSyntaxIndicator;
	}

	public int getSectionLength() {
		return mSectionLength;
	}

	public int getVersionNumber() {
		return mVersionNumber;
	}

	public int getCurrentNextIndicator() {
		return mCurrentNextIndicator;
	}

	public int getSectionNumber() {
		return mSectionNumber;
	}

	public int getLastSectionNumber() {
		return mLastSectionNumber;
	}

	public int getCRC32() {
		return mCRC32; 
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < mData.length; i++) {
			buf.append(Integer.toString(mData[i] & 0xFF, 16)).append(" ");
			if (i % 8 == 0)
				buf.append("\r\n");
		}
		return buf.toString();
	}

	protected abstract void parseTable();
}