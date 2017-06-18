package com.accumulation.app.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;

public class ClipboardUtil {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public static void copyToClipboard(Context context,String s) {
		if (android.os.Build.VERSION.SDK_INT > 11) {
			android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setPrimaryClip(ClipData.newPlainText("label", s));
		} else {
			android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setText(s);
		}
		UIUtils.showTast(context, "“—∏¥÷∆µΩºÙ«–∞Â");
	}
}
