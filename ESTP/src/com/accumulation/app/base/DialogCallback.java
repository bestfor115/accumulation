package com.accumulation.app.base;

import android.app.Dialog;
import android.content.Context;

import com.accumulation.app.util.UIUtils;
import com.accumulation.lib.sociability.BaseCallback;

public abstract class DialogCallback<T> extends BaseCallback<T> {

	private Dialog dialog;

	public DialogCallback(Context context) {
		dialog = UIUtils.createLoadingDialog(context, true);
	}

	@Override
	public void onRequestEnd() {
		// TODO Auto-generated method stub
		super.onRequestEnd();
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@Override
	public void onRequestStart() {
		// TODO Auto-generated method stub
		super.onRequestStart();
		dialog.show();
	}

}
