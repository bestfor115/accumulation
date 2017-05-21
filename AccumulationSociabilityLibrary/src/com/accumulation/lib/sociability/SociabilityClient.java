package com.accumulation.lib.sociability;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import com.accumulation.lib.sociability.data.JDUpload;
import com.accumulation.lib.sociability.data.KVPair;
import com.accumulation.lib.sociability.data.MessageAble;
import com.accumulation.lib.sociability.data.Subject;
import com.accumulation.lib.sociability.data.Target;
import com.accumulation.lib.sociability.im.Messagedispatcher;
import com.accumulation.lib.sociability.util.SharePreferenceManager;
import com.accumulation.lib.sociability.util.UploadHelper;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.net.RequestSetting;
import com.accumulation.lib.tool.net.ResponseSetting;
import com.accumulation.lib.tool.net.ServiceHelper;
import com.accumulation.lib.tool.net.ServiceHelper.ResponseHandlerT;
import com.accumulation.lib.tool.net.ServiceHelper.SerializerType;
import com.accumulation.lib.tool.net.data.JsonBase;
import com.accumulation.lib.tool.net.data.JsonData;
import com.accumulation.lib.tool.net.http.RequestParams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;

public class SociabilityClient {

	public static final int LOGIN_TYPE_ACCOUNT = 0;
	public static final int LOGIN_TYPE_PHONE = 1;
	public static final int LOGIN_TYPE_DEFAULT = LOGIN_TYPE_ACCOUNT;
	public static final String BROADCAST_TYPE_ID_ALL = "All";
	public final List<KVPair> groups = new ArrayList<KVPair>();
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
	
	public void registerMessagedispatcher(Messagedispatcher dispacher){
	}

	public <T extends JsonBase> void getBroadcastByType(String type, int page,int size,
			final BaseCallback<T> callback, Class<T> c) {
		String url = config.getBroadcastListURL();
		RequestParams param = new RequestParams();
		param.put("pagesize", size + "");
		param.put("pageindex", page + 1 + "");
		param.put("type", type + "");
		doCommonRequest(param, url, callback, c);
	}

	public <T extends JsonBase> void getBroadcastById(String id,
			final BaseCallback<T> callback, Class<T> c) {
		String url = config.getBroadcastByIdURL();
		RequestParams param = new RequestParams();
		param.put("id", id);
		param.put("pagesize", 20 + "");
		param.put("pageindex", 1 + "");
		param.put("type", BROADCAST_TYPE_ID_ALL + "");
		doCommonRequest(param, url, callback, c);
	}

	public <T extends JsonBase> void getBroadcastReply(String id,
			final BaseCallback<T> callback, Class<T> c) {
		String url = config.getBroadcastReplyURL();
		RequestParams param = new RequestParams();
		param.put("id", id);
		doCommonRequest(param, url, callback, c);
	}

	public <T extends JsonBase> void deleteBroadcastReply(String id,
			final BaseCallback<T> callback, Class<T> c) {
		String url = config.deleteBroadcastReplyURL();
		RequestParams param = new RequestParams();
		param.put("id", id);
		doCommonRequest(param, url, callback, c);
	}
	public <T extends JsonBase> void deleteBroadcast(String id,
			final BaseCallback<T> callback, Class<T> c) {
		String url = config.deleteBroadcastURL();
		RequestParams param = new RequestParams();
		param.put("id", id);
		doCommonRequest(param, url, callback, c);
	}
	public <T extends JsonBase> void addArticle(String title,String content,
			final BaseCallback<T> callback, Class<T> c) {
		String url = config.addArticleURL();
		RequestParams param = new RequestParams();
		param.put("Title", title);
		param.put("Content", content);
		param.put("editorValue", content);

		doCommonRequest(param, url, callback, c);
	}
	
