package com.accumulation.lib.sociability.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.debug.Logger;
import com.google.gson.Gson;

public class MessageItem implements Serializable,MessageAble{
	public final static int COMMITING=0;
	public final static int ERROR=-1;
	public final static int DONE=1;
	public String id;
	public String senderId;
	public String toUserId;
	public boolean sentByMySelf;
	public String headpic;
	public String sender;
	public String content;
	public String publishTime;
	public String publishTimeAccurate;
	public int replayCount;
	public String sentTo;
	public String iWillReplyUserId;
	public String to;
	public List<Image> ImageList;
	public String target;
	public String targetAvatar;
	public String type;
	public String groupId;
	public boolean hasRead;
	public int state=DONE;
	static Gson gson = new Gson();

	@Override
	public String getSender() {
		// TODO Auto-generated method stub
		return senderId;
	}
	@Override
	public String getReciver() {
		// TODO Auto-generated method stub
		return toUserId;
	}
	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return content;
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getTime() {
		// TODO Auto-generated method stub
		return publishTime;
	}
	@Override
	public HashMap<String, String> changeMap() {
		// TODO Auto-generated method stub
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("id", id+"");
		map.put("senderId", senderId+"");
		map.put("toUserId", toUserId+"");
		map.put("content", CommonUtils.isEmpty(content)?"":(content+""));
		map.put("publishTime", publishTime+"");
		map.put("headpic", headpic+"");
		map.put("publishTimeAccurate", publishTimeAccurate+"");
		map.put("targetAvatar", targetAvatar+"");
		map.put("sender", sender+"");
		map.put("to", to+"");
		if(ImageList!=null&&ImageList.size()>0){
			String images=gson.toJson(ImageList);
			map.put("ImageList", images+"");
		}

		return map;
	}
	@SuppressWarnings("unchecked")
	public static MessageItem  createByMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		MessageItem message=new MessageItem();
		message.id=map.get("id")+"";
		message.senderId=map.get("senderId")+"";
		message.toUserId=map.get("toUserId")+"";
		message.content=map.get("content")+"";
		message.publishTime=map.get("publishTime")+"";
		message.headpic=map.get("headpic")+"";
		message.publishTimeAccurate=map.get("publishTimeAccurate")+"";
		message.targetAvatar=map.get("targetAvatar")+"";
		message.sender=map.get("sender")+"";
		message.to=map.get("to")+"";
		if(map.get("ImageList")!=null){
			message.ImageList=gson.fromJson(map.get("ImageList"),Images .class);
			Logger.e(message.ImageList.get(0).AccessURL);
		}
		return message;
	}
	

}