package com.accumulation.lib.utility.gif;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;

/**
 * <p>
 * Creates an AnimationDrawable from a GIF image.
 * </p>
 * 
 * @author Femi Omojola <femi@hipmob.com>
 */
public class GifAnimationDrawable extends AnimationDrawable {
	private boolean decoded;

	private GifDecoder mGifDecoder;

	private Bitmap mTmpBitmap;

	private int height, width;
	
	private Resources mResources;

	public GifAnimationDrawable(Resources res, File f) throws IOException {
		this(res, f, false);
	}

	public GifAnimationDrawable(Resources res, InputStream is) throws IOException {
		this(res, is, false);
	}

	public GifAnimationDrawable(Resources res, File f, boolean inline) throws IOException {
		this(res, new BufferedInputStream(new FileInputStream(f), 32768), inline);
	}

	public GifAnimationDrawable(Resources res, InputStream is, boolean inline) throws IOException {
		super();
		InputStream bis = is;
		if (!BufferedInputStream.class.isInstance(bis))
			bis = new BufferedInputStream(is, 32768);
		mResources = res;
		decoded = false;
		mGifDecoder = new GifDecoder();
		mGifDecoder.read(bis);
		mTmpBitmap = mGifDecoder.getFrame(0);
		android.util.Log.v("GifAnimationDrawable", "===>Lead frame: [" + width + "x" + height
				+ "; " + mGifDecoder.getDelay(0) + ";" + mGifDecoder.getLoopCount() + "]");
		height = mTmpBitmap.getHeight();
		width = mTmpBitmap.getWidth();
		addFrame(new BitmapDrawable(mResources, mTmpBitmap), mGifDecoder.getDelay(0));
		setOneShot(mGifDecoder.getLoopCount() != 0);
		setVisible(true, true);
		if (inline) {
			loader.run();
		} else {
			new Thread(loader).start();
		}
	}

	public boolean isDecoded() {
		return decoded;
	}

	private Runnable loader = new Runnable() {
		public void run() {
			mGifDecoder.complete();
			int i, n = mGifDecoder.getFrameCount(), t;
			for (i = 1; i < n; i++) {
				mTmpBitmap = mGifDecoder.getFrame(i);
				t = mGifDecoder.getDelay(i);
				android.util.Log.v("GifAnimationDrawable", "===>Frame " + i + ": " + t + "]");
				addFrame(new BitmapDrawable(mResources, mTmpBitmap), t);
			}
			decoded = true;
			mGifDecoder = null;
			setVisible(true, true);
		}
	};

	public int getMinimumHeight() {
		return height;
	}

	public int getMinimumWidth() {
		return width;
	}

	public int getIntrinsicHeight() {
		return height;
	}

	public int getIntrinsicWidth() {
		return width;
	}
}