package com.zyl.push.sdk.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Utility class for download and loading external jar
 * 
 * @author Zexu
 * 
 */
public class ExtJarLoader {
	private static String TAG = "ExtJarLoader";

	public interface ExtJarLoadListener {
		public void onLoaded(DexClassLoader classLoader);
	}

	/**
	 * 
	 * @param ctx
	 *            application context
	 * @param url
	 *            external jar url
	 * @param listener
	 *            listener to be notified after jar loaded
	 */
	public static void loadExtJar(final Context ctx, final String url, final ExtJarLoadListener listener) {
		final File jarFile = new File(ctx.getDir("extJar", 0), "ext" + url.hashCode() + ".jar");
		if (jarFile.exists() && jarFile.length() > 0) {
			if (listener != null) {
				listener.onLoaded(loadJar(ctx, jarFile));
			}
		} else {
			// final Handler uiHandler = new Handler();
			new Thread() {
				public void run() {
					DexClassLoader classLoader = null;
					try {
						HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
						Log.d(TAG, "Connect to: " + conn.getURL().toString());
						conn.setConnectTimeout(5000);
						conn.connect();
						int resCode = conn.getResponseCode();
						Log.d(TAG, "Response " + resCode + " for " + conn.getURL().toString());
						if (resCode >= 200 && resCode < 300) {
							InputStream is = conn.getInputStream();
							OutputStream os = new FileOutputStream(jarFile);
							streamCopy(is, os);
							File tmp = new File(ctx.getDir("dexout", 0).getAbsolutePath() + "/classes.odex");
							if (!tmp.exists()) {
								tmp.createNewFile();
							}
//							Log.d("zhangyl", "--------"+jarFile.length());
//							DexFile dex = DexFile.loadDex(jarFile.getAbsolutePath(), tmp.getAbsolutePath(), 0);
//							Enumeration<String> e = dex.entries();
//							Log.d("zhangyl", "--------"+dex.entries().hasMoreElements());
//							while (e.hasMoreElements()) {
//								String s = e.nextElement();
//								Log.d("zhangyl", s);
//							}
//							dex.close();
							classLoader = loadJar(ctx, jarFile);
						}
					} catch (Exception e) {
						e.printStackTrace();
						jarFile.delete();
					}
					final DexClassLoader fLoader = classLoader;
					if (listener != null) {
						listener.onLoaded(fLoader);
					}
					// uiHandler.post(new Runnable() {
					// @Override
					// public void run() {
					// listener.onLoaded(fLoader);
					// }
					// });
				}
			}.start();
		}
	}

	private static DexClassLoader loadJar(Context ctx, File jarFile) {
		Log.d(TAG, "Load external jar: " + jarFile.getAbsolutePath());
		return new DexClassLoader(jarFile.getAbsolutePath(), ctx.getDir("dexout", 0).getAbsolutePath(), null,
				ctx.getClassLoader());
	}

	public static void streamCopy(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[8192];
		int count;
		while ((count = is.read(buffer)) != -1) {
			os.write(buffer, 0, count);
		}
		os.flush();
		os.close();
		is.close();
	}
}
