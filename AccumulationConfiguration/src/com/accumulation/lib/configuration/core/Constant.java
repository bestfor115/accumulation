package com.accumulation.lib.configuration.core;

import java.io.IOException;
import java.io.Serializable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Constant implements Serializable{

	public static final String ATT_KEY = "key";
	public static final String ATT_VALUE = "value";
	public static final String TAG = "Constant";
	public static final String ITEM_TAG = "constant";
	/**
	 * 
	 */
	private static final long serialVersionUID = -9108128629305032269L;

    protected String key;
    protected String value;
    
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void loadData(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG && ITEM_TAG.equals(xpp.getName())) {
			key = xpp.getAttributeValue(null, ATT_KEY);
			eventType = xpp.next();
			while (!(eventType == XmlPullParser.END_TAG && ITEM_TAG.equals(xpp.getName()))) {
				if (key != null && eventType == XmlPullParser.TEXT) {
					value = xpp.getText().trim();
				}
				eventType = xpp.next();
			}
		}
	}

	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Constant c = new Constant();
		c.key=key;
		c.value=value;
		return c;
	}

    
}
