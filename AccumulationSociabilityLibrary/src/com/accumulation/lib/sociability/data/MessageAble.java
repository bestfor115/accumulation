package com.accumulation.lib.sociability.data;

import java.util.HashMap;

public interface MessageAble {
	public static final int MESSAGE_TYPE_TEXT = 0;
	public static final int MESSAGE_TYPE_IMAGE = 0;
	public static final int MESSAGE_TYPE_LOCATION = 0;
	public static final int MESSAGE_TYPE_VOICE = 0;
	public static final int MESSAGE_TYPE_FILE = 0;

	public String getSender();

	public String getReciver();

	public String getContent();

	public int getType();

	public String getTime();

	public HashMap<String, String> changeMap();

}
