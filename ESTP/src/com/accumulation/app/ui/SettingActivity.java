package com.accumulation.app.ui;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.sociability.SociabilityClient;

public class SettingActivity extends BaseActivity {

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_setting;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "…Ë÷√";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		findViewById(R.id.logout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SociabilityClient.getClient().logout();
				Intent mIntent = new Intent(getContext(), LoginActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(mIntent);
			}
		});
		findViewById(R.id.suggest).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(getContext(), SuggestActivity.class);
				startActivity(mIntent);
			}
		});
		
		findViewById(R.id.about).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(getContext(), AboutActivity.class);
				startActivity(mIntent);
			}
		});
	}

}
