package com.accumulation.app.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.text.style.ImageSpan;
import android.view.View;

import com.accumulation.app.config.Config;
import com.accumulation.app.util.UIUtils;
import com.accumulation.lib.tool.debug.Logger;

public class ClickableImageSpan extends ImageSpan {

	private String url;

	public ClickableImageSpan(Drawable d, String url) {
		super(d, ALIGN_BASELINE);
		this.url = url;
	}

	public void onClick(View widget) {
		Context context = widget.getContext();
		Logger.d("visit url :"+url);
		if (url.startsWith(Config.BOARD_URL_FLAG)) {
//			String ids=url.substring(Config.BOARD_URL_FLAG.length());
//			String cardId=ids.split("#")[1];
//			Intent mIntent = new Intent(context,
//					DumpedCardDetailActivity.class);
//			Bundle mBundle = new Bundle();
//			mBundle.putString("cardId", cardId);
//			mIntent.putExtras(mBundle);
//			context.startActivity(mIntent);
		}else{
			try{
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
				context.startActivity(intent);
			}catch(Exception e){
				UIUtils.showTast(context, "无法解析该地址");
			}

		}
	}

	public int getSize(Paint paint, CharSequence text, int start, int end,
			Paint.FontMetricsInt fontMetricsInt) {
		Drawable drawable = getDrawable();
		Rect rect = drawable.getBounds();
		if (fontMetricsInt != null) {
			Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
			int fontHeight = fmPaint.bottom - fmPaint.top;
			int drHeight = rect.bottom - rect.top;

			int top = drHeight / 2 - fontHeight / 4;
			int bottom = drHeight / 2 + fontHeight / 4;

			fontMetricsInt.ascent = -bottom;
			fontMetricsInt.top = -bottom;
			fontMetricsInt.bottom = top;
			fontMetricsInt.descent = top;
		}
		return rect.right;
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
		Drawable drawable = getDrawable();
		canvas.save();
		int transY = 0;
		// transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
		transY = ((bottom - top) - drawable.getBounds().bottom) / 4 + top;

		canvas.translate(x, transY);
		drawable.draw(canvas);
		canvas.restore();
	}
}
