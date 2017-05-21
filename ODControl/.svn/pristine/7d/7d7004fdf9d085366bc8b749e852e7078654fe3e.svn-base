/**
 * Filter.java
 * TODO
 */
package com.ipanel.join.huawei.vod.dvb;



import com.ipanel.join.sx.vodcontrol.Logger;

import android.net.telecast.SectionFilter;
import android.util.Log;

/**
 * @author Zengk
 * 2012-9-14 上午11:00:29
 * TODO
 */
public abstract class Filter implements Logger {

	public static final int MSG_RECEIVE_TIMEOUT = 0x00;
	public static final int MSG_STREAM_LOST	= 0x01;
	public static final int MSG_RECEIVE_TABLE = 0x02;

	byte mTableID 	= 0;	/** 要搜索的表ID  **/
	int mPID 		= 0;	/** 要搜索的表PID **/
	int mTimeout 	= 10;	/** 超时，单位秒，小于0表示不超时    **/ 

	SectionFilter mFilter;
	OnFilterCallback mCallback;
	boolean started = false;
	Thread mThread = null;

	protected Filter(SectionFilter filter) {
		if (filter == null)
			throw new RuntimeException("SectionFilter is null...");
		mFilter = filter;
		mFilter.setCARequired(false);
		mFilter.setAcceptionMode(SectionFilter.ACCEPT_ONCE);
		mFilter.setSectionDisposeListener(new OnSectionDispose());
	}

	public void setOnFilterCallback(OnFilterCallback callback) {
		mCallback = callback;
	}

	public OnFilterCallback getOnFilterCallback() {
		return mCallback;
	}

	public void setFrequency(long f) {
		mFilter.setFrequency(f);
	}

	public void setTimeout(int s) {
		mTimeout = s;
	}

	public synchronized void startFilter() {
		if (started)
			return;
		started = true;
		byte[] coef = new byte[] { mTableID, 0, 0, 0 };
		byte[] mask = new byte[] { (byte) 0xFF, 0, 0, 0 };
		byte[] excl = new byte[] { 0, 0, 0, 0 };
		if (mTimeout > 0) {
			mFilter.setTimeout(mTimeout * 1000);
			Log.d(TAG, "Filter.setTimeout " + mTimeout + "s");
		}
		mFilter.start(mPID, coef, mask, excl, 4);
		Log.d(TAG, "Filter.startFilter...");
	}

	public synchronized void stopFilter() {
		if (!started)
			return;
		started = false;
		mFilter.stop();
		Log.d(TAG, "Filter.stopFilter...");
	}

	public synchronized boolean isStart() {
		return started;
	}

	protected void setTableID(byte tableid) {
		mTableID = tableid;
	}

	protected void setTablePID(int pid) {
		mPID = pid;
	}

	protected abstract Table createTable(byte[] buf);

	public interface OnFilterCallback {

		public void onMessage(Filter f, int msg, Table t);
	}

	class OnSectionDispose implements SectionFilter.SectionDisposeListener {

		@Override
		public void onStreamLost(SectionFilter f) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Filter.onStreamLost...");
			if (mCallback != null) {
				mCallback.onMessage(Filter.this, MSG_STREAM_LOST, null);
			}
		}

		@Override
		public void onSectionRetrieved(SectionFilter f, int len) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Filter.onSectionRetrieved...");

			if (mThread != null)
				return;

			byte[] buf = new byte[len];
			f.readSection(buf, 0, len);

			mThread = new Thread(new FilterParseRunnable(createTable(buf)));
			mThread.start();
		}

		@Override
		public void onReceiveTimeout(SectionFilter f) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Filter.onTimeout...");
			if (mCallback != null) {
				mCallback.onMessage(Filter.this, MSG_RECEIVE_TIMEOUT, null);
			}
		}
	}

	class FilterParseRunnable implements Runnable {

		Table mTable;

		FilterParseRunnable(Table table) {
			mTable = table;
		}

		@Override
		public void run() {
			try {
				mTable.parseTable();
			} catch (Exception e) {
				Log.e(TAG, "Filter.ParseTable Exception, the message is " + e.getMessage());
			}
			mThread = null;
			if (mCallback != null) {
				mCallback.onMessage(Filter.this, MSG_RECEIVE_TABLE, mTable);
			}
		}
	}
}