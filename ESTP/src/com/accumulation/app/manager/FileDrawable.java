package com.accumulation.app.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.accumulation.app.AccumulationAPP;

public class FileDrawable extends Drawable {
	String file;
	int W, H;
	Bitmap bmp;
	private Paint mPaint;  

	public FileDrawable(String file) {
		this.file = file;
		mPaint = new Paint();  
        mPaint.setAntiAlias(true);  
		int[] wh = AccumulationAPP.getInstance().getImageWH(file);

		float scale = (wh[0] + 0f) / AccumulationAPP.getInstance().width;
		W = (int) (wh[0] / scale);
		H = (int) (wh[1] / scale);
		this.setBounds(0, 0, W, H);
		bmp=BitmapFactory.decodeFile(file);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.GRAY);
		if(bmp!=null){
			canvas.drawBitmap(bmp, 0, 0, null);
		}
	}

	@Override
	public int getIntrinsicWidth()
	{
		return W;
	}

	@Override
	public int getIntrinsicHeight()
	{
		return H;
	}

	@Override
	public void setAlpha(int alpha)
	{
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf)
	{
		mPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}
}
