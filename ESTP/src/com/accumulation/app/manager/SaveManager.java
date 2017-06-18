package com.accumulation.app.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.accumulation.app.R;

public class SaveManager {
	public static final String CONFIG = "config";

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private static SaveManager save;

	private final String KEY_FOR_LOGIN_ID = "login_id";
	private final String KEY_FOR_APP_THEME = "app_theme";
	private final String KEY_FOR_USER_HEADER = "user_header";
	private final String KEY_FOR_OFFLINE_BROADCAST = "offline_broadcast";
	private final String KEY_FOR_NEED_GUIDE = "need_guide";
	private final String KEY_FOR_QRCODE = "qr_code";

	public static SaveManager getInstance(Context mContext) {
		if (save == null) {
			save = new SaveManager(mContext, CONFIG);
		}
		return save;
	}

	private SaveManager(Context mContext, String name) {
		sp = mContext.getSharedPreferences(name, Context.MODE_WORLD_READABLE
				+ Context.MODE_WORLD_WRITEABLE);
		editor = sp.edit();
	}

	public String getLoginId() {
		return getValueString(KEY_FOR_LOGIN_ID);
	}


	public void saveLoginId(String id) {
		putValueString(KEY_FOR_LOGIN_ID, id);
		saveData();
	}
	public String getSaveHeader() {
		return getValueString(KEY_FOR_USER_HEADER);
	}

	public void saveUserHeader(String id) {
		putValueString(KEY_FOR_USER_HEADER, id);
		saveData();
	}
	

	public boolean isSelf(String id) {
		String lid = getValueString(KEY_FOR_LOGIN_ID);
		if (id == null || lid == null) {
			return false;
		} else {
			return lid.equals(id);
		}
	}

	public boolean needGuide() {
		return false;
//		return !getValueBoolean(KEY_FOR_NEED_GUIDE);
	}

	public void setDidGuide(boolean flag) {
		editor.putBoolean(KEY_FOR_NEED_GUIDE, flag);
		saveData();
	}

	public void putValueString(String key, String value) {
		editor.putString(key, value);
	}

	public String getValueString(String key) {
		return sp.getString(key, null);
	}

	public String getValueString(String key, String def) {
		return sp.getString(key, def);
	}

	public void putValueInt(String key, int value) {
		editor.putInt(key, value);
	}

	public int getValueInt(String key) {
		return sp.getInt(key, 0);
	}

	public int getValueInt(String key, int def) {
		return sp.getInt(key, def);
	}

	public void remove(String key) {
		editor.remove(key);
	}

	public void saveData() {
		editor.commit();
	}

	public SharedPreferences getSharedPreferences() {
		return sp;
	}

	public void putValueFloat(String string, float value) {
		editor.putFloat(string, value);
	}

	public float getValueFloat(String key) {
		return sp.getFloat(key, 0);
	}

	public void putValueBoolean(String string, boolean value) {
		editor.putBoolean(string, value);
	}

	public boolean getValueBoolean(String key) {
		return sp.getBoolean(key, false);
	}

	public void putValueLong(String string, long value) {
		editor.putLong(string, value);
	}

	public long getValueLong(String key) {
		return sp.getLong(key, 0);
	}

	public int getAppTheme() {
		String value = getValueString(KEY_FOR_APP_THEME, "1");
		switch (Integer.valueOf(value)) {
		case 1:
			return R.style.AppTheme_day;
		case 2:
			return R.style.AppTheme_night;
		default:
			return R.style.AppTheme_day;
		}
	}

	public boolean isNightTheme() {
		String value = getValueString(KEY_FOR_APP_THEME, "1");
		return "2".equals(value);
	}

	public void triggerTheme() {
		String value = getValueString(KEY_FOR_APP_THEME, "1");
		String set = "1".equals(value) ? "2" : "1";
		putValueString(KEY_FOR_APP_THEME, set);
		saveData();
	}

	public void saveQRCodeIndex(int index) {
		putValueInt(KEY_FOR_QRCODE, index);
		saveData();
	}

	public int getSaveQRCodeIndex() {
		return getValueInt(KEY_FOR_QRCODE, 0);
	}

}
