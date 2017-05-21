package com.accumulation.lib.sociability.im;

import java.util.Map;

import android.content.Context;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

import com.accumulation.lib.sociability.data.MessageItem;
import com.accumulation.lib.tool.debug.Logger;

public class IMManager {
	private static final IMManager instance = new IMManager();

	Messagedispatcher dispacher;

	public static IMManager getManager() {
		return instance;
	}

	public void registerMessagedispatcher(Messagedispatcher d) {
		this.dispacher = d;
	}

	public void init(Context context) {
		JMessageClient.init(context);
		JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
		JMessageClient.registerEventReceiver(this);
	}

	public void enterSingleConversaion(String talk) {
		JMessageClient.enterSingleConversaion(talk);
	}

	public void exitConversaion() {
		JMessageClient.exitConversaion();
	}
	
	public void addStatistics(Context context,boolean flag){
		if(flag){
			JPushInterface.onResume(context);
		}else{
			JPushInterface.onPause(context);
		}
	}

	public void checkUserState(String account, String password) {
		JMessageClient.register(account, password, new BasicCallback() {

			@Override
			public void gotResult(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Logger.e("==================== " + arg1);
			}
		});
		JMessageClient.login(account, password, new BasicCallback() {

			@Override
			public void gotResult(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Logger.e("==================== " + arg1);
			}
		});
	}

	public void sendMessage(MessageItem message, String talkId) {
		Map<String, String> map = message.changeMap();
		map.put("msg_content", "this is a new message");
		final Message msg = JMessageClient.createSingleCustomMessage(talkId,
				map);
		msg.getContent().setStringExtra("msg_content", "this is a new message");
		JMessageClient.sendMessage(msg);
	}

	public void onEvent(MessageEvent event) {
		Message msg = event.getMessage();
		switch (msg.getContentType()) {
		case custom:
			CustomContent customContent = (CustomContent) msg.getContent();
			Map<String, String> map = customContent.getAllStringValues();
			Logger.e("image========"+map.get("ImageList"));
			MessageItem newItem = MessageItem.createByMap(map);
			if (dispacher != null) {
				dispacher.onDispachMessage(newItem);
			}
			break;
		default:
			break;
		}
	}

}
