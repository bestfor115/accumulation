package com.accumulation.lib.sociability.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.accumulation.lib.sociability.data.Broadcast.Group;

/**
 * ¶¯Ì¬ÆÀÂÛ
 * */
public class Comment implements Serializable {
	public String content;
	public String headPic;
	public String sendBy;
	public String id;
	public String sendByUserId;
	public String sendTime;
	public List<Group> sentTo;
	public ArrayList<Image> ImageList;

}
