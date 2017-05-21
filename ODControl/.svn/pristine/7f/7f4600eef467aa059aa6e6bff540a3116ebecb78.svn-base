/**
 * PAT.java
 * TODO
 */
package com.ipanel.join.huawei.vod.dvb;

import java.util.HashMap;
import java.util.Map;

import com.ipanel.join.sx.vodcontrol.Logger;


import android.util.Log;

/**
 * @author Zengk
 * 2012-9-20 ÉÏÎç10:25:55
 * TODO
 */
public class PAT extends Table implements Logger {

	public static final byte TABLE_ID 	= 0x00;
	public static final int TABLE_PID 	= 0x00;

	int mTransportStreamID;
	Map<String, Program> mProgramMap;

	PAT(byte[] data) {
		super(data);
		mProgramMap = new HashMap<String, PAT.Program>();
	}

	@Override
	protected void parseTable() {
		Log.d(TAG, "<------------------------------------------------------------------------>");
		Log.d(TAG, "Table.ParseTable PAT, Length is " + mData.length);
		Log.d(TAG, "Table.PAT data is " + toString());
		int i = 0;

		mTableID 					=   mData[i++] & 0xFF;
		mSectionSyntaxIndicator 	= ( mData[i]   & 0x40) >> 7;
		mSectionLength 				= ((mData[i++] & 0x0F) << 8) | (mData[i++] & 0xFF);
		mTransportStreamID			= ((mData[i++] & 0xFF) << 8) | (mData[i++] & 0xFF);
		mVersionNumber 				= ( mData[i]   & 0x3E) >> 1;
		mCurrentNextIndicator 		=   mData[i++] & 0x01;
		mSectionNumber 				=   mData[i++] & 0xFF;
		mLastSectionNumber 			=   mData[i++] & 0xFF;

		Log.d(TAG, "	TableID                      = 0x" + Integer.toString(mTableID, 16));
		Log.d(TAG, "	Section_Syntax_Indicator     = 0x" + Integer.toString(mSectionSyntaxIndicator, 16));
		Log.d(TAG, "	Section_Length               = 0x" + Integer.toString(mSectionLength, 16));
		Log.d(TAG, "	Transport_Stream_ID          = 0x" + Integer.toString(mTransportStreamID, 16));
		Log.d(TAG, "	Version_Number               = 0x" + Integer.toString(mVersionNumber, 16));
		Log.d(TAG, "	Current_Next_Indicator       = 0x" + Integer.toString(mCurrentNextIndicator, 16));
		Log.d(TAG, "	Section_Number               = 0x" + Integer.toString(mSectionNumber, 16));
		Log.d(TAG, "	Last_Section_Number          = 0x" + Integer.toString(mLastSectionNumber, 16));
		Log.d(TAG, "<------------------------------------------------------------------------>");
		while (i < mData.length - 4) {
			if (mData.length - i > 4) {
				int program_number 	= ((mData[i++] & 0xFF) << 8) | (mData[i++] & 0xFF);
				int pid 			= ((mData[i++] & 0x1F) << 8) | (mData[i++] & 0xFF);
				Log.d(TAG, "	Program_Number               = 0x" + Integer.toString(program_number, 16));
				Log.d(TAG, "	Progrem_PID                  = 0x" + Integer.toString(pid, 16));
				mProgramMap.put(String.valueOf(program_number), new Program(program_number, pid));
			}
		}
		i = mData.length - 4;
		Log.d(TAG, "<------------------------------------------------------------------------>");
		int crc32 = ((mData[i++] & 0xFF) << 24) | ((mData[i++] & 0xFF) << 16) | ((mData[i++] & 0xFF) << 8) | (mData[i++] & 0xFF);
		Log.d(TAG, "	CRC32                        = 0x" + Integer.toString(crc32, 16));
		Log.d(TAG, "<------------------------------------------------------------------------>");
	}

	public int getTransportStreamID() {
		return mTransportStreamID;
	}

	public Program[] getProgramArray() {
		Program[] p = new Program[mProgramMap.size()];
		mProgramMap.values().toArray(p);
		return p;
	}

	public Program getProgram(int pNumber) {
		return mProgramMap.get(String.valueOf(pNumber));
	}

	public class Program {

		int mNumber;
		int mPID;

		Program(int n, int p) {
			mNumber = n;
			mPID = p;
		}

		public int getProgramNumber() {
			return mNumber;
		}

		public int getProgramePID() {
			return mPID;
		}
	}
}