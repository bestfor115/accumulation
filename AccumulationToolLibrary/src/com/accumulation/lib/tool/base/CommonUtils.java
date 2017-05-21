package com.accumulation.lib.tool.base;

import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class CommonUtils {

	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	private static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/** 检查SD卡是否存在 */
	public static boolean checkSdCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static boolean isEmpty(CharSequence input) {
		return TextUtils.isEmpty(input) || "".equals(input.toString().trim());
	}

	public static boolean isEmpty(String input) {
		return TextUtils.isEmpty(input) || "".equals(input.trim());
	}

	// 判断，返回布尔值
	public static boolean isPhoneNumber(CharSequence input) {
		String regex = "1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";
		return Pattern.matches(regex, input);
	}

	public static String getWordSpell(String word) {
		String spell = CharacterParser.getInstance().getSelling(word);
		String result = spell.substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (result.matches("[A-Z]")) {
			return result;
		} else {
			return "#";
		}
	}
}
