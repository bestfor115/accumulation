package com.accumulation.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.im.IMManager;

public class LauncherActivity extends BaseActivity implements OnClickListener{

	public final int MSG_LOAD_FINISH = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOAD_FINISH:
				caculateNextActivity();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// �����ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		handler.removeMessages(MSG_LOAD_FINISH);
		handler.sendEmptyMessageDelayed(MSG_LOAD_FINISH, 3000);
		IMManager.getManager().checkUserState(SaveManager.getInstance(this).getLoginId(), "123456");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		handler.removeMessages(MSG_LOAD_FINISH);
	}
	
	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_launcher;
	}
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		findViewById(R.id.skip).setOnClickListener(this);
	}
	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}
	/**
	 * ��ת��������
	 * */
	private void gotoMainActivity(){
		handler.removeMessages(MSG_LOAD_FINISH);
		Intent intent=new Intent(this,UIActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	/**
	 * ��ת��������
	 * */
	private void gotoLoginActivity(){
		handler.removeMessages(MSG_LOAD_FINISH);
		Intent intent=new Intent(this,LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	/**
	 * ��ת��������
	 * */
	private void gotoGuideActivity(){
		handler.removeMessages(MSG_LOAD_FINISH);
		Intent intent=new Intent(this,GuideActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		caculateNextActivity();
	}
	
	public void caculateNextActivity(){
		if(SaveManager.getInstance(this).needGuide()){
			gotoGuideActivity();
		}else{
			if(SociabilityClient.getClient().isValidCookie()){
				gotoMainActivity();
			}else{
				gotoLoginActivity();
			}
		}
	}

}
