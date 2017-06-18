package com.accumulation.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.ui.fragment.BroadcastFragment;
import com.accumulation.app.ui.fragment.DiscoveryFragment;
import com.accumulation.app.ui.fragment.MessageFragmnet;
import com.accumulation.app.ui.fragment.SelfFragment;
import com.accumulation.app.upgrade.UpgradeManager;
import com.accumulation.lib.sociability.im.IMManager;

/**
 * 界面：主界面
 * */
public class MainActivity extends BaseActivity {

	public static final int FRAGMENT_INDEX_HOME = 0;
	public static final int FRAGMENT_INDEX_MESSAGE = 1;
	public static final int FRAGMENT_INDEX_DISCOVERY = 2;
	public static final int FRAGMENT_INDEX_SELFT = 3;
	private int currentFragmentIndex = -1;
	private long mExitTime;
	private static boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		UpgradeManager.getInstance().checkUpgrade(true, this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_main;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		decorateMenu(R.id.home_menu, FRAGMENT_INDEX_HOME);
		decorateMenu(R.id.message_menu, FRAGMENT_INDEX_MESSAGE);
		decorateMenu(R.id.discovery_menu, FRAGMENT_INDEX_DISCOVERY);
		decorateMenu(R.id.self_menu, FRAGMENT_INDEX_SELFT);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				toast("再按一次退出程序 ");
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void decorateMenu(int id, final int index) {
		View view = findViewById(id);
		if (view != null) {
			view.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						switchFragment(index);
					}
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switchFragment(index);
				}
			});
		}
	}

	private void switchFragment(int index) {
		switchFragment(index, null);
	}

	private void switchFragment(int index, Object o) {
		if (o == null && currentFragmentIndex == index) {
			return;
		}
		currentFragmentIndex = index;
		Fragment f = null;
		switch (index) {
		case FRAGMENT_INDEX_DISCOVERY:
			f = new DiscoveryFragment();
			break;
		case FRAGMENT_INDEX_HOME:
			f = new BroadcastFragment();
			break;
		case FRAGMENT_INDEX_MESSAGE:
			f = new MessageFragmnet();
			break;
		case FRAGMENT_INDEX_SELFT:
			f = new SelfFragment();
			break;
		}
		if (f != null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, f).commit();
		}
	}

}
