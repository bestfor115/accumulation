package com.accumulation.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.TypedValue;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;

import com.accumulation.app.config.Config;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.app.ui.chat.UserChatActivity;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.Broadcast;
import com.accumulation.lib.sociability.data.Broadcast.Group;
import com.accumulation.lib.sociability.data.Face;
import com.accumulation.lib.sociability.data.FaceHolder;
import com.accumulation.lib.sociability.data.Image;
import com.accumulation.lib.sociability.data.JDBroadcast;
import com.accumulation.lib.sociability.data.JDUserProfile;
import com.accumulation.lib.sociability.data.MessageItem;
import com.accumulation.lib.sociability.data.Subject;
import com.accumulation.lib.sociability.data.Target;
import com.accumulation.lib.sociability.data.UserProfile;
import com.accumulation.lib.sociability.im.IMManager;
import com.accumulation.lib.sociability.im.MessageReceiver;
import com.accumulation.lib.sociability.im.Messagedispatcher;
import com.accumulation.lib.tool.base.BaseApplication;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.tool.net.data.JsonData;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class AccumulationAPP extends BaseApplication implements
		Messagedispatcher {
	public final List<Broadcast> commitingList = new ArrayList<Broadcast>();
	public final List<FaceHolder> faces = new ArrayList<FaceHolder>();
	public Handler handler = new Handler();
	public boolean isSyncBroadcast;
	public UserProfile data;
	public int width;
	public int height;
	private static AccumulationAPP instance;
	private Activity activity;
    public LocationClient locationClient;

	public static AccumulationAPP getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		Logger.TAG = "ESTP";
		startlDevelopMode();
		initFaces();
		CookieSyncManager.createInstance(this);
		SociabilityClient.getClient().init(this, new ESTPConfig());
		IMManager.getManager().init(this);
		IMManager.getManager().registerMessagedispatcher(this);
		initImageLoader(this);
		width = this.getResources().getDisplayMetrics().widthPixels;
		height = this.getResources().getDisplayMetrics().heightPixels;
        locationClient = new LocationClient(this.getApplicationContext());
		SDKInitializer.initialize(this);

	}

	public void setCurrentActivity(Activity activity) {
		this.activity = activity;
	}

	public void loadImage(String url, ImageView view) {
		ImageLoader.getInstance().displayImage(url, view);
	}

	@Override
	protected String getLogSavePath() {
		// TODO Auto-generated method stub
		return null;
	}

	/** 锟斤拷始锟斤拷ImageLoader */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				Config.CACHE_FILE_PATH);// 锟斤拷取锟斤拷锟斤拷锟斤拷锟侥柯硷拷锟街�

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.discCache(new UnlimitedDiscCache(cacheDir))// 锟皆讹拷锟藉缓锟斤拷路锟斤拷
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 锟斤拷锟捷憋拷锟斤拷锟斤拷锟斤拷锟斤拷息锟斤拷取图片锟斤拷源
	 * */
	public Drawable getBitmapByTitle(String title) {
		int N = faces.size();
		for (int i = 0; i < N; i++) {
			if (title.equals(faces.get(i).face.getTitle())) {
				AssetManager assetManager = this.getAssets();
				try {
					return new GifDrawable(assetManager, faces.get(i).face.path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return getResources().getDrawable(R.drawable.unknown_face);
	}

	/**
	 * 锟斤拷始锟斤拷锟斤拷锟斤拷
	 * */
	private void initFaces() {
		int N = Config.FACE_CONFIG.length;
		int i = 0;
		while (i < N) {
			FaceHolder holder = new FaceHolder();
			holder.index = i / 2;
			holder.face = new Face(Config.FACE_CONFIG[i++],
					Config.FACE_CONFIG[i++]);
			faces.add(holder);
		}
	}

	public boolean isNightTheme() {
		return SaveManager.getInstance(this).isNightTheme();
	}

	public void test() {
		String json = "{\"success\":true,\"msg\":\"锟斤拷锟斤拷锟缴癸拷\",\"isAuthenticated\":true,\"data\":null}";
		Gson gson = new Gson();
		JsonData result = gson.fromJson(json, JsonData.class);

	}

	public void requestAddBroadcast(final String content,
			final String orignalId, final String groupId,
			final ArrayList<ChooseFile> path,
			final ArrayList<Subject> subjects, final ArrayList<Target> targets,
			final boolean toAll, final boolean toCompany) {
		final Broadcast broadcast = new Broadcast();
		broadcast.id = "";
		broadcast.orignalId = orignalId;
		broadcast.senderId = SaveManager.getInstance(this).getLoginId();
		broadcast.content = content;
		broadcast.favoriteClicked = false;
		broadcast.favoriteCount = 0;
		broadcast.likeClicked = false;
		broadcast.liked = 0;
		broadcast.state = Broadcast.COMMITING;
		broadcast.publishTime = "锟秸革拷";
		if (data != null) {
			broadcast.sender = data.Name;
			broadcast.headpic = data.Avatar;
			broadcast.senderTag = data.Name;
		}
		int PN = path.size();
		broadcast.ImageList = new ArrayList<Image>();
		ArrayList<String> images = new ArrayList<String>();
		for (int i = 0; i < PN; i++) {
			int []wh=getImageWH(path.get(i).path);

			Image image = new Image();
			image.ThumbImg = path.get(i).path;
			image.SourceImg = path.get(i).path;
			image.AccessURL = path.get(i).path;
			image.Width=wh[0];
			image.Height=wh[1];
			broadcast.ImageList.add(image);
			images.add(path.get(i).path);
		}

		int GN = targets.size();
		broadcast.sentTo = new ArrayList<Group>();
		// for (int i = 0; i < GN; i++) {
		// Group group = new Group();
		// group.Id = targets.get(i).Id;
		// group.Name = targets.get(i).Name;
		// broadcast.sentTo.add(group);
		// }
		int SN = subjects.size();
		broadcast.subjectList = new Subject[SN];
		for (int i = 0; i < SN; i++) {
			Subject subject = new Subject();
			subject.Name = subjects.get(i).Name;
			subject.IcoPath = subjects.get(i).IcoPath;
			subject.Id = subjects.get(i).Id;
			broadcast.subjectList[i] = subject;
		}
		commitingList.add(broadcast);
		isSyncBroadcast = true;
		SociabilityClient.getClient().addBroadcast(content, orignalId, groupId,
				images, subjects, targets, toAll, toCompany,
				new BaseCallback<JDBroadcast>() {

					@Override
					public void onResultCallback(int code, String message,
							JDBroadcast result) {
						// TODO Auto-generated method stub
						if (code < 0) {

						} else {
							commitingList.remove(broadcast);
							Intent intent = new Intent(
									Config.REFRESH_BROADCAST_ACTION);
							intent.putExtra("data", result.data);
							sendBroadcast(intent);
						}
					}
				}, JDBroadcast.class);
	}

	public void loadUserData() {
		if (data == null) {
			SociabilityClient.getClient().getMyProfile(
					new BaseCallback<JDUserProfile>() {

						@Override
						public void onResultCallback(int code, String message,
								JDUserProfile result) {
							// TODO Auto-generated method stub
							if (code >= 0) {
								data = result.data;
							}
						}
					}, JDUserProfile.class);

		}
	}

	public int getThemeResource(int attr) {
		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute(attr, typedValue, true);
		return typedValue.resourceId;
	}

	@Override
	public void onDispachMessage(MessageItem message) {
		// TODO Auto-generated method stub
		if(activity instanceof MessageReceiver){
			MessageReceiver receiver=(MessageReceiver) activity;
			receiver.onReceiverMessage(message);
		}else{
			Logger.e("dispach message to notify");
			Logger.e("--------------"+message.sender);
			Logger.e("--------------"+message.content);
			Logger.e("--------------"+message.to);
			Logger.e("--------------"+message.toUserId);

			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			// 定义通知栏展现的内容信息
			int icon = R.drawable.ic_launcher;
			String title=message.sender+"给您发来了新消息";
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, title, when);
			// 定义下拉通知栏时要展现的内容信息
			notification.defaults=Notification.DEFAULT_SOUND;

			Context context = getApplicationContext();
			CharSequence contentTitle = title;
			CharSequence contentText = message.content;
			Intent notificationIntent = new Intent(this, UserChatActivity.class);
			notificationIntent.putExtra("target_name", message.sender);
			notificationIntent.putExtra("target_id", message.senderId);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			mNotificationManager.notify(Config.NOTIFICATION_ID_MESSAGE,
					notification);
		}
	}
	
	public int [] getImageWH(String path){
	       BitmapFactory.Options opts = new BitmapFactory.Options();
           opts.inJustDecodeBounds = true;
           BitmapFactory.decodeFile(path, opts);
           opts.inSampleSize = 1;  
           opts.inJustDecodeBounds = false;
           Bitmap mBitmap =BitmapFactory.decodeFile(path, opts);
           int width=opts.outWidth;
           int height=opts.outHeight;
           return new int []{width,height};
	}

}
