package com.accumulation.app.ui;

import java.io.Serializable;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.base.DialogCallback;
import com.accumulation.app.config.URIConfig;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JDLogin;
import com.accumulation.lib.tool.base.CommonUtils;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private Button loginBtn;
	private EditText nameEdit;
	private EditText pwdEdit;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		checkValidState();
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "登陆";
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "注册";
	}

	@Override
	protected void initView() {
		super.initView();
		loginBtn = (Button) findViewById(R.id.login_by_account);
		nameEdit = (EditText) findViewById(R.id.login_passport);
		pwdEdit = (EditText) findViewById(R.id.login_password);
		nameEdit.addTextChangedListener(textWatcher);
		pwdEdit.addTextChangedListener(textWatcher);
		loginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_by_account:
			loginByAccount();
			break;
		}

	}

	@Override
	protected void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		Intent intent = new Intent(this, ResigterActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			checkValidState();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	private void checkValidState() {
		if (CommonUtils.isEmpty(nameEdit.getText())
				|| CommonUtils.isEmpty(pwdEdit.getText())) {
			loginBtn.setEnabled(false);
		} else {
			loginBtn.setEnabled(true);
		}
	}

	/**
	 * 跳转到主界面
	 * */
	private void gotoMainActivity() {
		Intent intent = new Intent(this, UIActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	private void loginByAccount() {
		String t_acount = nameEdit.getText().toString();
		String t_passard = pwdEdit.getText().toString();
		if (AccumulationAPP.getInstance().isDevelopMode()
				&& "1".equals(t_acount)) {
			t_acount = "bestfor115@163.com";
			t_passard = "123456";
		}
		if (AccumulationAPP.getInstance().isDevelopMode()
				&& "2".equals(t_acount)) {
			t_acount = "bestfor114@163.com";
			t_passard = "123456";
		}
		final String acount = t_acount;
		final String passard = t_passard;
		SociabilityClient.getClient().login(acount, passard, 0,
				new DialogCallback<JDLogin>(this) {

					@Override
					public void onResultCallback(int code, String message,
							JDLogin result) {
						if (code >= 0) {
							SaveManager.getInstance(getContext()).saveLoginId(
									result.data.Id);
							SaveManager.getInstance(getContext())
									.saveUserHeader(result.data.Avatar);
							gotoMainActivity();
						} else {
							toast(message);
						}
					}
				}, JDLogin.class);
	}

}
