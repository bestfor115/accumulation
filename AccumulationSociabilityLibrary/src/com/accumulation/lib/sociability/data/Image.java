package com.accumulation.lib.sociability.data;

import java.io.Serializable;
/**
 * 动态或评论中的图片
 * */
public class Image implements Serializable {

	private static final long serialVersionUID = -1980146382701976863L;
	public String AccessURL;
	public String SourceImg;
	public String ThumbImg;
	public int Width;
	public int Height;
}