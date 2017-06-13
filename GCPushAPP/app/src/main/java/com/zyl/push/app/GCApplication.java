package com.zyl.push.app;


import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import com.accumulation.lib.utility.debug.Logger;
import com.zyl.push.sdk.FileTransportManager;
import com.zyl.push.sdk.script.ScriptAccessibilityService;
import com.zyl.push.sdk.script.ScriptManager;

import java.util.List;


public class GCApplication extends Application {

	static GCApplication mInstance;
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance=this;
		ScriptManager.getManager().bindContext(this);
		FileTransportManager.getManager(this);
		Caches.loadData(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!checkAccessibilityService(ScriptAccessibilityService.class.getName())){
					Logger.d("checkAccessibilityService false");
				}
			}
		},1000);

	}
	
	public static GCApplication getInstance(){
		return mInstance;
	}

	private boolean checkAccessibilityService(String name) {
		AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
		List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
		List<AccessibilityServiceInfo> installedAccessibilityServiceList = am.getInstalledAccessibilityServiceList();
		for (AccessibilityServiceInfo info : installedAccessibilityServiceList) {
			if (name.equals(info.getId())) {
				return true;
			}
		}
		return false;
	}
}
