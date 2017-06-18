package com.accumulation.app.dialog;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.accumulation.app.R;
import com.accumulation.app.manager.SpannedManager;
import com.accumulation.lib.sociability.data.Broadcast;
import com.accumulation.lib.tool.debug.Logger;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class ShareDialog extends Dialog {
	private static final int THUMB_SIZE = 150;

	public ShareDialog(Context context, int theme) {
		super(context, theme);
	}

	Broadcast entry;
	protected IWXAPI api;

	public static ShareDialog createDialog(Broadcast entry, Context cxt) {
		ShareDialog share = new ShareDialog(cxt, R.style.dialog);
		share.entry = entry;

		return share;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_share);
		// 设置对话框的位置和大小
		LayoutParams params = this.getWindow().getAttributes();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		getWindow().setAttributes(params);
		getWindow().setWindowAnimations(R.style.Animations_GrowFromBottom);
		findViewById(R.id.share_wx).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareBroadcast(0);
					}
				});
		findViewById(R.id.share_wx_chat).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareBroadcast(1);
					}
				});
		findViewById(R.id.share_wb_sina).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareBroadcast(2);
					}
				});
		findViewById(R.id.share_wb_tencent).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareBroadcast(3);
					}
				});
		findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});
		findViewById(R.id.share_qq).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareBroadcast(4);
					}
				});
		
	}

	public ShareParams getWechatMomentsShareParams() {
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
//		String content = entry.content;
//		content=SpannedManager.formatShareContent(getContext(), content);
//		sp.text = content;
//		sp.shareType = Platform.SHARE_WEBPAGE;
//		sp.url = UriConfig.DOMAIN+"/PublicShared/Index/"+entry.id;
//		sp.setTitle("Gov内控协作平台");
//		int N = entry.ImageList.size();
//		if (N > 0) {
//			String img = UriConfig.DOMAIN + "/Image/Index/"
//					+ entry.ImageList.get(0).SourceImg;
//			sp.imageUrl = img;
//		} 
		return sp;
	}

	public ShareParams getWechatShareParams() {
		Wechat.ShareParams sp = new Wechat.ShareParams();
		String content = entry.content;
//		content=SpannedManager.formatShareContent(getContext(), content);
//		sp.text = content;
//		sp.shareType = Platform.SHARE_WEBPAGE;
//		sp.url = UriConfig.DOMAIN+"/PublicShared/Index/"+entry.id;
//		sp.setTitle("Gov内控协作平台");
//		int N = entry.ImageList.size();
//		if (N > 0) {
//			String img = UriConfig.DOMAIN + "/Image/Index/"
//					+ entry.ImageList.get(0).SourceImg;
//			sp.imageUrl = img;
//		} 
		return sp;
	}

	public ShareParams getSinaWeiboShareParams() {
		SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
//		String url = UriConfig.DOMAIN+"/PublicShared/Index/"+entry.id;
//		sp.text = entry.content+url;
//		int N = entry.ImageList.size();
//		if (N > 0) {
//			String imgs="";
//			for (int i = 0; i < N; i++) {
//				String img = UriConfig.DOMAIN + "/Image/Index/"
//						+ entry.ImageList.get(i).SourceImg;
////				if(i==0){
////					imgs+=img;
////				}else{
////					imgs+=";"+img;
////				}
//				sp.imageUrl = img;
//			}
//		}
		
		return sp;
	}
	public ShareParams getTencentWeiboShareParams() {
		
		TencentWeibo.ShareParams sp = new TencentWeibo.ShareParams();
//		String url = UriConfig.DOMAIN+"/PublicShared/Index/"+entry.id;
//		sp.text = entry.content+url;
//		int N = entry.ImageList.size();
//		if (N > 0) {
//			for (int i = 0; i < N; i++) {
//				String img = UriConfig.DOMAIN + "/Image/Index/"
//						+ entry.ImageList.get(i).SourceImg;
//				sp.imageUrl = img;
//			}
//		}
//		
		return sp;
	}
	public ShareParams getQZoneShareParams() {
		
		QZone.ShareParams sp = new QZone.ShareParams();
//		String url = UriConfig.DOMAIN+"/PublicShared/Index/"+entry.id;
//
//		sp.setTitle("Gov内控协作平台");
//		sp.setTitleUrl(url); // 标题的超链接
//		String content = entry.content;
//		content=SpannedManager.formatShareContent(getContext(), content);
//		sp.setText(content);
//		sp.setSite(url);
//		sp.setSiteUrl(url);
//		sp.site=UriConfig.DOMAIN;
//		sp.text = entry.content+url;
//		int N = entry.ImageList.size();
//		if (N > 0) {
//			for (int i = 0; i < N; i++) {
//				String img = UriConfig.DOMAIN + "/Image/Index/"
//						+ entry.ImageList.get(i).SourceImg;
//				sp.imageUrl = img;
//			}
//		}

		return sp;
	}
	public ShareParams getQQShareParams() {
		
		QQ.ShareParams sp = new QQ.ShareParams();
		// String url = UriConfig.DOMAIN+"/PublicShared/Index/"+entry.id;
		//
		// sp.setTitle("Gov内控协作平台");
		// sp.setTitleUrl(url); // 标题的超链接
		// String content = entry.content;
		// content=SpannedManager.formatShareContent(getContext(), content);
		// sp.setText(content);
		// sp.setSite(url);
		// sp.setSiteUrl(url);
		// sp.text = entry.content+url;
		// int N = entry.ImageList.size();
		// if (N > 0) {
		// for (int i = 0; i < N; i++) {
		// String img = UriConfig.DOMAIN + "/Image/Index/"
		// + entry.ImageList.get(i).SourceImg;
		// sp.imageUrl = img;
		// }
		// }

		return sp;
	}
	/** 将action转换为String */
	public static String actionToString(int action) {
		switch (action) {
		case Platform.ACTION_AUTHORIZING:
			return "ACTION_AUTHORIZING";
		case Platform.ACTION_GETTING_FRIEND_LIST:
			return "ACTION_GETTING_FRIEND_LIST";
		case Platform.ACTION_FOLLOWING_USER:
			return "ACTION_FOLLOWING_USER";
		case Platform.ACTION_SENDING_DIRECT_MESSAGE:
			return "ACTION_SENDING_DIRECT_MESSAGE";
		case Platform.ACTION_TIMELINE:
			return "ACTION_TIMELINE";
		case Platform.ACTION_USER_INFOR:
			return "ACTION_USER_INFOR";
		case Platform.ACTION_SHARE:
			return "ACTION_SHARE";
		default: {
			return "UNKNOWN";
		}
		}
	}

	private void shareBroadcast(int type) {
		Logger.d("start share------------1-");

		Platform plat = null;
		ShareParams sp = null;
		switch (type) {
		case 0:
			plat = ShareSDK.getPlatform(getContext(), WechatMoments.NAME);
			sp = getWechatMomentsShareParams();
			break;
		case 1:
			plat = ShareSDK.getPlatform(getContext(), Wechat.NAME);
			sp = getWechatShareParams();
			break;
		case 2:
			plat = ShareSDK.getPlatform(getContext(), SinaWeibo.NAME);
			sp = getSinaWeiboShareParams();
			break;
		case 3:
			plat = ShareSDK.getPlatform(getContext(), QZone.NAME);
			sp = getQZoneShareParams();
//			plat = ShareSDK.getPlatform(getContext(), TencentWeibo.NAME);
//			sp = getTencentWeiboShareParams();
			break;
		case 4:
			plat = ShareSDK.getPlatform(getContext(), QQ.NAME);
			plat.SSOSetting(false);
			sp = getQQShareParams();
			break;
		}
		plat.setPlatformActionListener(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub
				Logger.d("------------1-" + actionToString(arg1));
				Logger.d("------------1-" + arg2.getMessage());

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				// TODO Auto-generated method stub
				Logger.d("------------2-");
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub
				Logger.d("------------3-");

			}
		});
		plat.share(sp);
		dismiss();

	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}
}
