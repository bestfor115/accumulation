package com.accumulation.app.manager;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;

import com.accumulation.app.AccumulationAPP;

public class FileImageSpan extends DynamicDrawableSpan {
	String file;
	int W, H;
    private Drawable mDrawable;
    private Context context;
	public FileImageSpan(String file,Context context){
		this.file = file;
		this.context=context;
		int[] wh = AccumulationAPP.getInstance().getImageWH(file);

		float scale = (wh[0] + 0f) / AccumulationAPP.getInstance().width;
		W = (int) (wh[0] / scale);
		H = (int) (wh[1] / scale);
	}
	
    @Override
    public int getSize(Paint paint, CharSequence text,
                         int start, int end,
                         Paint.FontMetricsInt fm) {
        return W;
    }

	@Override
	public Drawable getDrawable() {
		// TODO Auto-generated method stub
        Drawable drawable = null;
        
        if (mDrawable != null) {
            drawable = mDrawable;
        } else {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(file);
                drawable = new BitmapDrawable(context.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
            } catch (Exception e) {
                Log.e("sms", "Failed to loaded content " + file);
            }
        }
        return drawable;
	}
	
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
		// TODO Auto-generated method stub
        Drawable b = getFileCachedDrawable();
        canvas.save();
        
        int transY = bottom- b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
	}
	
    private Drawable getFileCachedDrawable() {
        WeakReference<Drawable> wr = mFileDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mFileDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mFileDrawableRef;
}
