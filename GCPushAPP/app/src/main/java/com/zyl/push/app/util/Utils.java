package com.zyl.push.app.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

public class Utils {
	/**
	 * 
	 * @param message
	 *            auth-token+��$��+userid
	 * @param rawkey
	 *            private key (Base64 encoded)
	 * @param rawiv
	 *            TerminalID
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String message, String rawkey) throws Exception {

		final SecretKey key = new SecretKeySpec(Base64.decode(rawkey, Base64.DEFAULT), "DESede");
		final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		final byte[] plainTextBytes = message.getBytes();
		final byte[] cipherText = cipher.doFinal(plainTextBytes);

		return new String(Base64.encode(cipherText, Base64.DEFAULT));
	}

	public static String decrypt(String message, String rawkey) throws Exception {

		final SecretKey key = new SecretKeySpec(Base64.decode(rawkey, Base64.DEFAULT), "DESede");
		final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		final byte[] plainTextBytes = message.getBytes();
		final byte[] cipherText = cipher.doFinal(plainTextBytes);

		return new String(Base64.encode(cipherText, Base64.DEFAULT));
	}
	
	public static String loadAssetText(Context ctx, String fileName) {
		try {
			InputStream is = ctx.getAssets().open(fileName);
			String content = new String(IS2ByteArray(is));
			is.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] IS2ByteArray(InputStream is) throws IOException {
		byte[] buf = new byte[8192];
		int len;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		while ((len = is.read(buf)) != -1) {
			bao.write(buf, 0, len);
		}
		byte[] result = bao.toByteArray();
		bao.close();
		return result;
	}

	public static boolean saveSharedPreferencesToFile(SharedPreferences pref, File dst) {
		boolean res = false;
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(dst));

			output.writeObject(pref.getAll());

			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

	@SuppressWarnings({ "unchecked" })
	public static boolean loadSharedPreferencesFromFile(SharedPreferences pref, File src) {
		boolean res = false;
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(src));
			Editor prefEdit = pref.edit();
			prefEdit.clear();
			Map<String, ?> entries = (Map<String, ?>) input.readObject();
			for (Entry<String, ?> entry : entries.entrySet()) {
				Object v = entry.getValue();
				String key = entry.getKey();

				if (v instanceof Boolean)
					prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
				else if (v instanceof Float)
					prefEdit.putFloat(key, ((Float) v).floatValue());
				else if (v instanceof Integer)
					prefEdit.putInt(key, ((Integer) v).intValue());
				else if (v instanceof Long)
					prefEdit.putLong(key, ((Long) v).longValue());
				else if (v instanceof String)
					prefEdit.putString(key, ((String) v));
			}
			prefEdit.commit();
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

	public static String getSystemPropertie(String key) {
		try {
			Class<?> clazz = Class.forName("android.os.SystemProperties");
			Method get = clazz.getMethod("get", String.class);
			return (String) get.invoke(null, key);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String getTVId() {
		return getSystemPropertie("ro.yunos.product.tvid");
	}
	
	public static String getUUID() {
		return getSystemPropertie("ro.aliyun.clouduuid");
	}
}
