package com.accumulation.lib.sociability.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.accumulation.lib.sociability.data.Broadcast.Group;

public class OriginalBroadcast  implements Serializable
{
	private static final long serialVersionUID = -3838630456618299580L;
	public String id;
	public String senderTag;
	public String content; 
	public String publishTime;
	public List<Group> sentTo;
	public ArrayList<Image> ImageList;

}



