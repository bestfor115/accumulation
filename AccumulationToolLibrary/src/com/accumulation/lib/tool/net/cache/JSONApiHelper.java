package com.accumulation.lib.tool.net.cache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;

import com.accumulation.lib.tool.base.HttpUtils;
import com.accumulation.lib.tool.base.IOUtils;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.tool.net.http.AsyncHttpClient;
import com.accumulation.lib.tool.net.http.RequestParams;

public class JSONApiHelper {
	public static enum CallbackType {
		/**
		 * 有缓存时,先回调缓存数据, 如果数据有更新会再次回调
		 */
		CacheFirst,
		/**
		 * 网络调用成功时,返回新数据, 如果没有网络或网络请求失败, 返回缓存数据
		 */
		ForceUpdate,
		/**
		 * 只有网络返回的数据和缓存里的不同时才会回调
		 */
		CallbackOnChange,
		/**
		 * 网络调用成功时,返回新数据
		 */
		NoCache
	}

	/**
	 * API 数据回调
	 * 
	 * @author Zexu
	 * 
	 */
	public interface StringResponseListener {
		public void onResponse(String content);
	}

	public static AsyncHttpClient mClient = new AsyncHttpClient();

	/**
	 * 这个方法必须在UI线程调用, 实际的网络操作和DB操作会在新的线程里执行. 回调接口在UI线程运行
	 * 
	 * @param context
	 * @param callbackType
	 *            数据回调方式
	 * @param url
	 * @param params
	 *            url的额外参数, 可以为空
	 * @param listener
	 *            回调接口
	 */
	public static void callJSONAPI(final Context context, final CallbackType callbackType,
			final String url, final RequestParams params, final StringResponseListener listener) {
		final Handler uiHandler = new Handler();
		new Thread() {
			public void run() {
				if (url != null && url.startsWith("asset://")) {
					String path = Uri.parse(url).getEncodedPath();
					if (path.startsWith("/"))
						path = path.substring(1);
					postResult(getAssetContent(context, path));
					return;
				}

				String result = null;
				final JSONCache cache = new JSONCache(context);
				final String key = AsyncHttpClient.getUrlWithQueryString(url, params);
				// boolean online = isOnline(context);
				final String cacheData = cache.getCacheData(key);
				if (callbackType == CallbackType.CacheFirst) {
					if (listener != null) {
						postResult(cacheData);
					}
				}

				try {
					URL url = new URL(key);
					//do a dns lookup first
					Logger.d("dns lookup for: " + url.getHost());
					if (!HttpUtils.checkDNSResolve(url.getHost(), 3000)) {
						throw new UnknownHostException("Faile to resolve " + url.getHost()
								+ " in 3 seconds");
					}
					Logger.d("connect to: " + key);
					URLConnection connection = url.openConnection();
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(30000);
					
					InputStream inputStream = connection.getInputStream();
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					String charset = HttpUtils.getCharSet(connection);
					Logger.d("charset = "+charset);
					IOUtils.streamCopy(inputStream, bao);
					result = new String(bao.toByteArray(), charset);
//					result = mClient.syncGet(url, params);
//					if (result == null)
//						throw new IOException("Result is null");

					if (callbackType == CallbackType.CallbackOnChange) {
						if (cacheData != null && cacheData.equals(result))
							return;
					}
					postResult(result);
					cache.addCacheData(key, result);

				} catch (Exception e) {
					Logger.e(e.getMessage(), e);
					e.printStackTrace();
					postResult(callbackType == CallbackType.ForceUpdate ? cacheData : null);
				}
			}

			void postResult(final String result) {
				if (listener == null)
					return;
				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						try {
							listener.onResponse(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});
			}
		}.start();

	}
	
	public static String syncCallJSONAPI(final Context context,
			final String url, final RequestParams params) {
		if (url != null && url.startsWith("asset://")) {
			String path = Uri.parse(url).getEncodedPath();
			if (path.startsWith("/"))
				path = path.substring(1);
			return getAssetContent(context, path);
		}

		String result = null;
		final JSONCache cache = new JSONCache(context);
		final String key = AsyncHttpClient.getUrlWithQueryString(url, params);
		Logger.d(key);
		// boolean online = isOnline(context);
		String cacheData = cache.getCacheData(key);

		try {
			result = mClient.syncGet(url, params);
			if (result == null)
				throw new IOException("Result is null");

			cacheData = result;
			cache.addCacheData(key, result);

		} catch (Exception e) {
			Logger.e(e.getMessage(), e);
		}
		return cacheData;
	}

	public static String getAssetContent(Context ctx, String filename) {
		try {
			InputStream is = ctx.getAssets().open(filename);
			String content = new String(IS2ByteArray(is));
			is.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] IS2ByteArray(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buf = new byte[8192];
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		int len;
		while ((len = bis.read(buf)) != -1) {
			bao.write(buf, 0, len);
		}
		return bao.toByteArray();
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
