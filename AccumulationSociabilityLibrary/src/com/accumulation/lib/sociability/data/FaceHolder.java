package com.accumulation.lib.sociability.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import com.accumulation.lib.sociability.R;

/**
 * ����ʵ����
 * */
public class FaceHolder {
	/** ����������Ϣ */
	public Face face;
	/** ����ͼƬ��Ϣ */
	private Drawable bmp;
	/** ����ı�� */
	public int index;

//	public void setBitmap(Drawable b) {
//		this.bmp = b;
//	}
//
//	public Drawable getFaceBmp(Context cxt) {
////		if (face != null) {
////			try {
////				AssetManager assetManager = cxt.getAssets();
////				InputStream is = assetManager.open(face.path);
////				return new GifAnimationDrawable(cxt.getResources(), is);
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
////		} else {
////			new ColorDrawable();
////		}
//
//		try {
//			if (face != null) {
//				AssetManager assetManager = cxt
//						.getAssets();
//				return new GifDrawable(assetManager, face.path);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}