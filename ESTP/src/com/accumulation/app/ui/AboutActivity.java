package com.accumulation.app.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;

/**
 * 界面：关于我们
 * */
public class AboutActivity extends BaseActivity {

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		loadVersion();
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "关于我们";
	}

	/**
	 * 获取最新版本
	 * */
	private void loadVersion() {
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				TextView current_version = (TextView) findViewById(R.id.current_version);
				current_version.setText("最新版本：" + versionName);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_about;
	}

}
