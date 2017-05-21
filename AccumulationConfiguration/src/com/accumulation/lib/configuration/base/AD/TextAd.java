package com.accumulation.lib.configuration.base.AD;

import java.util.List;

public class TextAd {
	public String x;
	public String y;
	public String width;
	public String height;
	
	public String id;
	public FlyAds flyads;
	
	public static class FlyAds {
		public List<FlyEntry> flyEntrys;
		public String extendsUrl;
		public int speed;
		public String fontColor;
		public String fontSize;
		public String runType;
		public String backgroundImage;
		public String backgroundColor;
		public String transparency;
		public int delay;
	}
	
	public static class FlyEntry{
		public String content;
		public int cycles;
	}
}
