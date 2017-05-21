package com.accumulation.lib.utility.base;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;

/**
 * 剪切板工具类
 * */
public class ClipboardUtils {
	public final static String CLIPBOARD_TAG="default_clipboard";
    /**
     * 将内容复制到剪切板
     *@param context 上下文
     * @param clipContent 剪切内容
     * @return none
     */
	public static void copyToClipboard(Context context,String clipContent) {
		if (Build.VERSION.SDK_INT > 11) {
			android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setPrimaryClip(ClipData.newPlainText(CLIPBOARD_TAG, clipContent));
		} else {
			android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setText(clipContent);
		}
	}
}
