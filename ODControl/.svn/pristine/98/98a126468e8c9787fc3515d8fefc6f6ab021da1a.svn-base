/**
 * PMT.java
 * TODO
 */
package com.ipanel.join.huawei.vod.dvb;

import java.util.ArrayList;
import java.util.List;

import com.ipanel.join.sx.vodcontrol.Logger;

import android.util.Log;

/**
 * @author Zengk
 * 2012-9-20 ÉÏÎç10:42:44
 * TODO
 */
public class PMT extends Table implements Logger {

	public static final byte TABLE_ID = 0x02;

	int mProgramNumber;
	int mPcrPID;
	List<Stream> mStreams;

	PMT(byte[] data) {
		super(data);
		mStreams = new ArrayList<PMT.Stream>();
	}

	@Override
	protected void parseTable() {
		Log.d(TAG, "<------------------------------------------------------------------------>");
		Log.d(TAG, "Table.ParseTable PMT, Length is " + mData.length);
		Log.d(TAG, "Table.PMT data is " + toString());
		int i = 0;

		mTableID 					=   mData[i++] & 0xFF;
		mSectionSyntaxIndicator 	= ( mData[i]   & 0x80) >> 7;
		mSectionLength 				= ((mData[i++] & 0x0F) << 8) | (mData[i++] & 0xFF);
		mProgramNumber 				= ((mData[i++] & 0xFF) << 8) | (mData[i++] & 0xFF);
		mVersionNumber 				= ( mData[i]   & 0x3E) >> 1;
		mCurrentNextIndicator 		=   mData[i++] & 0x01;
		mSectionNumber 				=   mData[i++] & 0xFF;
		mLastSectionNumber 			=   mData[i++] & 0xFF;
		mPcrPID						= ((mData[i++] & 0x1F) << 8) | (mData[i++] & 0xFF);
		int program_info_length 	= ((mData[i++] & 0x0F) << 8) | (mData[i++] & 0xFF);

		Log.d(TAG, "	TableID                      = 0x" + Integer.toString(mTableID, 16));
		Log.d(TAG, "	Section_Syntax_Indicator     = 0x" + Integer.toString(mSectionSyntaxIndicator, 16));
		Log.d(TAG, "	Section_Length               = 0x" + Integer.toString(mSectionLength, 16));
		Log.d(TAG, "	Program_Number               = 0x" + Integer.toString(mProgramNumber, 16));
		Log.d(TAG, "	Version_Number               = 0x" + Integer.toString(mVersionNumber, 16));
		Log.d(TAG, "	Current_Next_Indicator       = 0x" + Integer.toString(mCurrentNextIndicator, 16));
		Log.d(TAG, "	Section_Number               = 0x" + Integer.toString(mSectionNumber, 16));
		Log.d(TAG, "	Last_Section_Number          = 0x" + Integer.toString(mLastSectionNumber, 16));
		Log.d(TAG, "	PCR_Pid                      = 0x" + Integer.toString(mPcrPID, 16));
		Log.d(TAG, "	Program_info_length          = 0x" + Integer.toString(program_info_length, 16));

		i += program_info_length;
		Log.d(TAG, "<------------------------------------------------------------------------>");
		while (i < mData.length - 4) {
			if (mData.length - i > 5) {
				int stream_type 		= mData[i++] & 0xFF;
				int elementary_pid 		= ((mData[i++] & 0x1F) << 8) | (mData[i++] & 0xFF);
				int es_info_length		= ((mData[i++] & 0x0F) << 8) | (mData[i++] & 0xFF);
				Log.d(TAG, "	Stream_Type                  = 0x" + Integer.toString(stream_type, 16));
				Log.d(TAG, "	Elementary_PID               = 0x" + Integer.toString(elementary_pid, 16));
				Log.d(TAG, "	ES_Info_Length               = 0x" + Integer.toString(es_info_length, 16));
				i += es_info_length;
				mStreams.add(new Stream(stream_type, elementary_pid));
			}
		}
		i = mData.length - 4;
		Log.d(TAG, "<------------------------------------------------------------------------>");
		int crc32 = ((mData[i++] & 0xFF) << 24) | ((mData[i++] & 0xFF) << 16) | ((mData[i++] & 0xFF) << 8) | (mData[i++] & 0xFF);
		Log.d(TAG, "	CRC32                        = 0x" + Integer.toString(crc32, 16));
		Log.d(TAG, "<------------------------------------------------------------------------>");
	}

	public int getProgramNumber() {
		return mProgramNumber;
	}

	public int getPcrPID() {
		return mPcrPID;
	}

	public Stream[] getStreamArray() {
		Stream[] s = new Stream[mStreams.size()];
		mStreams.toArray(s);
		return s;
	}

	public class Stream {

		int streamType;
		int elementaryPid;

		Stream(int st, int ep) {
			streamType = st;
			elementaryPid = ep;
		}

		public int getStreamStype() {
			return streamType;
		}

		public int getElementaryPid() {
			return elementaryPid;
		}
	}
}