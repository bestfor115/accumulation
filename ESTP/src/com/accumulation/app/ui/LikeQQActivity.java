package com.accumulation.app.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.ui.scrollable.ObservableScrollView;
import com.accumulation.lib.ui.scrollable.ObservableScrollViewCallbacks;
import com.accumulation.lib.ui.scrollable.ScrollState;
import com.accumulation.lib.ui.scrollable.ScrollUtils;
import com.accumulation.lib.ui.scrollable.Scrollable;

public class LikeQQActivity extends BaseActivity implements
		ObservableScrollViewCallbacks {

	View topbar, topbarContainer, title;
	int topbarHeight = 0;
	protected int mFlexibleSpaceImageHeight;
	private int mPrevScrollY;
	private boolean mReady;
	private int actionbarColor;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setTranslucentStatus(true);
		mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(
				R.dimen.flexible_space_image_height);
		topbarHeight = statusBarHeight
				+ getResources().getDimensionPixelSize(
						R.dimen.base_action_bar_height);
		actionbarColor = getResources().getColor(R.color.toolbar_bg_color);
		final Scrollable scrollable = createScrollable();
		ScrollUtils.addOnGlobalLayoutListener((View) scrollable,
				new Runnable() {
					@Override
					public void run() {
						mReady = true;
						updateViews(scrollable.getCurrentScrollY(), false);
					}
				});
	}

	@SuppressLint("NewApi")
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		topbar = findViewById(R.id.topbar);
		title = findViewById(R.id.title);
		topbarContainer = findViewById(R.id.topbar_container);
		topbar.setPadding(0, statusBarHeight, 0, 0);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_qq_test;
	}

	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll,
			boolean dragging) {
		// TODO Auto-generated method stub
		updateViews(scrollY, true);
	}

	@Override
	public void onDownMotionEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		// TODO Auto-generated method stub

	}

	protected Scrollable createScrollable() {
		ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
		scrollView.setScrollViewCallbacks(this);
		return scrollView;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint({ "ResourceAsColor", "NewApi" })
	protected void updateViews(int scrollY, boolean animated) {
		// If it's ListView, onScrollChanged is called before ListView is laid
		// out (onGlobalLayout).
		// This causes weird animation when onRestoreInstanceState occurred,
		// so we check if it's laid out already.
		if (!mReady) {
			return;
		}

		boolean scrollUp = mPrevScrollY < scrollY;
		if (scrollUp) {

		} else {

		}
		int distance = scrollY - mFlexibleSpaceImageHeight + topbarHeight;
		if (distance > 0) {
			float persent = distance
					/ (mFlexibleSpaceImageHeight - topbarHeight + 0.0f);
			persent = Math.min(1.0f, persent * 5);
			topbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(persent,
					actionbarColor));
			title.setAlpha(persent);
		} else {
			topbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0,
					actionbarColor));
			title.setAlpha(0.0f);
		}
		mPrevScrollY = scrollY;
	}
}
