/**
 * Media.java
 * TODO
 */
package com.ipanel.join.huawei.vod.control;

import org.json.JSONException;
import org.json.JSONObject;

import com.ipanel.join.huawei.vod.dvb.DVBToolkit;
import com.ipanel.join.sx.vodcontrol.Logger;

import android.util.Log;

/**
 * @author Zengk
 * 2012-9-20 上午11:54:30
 * TODO
 */
public class Media implements Logger {

	long frequency;
	long symbolrate;
	int qam;
	int serviceId;
	int pmtpid;
	String modulation;

	public Media(String json) {
		Log.d(TAG, "Media is " + json);
		try {
			JSONObject object = new JSONObject(json);
			// 思迁给的是底层直接能用的，iPanel VOD给的需要放大100倍
			frequency = object.getLong("frequency");
			symbolrate = object.getLong("symbolrate") * 100;
			qam = object.getInt("qam");
			modulation = DVBToolkit.switchModulation(qam);
			serviceId = object.getInt("service_id");
			pmtpid = object.getInt("pmt_pid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public long getFrequency() {
		return frequency;
	}
	
	public long getSymbolRate() {
		return symbolrate;
	}
	
	public int getQam() {
		return qam;
	}
	
	public int getServiceId() {
		return serviceId;
	}
	
	public int getPmtId() {
		return pmtpid;
	}

	public String getModulation() {
		return modulation;
	}

	Media() {
	}

	@Override
	public String toString() {
		return "Frequency = " + frequency + ", Symbol_Rate = " + symbolrate
				+ ", Modulation = " + modulation + ", Programe_Number = "
				+ serviceId;
	}

	@Override
	public Media clone() {
		Media clone = new Media();
		clone.frequency = frequency;
		clone.symbolrate = symbolrate;
		clone.qam = qam;
		clone.serviceId = serviceId;
		clone.pmtpid = pmtpid;
		clone.modulation = modulation;
		return clone;
	}
}