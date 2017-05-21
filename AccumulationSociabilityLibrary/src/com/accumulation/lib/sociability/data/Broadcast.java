package com.accumulation.lib.sociability.data;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * ¶¯Ì¬
 * */
public class Broadcast implements Serializable {
	public static final int COMMITING=1;
	public static final int COMMITED=0;
	private static final long serialVersionUID = -2743265745593480465L;
	public String id;
	public String orignalId;
	public String senderId;
	public String content;
	public String publishTime;
	public String publishTimeAccurate;
	public int replayCount;
	public int liked;
	public int favoriteCount;
	public boolean hasCollected;
	public boolean isTodayPublished;
	public String sentByMySelf;
	public String headpic;
	public String sender;
	public String senderTag;
	public String at;
	public Boolean likeClicked ;
	public Boolean favoriteClicked;
	public ArrayList<Group> sentTo;
	public Subject[] subjectList;
	public OriginalBroadcast original;
	public ArrayList<Image> ImageList;
	public int state=COMMITED;

	public static class Group implements Serializable {
		private static final long serialVersionUID = -5056885963061040096L;
		public String Id;
		public String Name;
		public String Description;
	}
}