	public <T extends JsonBase> void addBroadcastReply(final String id, final String pid,
			final String content,final ArrayList<String> path,final BaseCallback<T> callback, final Class<T> c) {
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				final int N = path == null ? 0 : path.size();
				StringBuffer sb = new StringBuffer();
				if (N > 0) {
					String uploadURL = config.getUploadURL()
							+ "?type=image&w=240&h=240";
					GsonBuilder builder = new GsonBuilder();
					Gson gson = builder.create();
					for (int i = 0; i < N; i++) {
						String uploadResult = UploadHelper.uploadFile(context,
								uploadURL, path.get(i));
						try {
							JDUpload upload = gson.fromJson(uploadResult,
									JDUpload.class);
							if (upload != null && upload.success) {
								sb.append(upload.data.storeResult.imgId);
								if (i < N - 1) {
									sb.append(",");
								}
							} else {
								if (callback != null) {
									callback.onResultCallback(
											ResponseSetting.NET_EXCEPTION_TYPE_INVALIDL,
											"图片上传失败", null);
								}
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				String url = config.addBroadcastReplyURL();
				RequestParams param = new RequestParams();
				param.put("id", id);
				param.put("content", content);
				if (pid != null) {
					param.put("pid", pid);
				}
				if (sb.length() > 0) {
					param.put("images", sb.toString());
				}
				doCommonRequest(param, url, callback, c);
			}
		});

	}

	public <T extends JsonBase> void likeBroadcast(String id,
			final BaseCallback<T> callback, Class<T> c) {
		String url = config.likeBroadcastURL();
		RequestParams param = new RequestParams();
		param.put("id", id);
		doCommonRequest(param, url, callback, c);
	}

	public <T extends JsonBase> void favoriteBroadcast(String id,
			boolean favorite, final BaseCallback<T> callback, Class<T> c) {
		String url = favorite ? config.favoriteBroadcastReplyURL() : config
				.cancelFavoriteBroadcastURL();
		RequestParams param = new RequestParams();
		param.put("id", id);
		doCommonRequest(param, url, callback, c);
	}

	public <T extends JsonBase> void addBroadcast(final String content,
			final String orignalId, final String groupId,
			final ArrayList<String> path, final ArrayList<Subject> subjects,
			final ArrayList<Target> targets, final boolean toAll,
			final boolean toCompany, final BaseCallback<T> callback,
			final Class<T> c) {
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final int N = path == null ? 0 : path.size();
				StringBuffer sb = new StringBuffer();
				if (N > 0) {
					String uploadURL = config.getUploadURL()
							+ "?type=image&w=240&h=240";
					GsonBuilder builder = new GsonBuilder();
					Gson gson = builder.create();
					for (int i = 0; i < N; i++) {
						String uploadResult = UploadHelper.uploadFile(context,
								uploadURL, path.get(i));
						try {
							JDUpload upload = gson.fromJson(uploadResult,
									JDUpload.class);
							if (upload != null && upload.success) {
								sb.append(upload.data.storeResult.imgId);
								if (i < N - 1) {
									sb.append(",");
								}
							} else {
								if (callback != null) {
									callback.onResultCallback(
											ResponseSetting.NET_EXCEPTION_TYPE_INVALIDL,
											"图片上传失败", null);
								}
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				RequestParams param = new RequestParams();
				if (groupId != null) {
					param.put("sendto", groupId);
				}
				String input = content;
				int length = subjects.size();
				for (int i = 0; i < length; i++) {
					input += "#(" + subjects.get(i).Name + ") ";
				}
				param.put("content", input);
				if (sb.length() > 0) {
					param.put("images", sb.toString());
				}
				String url = null;
				if (orignalId != null) {
					url = config.resendBroadcastURL();
					param.put("originalId", orignalId);
				} else {
					url = config.sendBroadcastURL();
				}
				param.put("toCompany", toCompany + "");
				param.put("toAll", toAll + "");
				doCommonRequest(param, url, callback, c);
			}
		});
	}

	@SuppressLint("NewApi")
	public <T extends JsonBase> void login(String account, String passard,
			int type, final BaseCallback<T> callback, Class<T> c) {
		String url = config.getLoginURL();
		RequestParams param = new RequestParams();
		param.put("Email", account);
		param.put("password", passard);
		param.put("type", BROADCAST_TYPE_ID_ALL + "");
		doCommonRequest(param, url, callback, c, new RequestSuccessListener() {
			@Override
			public void onRequestSuccess() {
				saveLoginCookie(ServiceHelper.getHelper()
						.getDefaultHttpClient());
			}
		});
	}

	public <T extends JsonBase> void getMyProfile(
			final BaseCallback<T> callback, Class<T> c) {
		doCommonRequest(config.getMyProfileURL(), callback, c);
	}
	
	public <T extends JsonBase> void getAllSubject(
			final BaseCallback<T> callback, Class<T> c) {
		doCommonRequest(config.getAllSubjectURL(), callback, c);
	}
	
	public <T extends JsonBase> void getBroadcastTarget(
			final BaseCallback<T> callback, Class<T> c) {
		doCommonRequest(config.getBroadcastTargetURL(), callback, c);
	}
	
	public <T extends JsonBase> void getUserProfile(String id,String email,
			final BaseCallback<T> callback, Class<T> c) {
		String url=config.getUserProfileByEmailURL();
		RequestParams param = new RequestParams();
		if(!CommonUtils.isEmpty(id)){
			url=config.getUserProfileURL();
			param.put("id", id);
		}else{
			param.put("email", email);
		}
		doCommonRequest(param,url, callback, c);
	}
	public <T extends JsonBase> void getRecentChat(
			final BaseCallback<T> callback, Class<T> c) {
		String url=config.getRecentChatURL();
		RequestParams param = new RequestParams();
		param.put("type", "Message");
		param.put("pagesize", "200");
		param.put("pageIndex", "1");
		doCommonRequest(param,url, callback, c);
	}
	public <T extends JsonBase> void sendMessage(final MessageAble message,final String file,
			final BaseCallback<T> callback, final Class<T> c) {
		threadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final int N = CommonUtils.isEmpty(file)?0:1;
				StringBuffer sb = new StringBuffer();
				if (N > 0) {
					String uploadURL = config.getUploadURL()
							+ "?type=image&w=240&h=240";
					GsonBuilder builder = new GsonBuilder();
					Gson gson = builder.create();
					for (int i = 0; i < N; i++) {
						String uploadResult = UploadHelper.uploadFile(context,
								uploadURL, file);
						try {
							JDUpload upload = gson.fromJson(uploadResult,
									JDUpload.class);
							if (upload != null && upload.success) {
								sb.append(upload.data.storeResult.imgId);
								if (i < N - 1) {
									sb.append(",");
								}
							} else {
								if (callback != null) {
									callback.onResultCallback(
											ResponseSetting.NET_EXCEPTION_TYPE_INVALIDL,
											"图片上传失败", null);
								}
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				String url=config.sendMessageURL();
				RequestParams param = new RequestParams();
				param.put("UsersId", message.getReciver());
				param.put("Content", message.getContent());
				if (sb.length() > 0) {
					param.put("images", sb.toString());
				}
				doCommonRequest(param,url, callback, c);
			}
		});

	}
	
	public <T extends JsonBase> void getMessage(String talkId,int currentPage,int pageSize,
			final BaseCallback<T> callback, Class<T> c) {
		String url=config.getMessageURL();
		RequestParams param = new RequestParams();
		param.put("targetUserId", talkId);
		param.put("pagesize", pageSize+"");
		param.put("pageIndex", currentPage+1+"");
		doCommonRequest(param,url, callback, c);
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
		return !CommonUtils
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

	public void register(String account, String passard, BaseCallback callback) {
		String url = config.getRegisterURL();
		RequestParams param = new RequestParams();
		param.put("Email", account);
		param.put("Password", passard);
		param.put("ConfirmPassword", passard);
		param.put("Name", "");
		param.put("Mobile", "");

		doCommonRequest(param, url, callback, JsonData.class, new RequestSuccessListener() {
			@Override
			public void onRequestSuccess() {
				saveLoginCookie(ServiceHelper.getHelper()
						.getDefaultHttpClient());
			}
		});
	}

	@SuppressLint("NewApi")
	public void logout() {
		try {
			RequestSetting.Builder requestBuilder = (RequestSetting.Builder) config
					.getRequestBuilderClass().newInstance();
			ResponseSetting.Builder<JsonBase> responseBuilder = (ResponseSetting.Builder<JsonBase>) config
					.getResponseBuilderClass(JsonBase.class).newInstance();
			final ServiceHelper helper = ServiceHelper.getHelper();
			requestBuilder.setURL(config.getLoginURL());
			requestBuilder.setGETMethod(false);
			responseBuilder.setSerializerType(SerializerType.JSON);
			responseBuilder.setResponseResultHandlerT(
					new ResponseHandlerT<JsonBase>() {
						@Override
						public void onResponse(boolean success, JsonBase result) {
							if (success && result.isSuccess()) {
								clearLoginCookie();
							}
						}
					}, JsonBase.class);
			helper.callServiceAsync(context, requestBuilder.create(),
					responseBuilder.create());
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void getSelfProfile(Class c) {

	}

	private void loadGroups() {
		groups.clear();
		KVPair company = new KVPair();
		company.Id = "Collegue";
		company.Name = "单位";
		KVPair squre = new KVPair();
		squre.Id = "Square";
		squre.Name = "广场";
		KVPair favorite = new KVPair();
		favorite.Id = "Favorite";
		favorite.Name = "收藏";
		KVPair all = new KVPair();
		all.Id = "All";
		all.Name = "全部";

		groups.add(new KVPair("AtMe", "@我的"));
		groups.add(new KVPair("Myself", "我的"));
		groups.add(favorite);
		groups.add(company);
		groups.add(squre);
		groups.add(all);
	}

	public interface RequestSuccessListener {
		public void onRequestSuccess();
	}
}
