package com.accumulation.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by H3c on 12/12/14.
 */
public class ClipSquareView extends View {
    public static final int BORDERDISTANCE = 0;// ¾àÀëÆÁÄ»µÄ±ß¾à
    public static final int BODER_CUT_HEIGHT=510;
    private final float LINE_BORDER_WIDTH = 2f;// ¿ò¿ò¿í¶È
    private final int LINE_COLOR = Color.WHITE;
    private final Paint mPaint = new Paint();

    public ClipSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        mPaint.setColor(Color.parseColor("#ccffffff"));

        boolean isHorizontal = false;
        if(width > height) {
            isHorizontal = true;
        }

        int outLeft = 0;
        int outTop = 0;
        int outRight = width;
        int outBottom = height;
        int borderlength = isHorizontal ? (height - BORDERDISTANCE * 2) : (width - BORDERDISTANCE * 2);
        int inLeft = isHorizontal ? ((width - borderlength) / 2) : BORDERDISTANCE;
        int inTop = isHorizontal ? BORDERDISTANCE : ((height - borderlength) / 2);
        int inRight = isHorizontal ? (inLeft + borderlength) : borderlength + BORDERDISTANCE;
        int inBottom = isHorizontal ? borderlength + BORDERDISTANCE : (inTop + borderlength);
        int borderHeight=BODER_CUT_HEIGHT;
        int borderWidth=width;
        inLeft=0;
        inRight=width;
        inTop=height/2-borderHeight/2;
        inBottom=height/2+borderHeight/2;
        canvas.drawRect(outLeft, outTop, outRight, inTop, mPaint);
        canvas.drawRect(outLeft, inBottom, outRight, outBottom, mPaint);
        canvas.drawRect(outLeft, inTop, inLeft, inBottom, mPaint);
        canvas.drawRect(inRight, inTop, outRight, inBottom, mPaint);

        mPaint.setColor(LINE_COLOR);
        mPaint.setStrokeWidth(LINE_BORDER_WIDTH);
        canvas.drawLine(inLeft, inTop, inLeft, inBottom, mPaint);
        canvas.drawLine(inRight, inTop, inRight, inBottom, mPaint);
        canvas.drawLine(inLeft, inTop, inRight, inTop, mPaint);
        canvas.drawLine(inLeft, inBottom, inRight, inBottom, mPaint);
        
        mPaint.setColor(LINE_COLOR);
        mPaint.setStrokeWidth(1.5f);
        int N=3;
        for (int i = 1; i < N; i++) {
            canvas.drawLine(inLeft+i*borderWidth/N, inTop, inLeft+i*borderWidth/N, inBottom, mPaint);
		}
        for (int i = 1; i < N; i++) {
            canvas.drawLine(inLeft, inTop+i*borderHeight/N, inRight, inTop+i*borderHeight/N, mPaint);
		}
    }
}
