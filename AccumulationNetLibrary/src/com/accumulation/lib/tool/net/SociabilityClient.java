package com.accumulation.lib.tool.net;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import com.accumulation.lib.tool.common.MatcherUtils;
import com.accumulation.lib.tool.net.base.BaseCallback;
import com.accumulation.lib.tool.net.base.RequestSetting;
import com.accumulation.lib.tool.net.base.ResponseSetting;
import com.accumulation.lib.tool.net.base.ServiceHelper;
import com.accumulation.lib.tool.net.base.ServiceHelper.ResponseHandlerT;
import com.accumulation.lib.tool.net.base.ServiceHelper.SerializerType;
import com.accumulation.lib.tool.net.data.JsonBase;
import com.accumulation.lib.tool.net.http.RequestParams;
import com.accumulation.lib.tool.net.util.SharePreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;

public class SociabilityClient {

	final static ExecutorService threadPool = Executors
			.newSingleThreadExecutor();

	Context context;
	SociabilityConfig config;

	private static final SociabilityClient instance = new SociabilityClient();

	public static SociabilityClient getClient() {
		return instance;
	}

	private SociabilityClient() {

	}

	public void init(Context context, SociabilityConfig config) {
		this.context = context.getApplicationContext();
		this.config = config;
		SharePreferenceManager.init(context, "user_data");
	}
	
	public String getLoginCookie() {
		CookieManager cookieManager = CookieManager.getInstance();
		String cookie = cookieManager.getCookie(config.getDomain()) + "";
		if (!cookie.endsWith(";")) {
			cookie += ";";
		}
		return cookie;
	}

	public boolean isValidCookie() {
		CookieManager cookieManager = CookieManager.getInstance();
		return !MatcherUtils
				.isEmpty(cookieManager.getCookie(config.getDomain()));
	}

	public void clearLoginCookie() {
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setCookie(config.getDomain(), "");
	}

	public void saveLoginCookie(DefaultHttpClient httpClient) {
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		int N = cookies.size();
		String tag = "ProviderMvcSession";
		for (int i = 0; i < N; i++) {
			Cookie cookie = cookies.get(i);
			String cookieName = cookie.getName();
			String cookieValue = cookie.getValue();
			if (!TextUtils.isEmpty(cookieName)
					&& !TextUtils.isEmpty(cookieValue)) {
				if (tag.equals(cookieName)) {
					String save = cookieName + "=" + cookieValue + ";";
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.setCookie(config.getDomain(), save);
				}
			}
		}
	}

	public boolean handleCommonResult(boolean success, JsonBase result,
			BaseCallback callback) {
		boolean valid = false;

		if (!success) {
			if (callback != null) {
				callback.onResultCallback(
						ResponseSetting.NET_EXCEPTION_TYPE_FAIL,
						ResponseSetting.DATA_EXCEPTION_MESSAGE, null);
			}
		} else {
			if (result == null) {
				if (callback != null) {
					callback.onResultCallback(
							ResponseSetting.NET_EXCEPTION_TYPE_INVALIDL,
							ResponseSetting.DATA_EXCEPTION_MESSAGE, null);
				}
			} else {
				if (!result.isSuccess()) {
					if (callback != null) {
						callback.onResultCallback(
								ResponseSetting.NET_EXCEPTION_TYPE_BUSINESS,
								result.getMessage(), null);
					}
				} else {
					valid = true;
				}
			}

		}
		return valid;
	}

	public <T extends JsonBase> void doCommonRequest(String url,
			final BaseCallback<T> callback, Class<T> c,
			final RequestSuccessListener extra) {
		doCommonRequest(null, url, callback, c, extra);
	}

	public <T extends JsonBase> void doCommonRequest(String url,
			final BaseCallback<T> callback, Class<T> c) {
		doCommonRequest(null, url, callback, c, null);
	}

	public <T extends JsonBase> void doCommonRequest(RequestParams param,
			String url, final BaseCallback<T> callback, Class<T> c) {
		doCommonRequest(param, url, callback, c, null);
	}

	@SuppressLint("NewApi")
	public <T extends JsonBase> void doCommonRequest(RequestParams param,
			String url, final BaseCallback<T> callback, Class<T> c,
			final RequestSuccessListener extra) {
		try {
			RequestSetting.Builder requestBuilder = (RequestSetting.Builder) config
					.getRequestBuilderClass().newInstance();
			ResponseSetting.Builder<T> responseBuilder = (ResponseSetting.Builder<T>) config
					.getResponseBuilderClass(c).newInstance();
			final ServiceHelper helper = ServiceHelper.getHelper();
			requestBuilder.setURL(url);
			requestBuilder.setGETMethod(false);
			if (param != null) {
				requestBuilder.setParams(param);
			}
			responseBuilder.setSerializerType(SerializerType.JSON);
			responseBuilder.setResponseResultHandlerT(
					new ResponseHandlerT<T>() {
						@Override
						public void onResponse(boolean success, T result) {
							if (callback != null) {
								callback.onRequestEnd();
							}
							if (handleCommonResult(success, result, callback)) {
								if (extra != null) {
									extra.onRequestSuccess();
								}
								saveLoginCookie(helper.getDefaultHttpClient());
								if (callback != null) {
									callback.onResultCallback(
											ResponseSetting.NET_EXCEPTION_TYPE_NONE,
											result.getMessage(), result);
								}
							}
						}
					}, c);
			helper.callServiceAsync(context, requestBuilder.create(),
					responseBuilder.create());
			if (callback != null) {
				callback.onRequestStart();
			}
		}catch (InstantiationException | IllegalAccessException  e) {
			e.printStackTrace();
			if (callback != null) {
				callback.onResultCallback(
						ResponseSetting.NET_EXCEPTION_TYPE_FAIL,
						ResponseSetting.NETWORK_EXCEPTION_MESSAGE, null);
			}
		}
	}


	public interface RequestSuccessListener {
		public void onRequestSuccess();
	}
}
