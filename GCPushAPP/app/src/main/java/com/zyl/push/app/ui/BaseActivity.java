package com.zyl.push.app.ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accumulation.lib.utility.debug.Logger;
import com.accumulation.lib.utility.statusbar.SystemBarTintManager;
import com.zyl.push.app.util.SaveManager;


public abstract class BaseActivity extends FragmentActivity {
	protected ViewGroup rootLayout;
	protected int statusBarHeight;

	protected abstract int getLayoutResId();

	protected SystemBarTintManager tintManager;
	protected View menuView;
	protected View commitView;
	protected View toolbar;
	protected ImageView menuIcon;
	protected TextView titleView;
	protected float density;
	protected int theme = 0;
	protected String TAG = "";
	protected boolean pause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (savedInstanceState == null) {
			theme = SaveManager.getInstance(this).getAppTheme();
		} else {
			theme = savedInstanceState.getInt("theme");
		}
		setTheme(theme);
		super.onCreate(savedInstanceState);

		TAG = this.getClass().getSimpleName();
		Logger.d(TAG, "onCreate");
		density = this.getResources().getDisplayMetrics().density;
		pause = false;
		initDataFromIntent(getIntent());
		setContentView(getLayoutResId());
		initCommonView();
		initStatuBar();
		initView();
		startLoadData();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (theme == SaveManager.getInstance(this).getAppTheme()) {

		} else {
			reload();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Logger.d(TAG, "onPause");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.d(TAG, "onDestroy");
		pause = true;

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", theme);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Logger.d(TAG, "onNewIntent");
		setIntent(intent);
	}
	public int changeDx(int value) {
		return (int) (density * value);
	}

	public void reload() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	protected void initCommonView() {
		rootLayout = (ViewGroup) findViewById(R.id.root);
		toolbar=findViewById(R.id.top);
		View back = findViewById(R.id.back);
		if (back != null) {
			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onBack();
					onBackPressed();
				}
			});
		}

		menuView = findViewById(R.id.menu);
		if (menuView != null) {
			menuView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onMenu();
				}
			});
			if (menuView instanceof TextView) {
				if (getMenuTitle() != null) {
					((TextView) menuView).setText(getMenuTitle());
				}
			} else {
				menuIcon = (ImageView) menuView.findViewWithTag("image");
				if (menuIcon != null) {
					if (getMenuRes() > 0) {
						menuIcon.setImageResource(getMenuRes());
					}
				}
			}

		}
		titleView = (TextView) findViewById(R.id.title);
		if (titleView != null) {
			String s = getTopTitle();
			if (!TextUtils.isEmpty(s)) {
				titleView.setText(s);
			}
		}
		commitView = findViewById(R.id.commit);
		if (commitView != null) {
			commitView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onCommit();
				}


			});
		}
	}

	protected void initView() {

	}

	protected void initDataFromIntent(Intent intent) {

	}
	
	protected void startLoadData(){
		
	}

	protected void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	protected void toast(int msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected int getStatusBarResId() {
		return R.color.toolbar_bg_color;
	}

	protected void initStatuBar() {
		int res = getStatusBarResId();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (res > 0) {
				setTranslucentStatus(true);
				tintManager = new SystemBarTintManager(this);
				statusBarHeight = tintManager.getConfig().getStatusBarHeight();
				tintManager.setStatusBarTintEnabled(true);
				tintManager.setStatusBarTintColor(getResources().getColor(res));
				if (rootLayout != null) {
					rootLayout.setPadding(0, statusBarHeight, 0, 0);
				}
			} else {
				tintManager = new SystemBarTintManager(this);
				statusBarHeight = tintManager.getConfig().getStatusBarHeight();
			}
		}
	}

	@TargetApi(19)
	public void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	protected void onMenu() {
		// TODO Auto-generated method stub

	}

	protected void onBack() {
		// TODO Auto-generated method stub
		finish();
	}
	
	protected void onCommit() {
		// TODO Auto-generated method stub
		
	}
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "";
	}

	protected int getMenuRes() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public Context getContext(){
		return this;
	}
	
	public int getThemeResource(int attr) {
		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute(attr, typedValue, true);
		return typedValue.resourceId;
	}
}
