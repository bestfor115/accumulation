package com.accumulation.lib.configuration.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

public class ScrollTextView extends TextView {
	public interface OnScrollEndListener {
		public void onScrollEnd();
	}

	// scrolling feature
	private Scroller mSlr;

	// milliseconds per pixel
	private float mSpeed = 1f;

	// the X offset when paused
	private int mXPaused = 0;

	// whether it's being paused
	private boolean mPaused = true;

	/**
	 * repeat count, -1 for infinite
	 */
	private int mRepeat = -1;

	private OnScrollEndListener mScrollEndListener;

	public void setOnScrollEndListener(OnScrollEndListener l) {
		this.mScrollEndListener = l;
	}

	/*
	 * constructor
	 */
	public ScrollTextView(Context context) {
		this(context, null);
		// customize the TextView
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
	}

	/*
	 * constructor
	 */
	public ScrollTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
		// customize the TextView
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
	}

	/*
	 * constructor
	 */
	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// customize the TextView
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
	}

	/**
	 * begin to scroll the text from the original position
	 */
	public void startScroll(int count) {
		mRepeat = count;
		// begin from the very right side
		mXPaused = -1 * getWidth();
		// assume it's paused
		mPaused = true;
		resumeScroll();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(changed){
			mXPaused = -1 * getWidth();
			if(!mPaused)
				mPaused = true;
			resumeScroll();
		}
	}

	/**
	 * resume the scroll from the pausing point
	 */
	public void resumeScroll() {

		if (!mPaused)
			return;

		// Do not know why it would not scroll sometimes
		// if setHorizontallyScrolling is called in constructor.
		setHorizontallyScrolling(true);

		// use LinearInterpolator for steady scrolling
		mSlr = new Scroller(this.getContext(), new LinearInterpolator());
		setScroller(mSlr);

		int scrollingLen = calculateScrollingLen();
		int distance = scrollingLen - (getWidth() + mXPaused);
		int duration = (int) (distance * 50 / mSpeed);

		setVisibility(VISIBLE);
		mSlr.startScroll(mXPaused, 0, distance, 0, duration);
		mPaused = false;
	}

	/**
	 * calculate the scrolling length of the text in pixel
	 * 
	 * @return the scrolling length in pixels
	 */
	private int calculateScrollingLen() {
		TextPaint tp = getPaint();
		Rect rect = new Rect();
		String strTxt = getText().toString();
		tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
		int scrollingLen = rect.width() + getWidth();
		rect = null;
		return scrollingLen;
	}

	/**
	 * pause scrolling the text
	 */
	public void pauseScroll() {
		if (null == mSlr)
			return;

		if (mPaused)
			return;

		mPaused = true;

		// abortAnimation sets the current X to be the final X,
		// and sets isFinished to be true
		// so current position shall be saved
		mXPaused = mSlr.getCurrX();

		mSlr.abortAnimation();
	}

	/*
	 * override the computeScroll to restart scrolling when finished so as that
	 * the text is scrolled forever
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();

		if (null == mSlr)
			return;

		if (mSlr.isFinished() && (!mPaused)) {
			if (mRepeat == -1)
				startScroll(mRepeat);
			else if (mRepeat > 0)
				startScroll(mRepeat - 1);
			else if (mRepeat == 0 && mScrollEndListener != null)
				mScrollEndListener.onScrollEnd();

		}
	}

	public float getSpeed() {
		return mSpeed;
	}

	public void setSpeed(float speed) {
		if (speed <= 0)
			speed = 1f;
		this.mSpeed = speed;
	}

	public boolean isPaused() {
		return mPaused;
	}
}
