package com.accumulation.app.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.tool.net.data.JsonData;

public class ResigterActivity extends BaseActivity {

	EditText user_pwd, user_email;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_register;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		user_email = (EditText) findViewById(R.id.user_email);
		user_pwd = (EditText) findViewById(R.id.user_pwd);

		findViewById(R.id.save).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Logger.e("-------------------------");
				String account = user_email.getText().toString();
				String pwd = user_pwd.getText().toString();
				SociabilityClient.getClient().register(account, pwd,
						new BaseCallback<JsonData>() {

							@Override
							public void onResultCallback(int code,
									String message, JsonData result) {
								// TODO Auto-generated method stub

							}
						});
			}
		});
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "зЂВс";
	}

}
