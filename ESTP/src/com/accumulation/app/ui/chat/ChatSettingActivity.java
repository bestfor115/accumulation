package com.accumulation.app.ui.chat;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.ui.user.UserCardActivity;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JDUserProfile;
import com.accumulation.lib.sociability.data.UserProfile;
import com.accumulation.lib.ui.ViewHolder;

public class ChatSettingActivity extends BaseActivity {
	private UserProfile data;
	private String talkId;
	private TextView user_name;
	private TextView user_dsn;
	private ImageView user_header;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_chat_setting;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "ª·ª∞…Ë÷√";
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		talkId = intent.getStringExtra("target_id");
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		loadUserProfile();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		ViewHolder.get(rootLayout, R.id.user_home).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getContext(),
								UserCardActivity.class);
						intent.putExtra("id", talkId);
						startActivity(intent);
					}
				});
		user_name=ViewHolder.get(rootLayout, R.id.user_name);
		user_dsn=ViewHolder.get(rootLayout, R.id.user_dsn);
		user_header=ViewHolder.get(rootLayout, R.id.user_header);

	}

	public void loadUserProfile() {
		SociabilityClient.getClient().getUserProfile(talkId, null,
				new BaseCallback<JDUserProfile>() {

					@Override
					public void onResultCallback(int code, String message,
							JDUserProfile result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							data = result.data;
							user_name.setText(data.Name);
							user_dsn.setText(data.Intro);
							AccumulationAPP.getInstance().loadImage(data.Avatar, user_header);
						}
					}
				}, JDUserProfile.class);
	}
}
