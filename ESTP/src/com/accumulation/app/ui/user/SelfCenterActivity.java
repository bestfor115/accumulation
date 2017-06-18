package com.accumulation.app.ui.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.ui.SettingActivity;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JDUserProfile;
import com.accumulation.lib.ui.ViewHolder;

public class SelfCenterActivity extends BaseActivity {
	private ImageView user_avatar;
	private TextView user_name;
	private TextView broadcast_count;
	private TextView attention_coount;
	private TextView follow_count;
	private TextView favorite_count;
	private TextView user_account;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_self_center;
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "设置";
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "个人中心";
	}

	@Override
	protected void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		Intent intent = new Intent(getContext(),
				SettingActivity.class);
		startActivity(intent);
		
	}
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		user_avatar = ViewHolder.get(rootLayout, R.id.user_avatar);
		user_name = ViewHolder.get(rootLayout, R.id.user_name);
		broadcast_count = ViewHolder.get(rootLayout, R.id.broadcast_count);
		attention_coount = ViewHolder.get(rootLayout, R.id.attention_coount);
		follow_count = ViewHolder.get(rootLayout, R.id.follow_count);
		user_account = ViewHolder.get(rootLayout, R.id.user_account);
		favorite_count = ViewHolder.get(rootLayout, R.id.favorite_count);
		ViewHolder.get(rootLayout, R.id.my_qrcode).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getContext(),MyQrcodeActivity.class);
				startActivity(intent);
			}
		});
		
		caculateShow();
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		loadUserData();
	}

	public void loadUserData() {
		SociabilityClient.getClient().getMyProfile(
				new BaseCallback<JDUserProfile>() {

					@Override
					public void onResultCallback(int code, String message,
							JDUserProfile result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							AccumulationAPP.getInstance().data = result.data;
							caculateShow();
						}
					}
				}, JDUserProfile.class);
	}

	private void caculateShow() {
		if(AccumulationAPP.getInstance().data==null){
			return ;
		}
		AccumulationAPP.getInstance().loadImage(
				AccumulationAPP.getInstance().data.Avatar, user_avatar);
		user_name.setText(AccumulationAPP.getInstance().data.Name + " ("
				+ AccumulationAPP.getInstance().data.Title + " )");
		broadcast_count.setText(""
				+ AccumulationAPP.getInstance().data.BroadcastNum + "");
		favorite_count
				.setText(TextUtils.isEmpty(AccumulationAPP.getInstance().data.FavoriteBroadcastsCount) ? "0"
						: AccumulationAPP.getInstance().data.FavoriteBroadcastsCount);
		attention_coount.setText(""
				+ AccumulationAPP.getInstance().data.FollowedNum);
		follow_count.setText("" + AccumulationAPP.getInstance().data.FansNum);
		user_account.setText("" + AccumulationAPP.getInstance().data.Email);

	}
}
