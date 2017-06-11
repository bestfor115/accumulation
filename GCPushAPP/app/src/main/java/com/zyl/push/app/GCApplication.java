package com.zyl.push.app;


import android.app.Application;

import com.zyl.push.sdk.FileTransportManager;
import com.zyl.push.sdk.script.ScriptManager;


public class GCApplication extends Application {

	static GCApplication mInstance;
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance=this;
		ScriptManager.getManager().bindContext(this);
		FileTransportManager.getManager(this);
		Caches.loadData(this);
	}
	
	public static GCApplication getInstance(){
		return mInstance;
	} 
}
