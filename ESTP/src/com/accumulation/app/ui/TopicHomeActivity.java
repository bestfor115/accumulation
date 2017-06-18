package com.accumulation.app.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.ui.fragment.TopicBroadcastFragment;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.ui.ViewHolder;
import com.accumulation.lib.ui.header.TouchCallbackLayout;
import com.accumulation.lib.ui.header.TouchCallbackLayout.TouchEventListener;
import com.accumulation.lib.ui.header.tools.ScrollableFragmentListener;
import com.accumulation.lib.ui.header.tools.ScrollableListener;
import com.accumulation.lib.ui.header.tools.ViewPagerHeaderHelper;
import com.accumulation.lib.ui.header.tools.ViewPagerHeaderHelper.OnViewPagerTouchListener;
import com.accumulation.lib.ui.scrollable.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

public class TopicHomeActivity extends BaseActivity implements
		TouchEventListener, OnViewPagerTouchListener,
		ScrollableFragmentListener, ViewPropertyAnimatorListener {
	private static final long DEFAULT_DURATION = 300L;
	private static final float DEFAULT_DAMPING = 1.5f;
	private SparseArrayCompat<ScrollableListener> scrollableListenerArrays = new SparseArrayCompat<ScrollableListener>();
	private View headerLayout;
	private View tabbarLayout;
	private View pagerLayout;
	private ViewPager pager;
	private View topbar, title;
	private int tabbarHeight;
	private int pushHeight;
	private int headerHeight;
	private int toolbarHeight;
	private Interpolator mInterpolator = new DecelerateInterpolator();
	private Interpolator mScaleInterpolator = new LinearInterpolator();

	private int actionbarColor;
	private int touchSlop;
	private ViewPagerHeaderHelper pagerHeaderHelper;
	long downtime = -1;
	int countPushEnd = 0, countPullEnd = 0;
	private ScalingRunnable scaleRunnable = new ScalingRunnable();
	private ImageView flagView;
	private boolean loading;
	private ProgressBar loadingProgress;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_topic_home;
	}

	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTranslucentStatus(true);
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "#话题名称#";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		actionbarColor = getResources().getColor(R.color.toolbar_bg_color);
		touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
		tabbarHeight = getResources().getDimensionPixelSize(
				R.dimen.topic_tab_height);
		pushHeight = getResources().getDimensionPixelSize(
				R.dimen.topic_push_height);
		headerHeight = getResources().getDimensionPixelSize(
				R.dimen.topic_header_height);
		toolbarHeight = getResources().getDimensionPixelSize(
				R.dimen.base_action_bar_height);

		pagerHeaderHelper = new ViewPagerHeaderHelper(this, this);
		topbar = findViewById(R.id.topbar);
		title = findViewById(R.id.title);
		flagView = (ImageView) findViewById(R.id.flagView);
		loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);
		TouchCallbackLayout touchCallbackLayout = (TouchCallbackLayout) findViewById(R.id.root);

		touchCallbackLayout.setTouchEventListener(this);
		topbar.setPadding(0, statusBarHeight, 0, 0);

		pager = ViewHolder.get(rootLayout, R.id.viewpager);
		headerLayout = ViewHolder.get(rootLayout, R.id.header_layout);
		tabbarLayout = ViewHolder.get(rootLayout, R.id.tabbar_layout);
		pagerLayout = ViewHolder.get(rootLayout, R.id.pager_layout);
		pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		headerExpand(0L);
		caculateDrawable(BitmapFactory.decodeResource(getResources(),
				R.drawable.column3_user_05),
				(ImageView) findViewById(R.id.card_background));
		ViewHolder.get(rootLayout, R.id.topic_chat).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getContext(),
								ChatroomActivity.class);
						startActivity(intent);
					}
				});
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			return TopicBroadcastFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}
	}

	@Override
	public void onFragmentAttached(ScrollableListener listener, int position) {
		scrollableListenerArrays.put(position, listener);
	}

	@Override
	public void onFragmentDetached(ScrollableListener listener, int position) {
		scrollableListenerArrays.remove(position);
	}

	@Override
	public boolean onLayoutInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return pagerHeaderHelper.onLayoutInterceptTouchEvent(ev,
				getMaxHeaderOffset());
	}

	@Override
	public boolean onLayoutTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return pagerHeaderHelper.onLayoutTouchEvent(ev);
	}

	@Override
	public boolean isViewBeingDragged(MotionEvent event) {
		// TODO Auto-generated method stub
		return scrollableListenerArrays.valueAt(pager.getCurrentItem())
				.isViewBeingDragged(event);
	}

	@Override
	public void onMoveStarted(float eventY) {
		// TODO Auto-generated method stub

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public void onMove(float y, float yDx) {
		// TODO Auto-generated method stub
		float headerTranslationY = ViewCompat.getTranslationY(headerLayout)
				+ yDx;
		Logger.e("---------------TY           " + (int) headerTranslationY);

		if (headerTranslationY >= pushHeight) { // pull end
			if (countPullEnd >= 1) {
				if (countPullEnd == 1) {
					downtime = SystemClock.uptimeMillis();
					simulateTouchEvent(pager, downtime,
							SystemClock.uptimeMillis(),
							MotionEvent.ACTION_DOWN, 250, y
									+ getMaxHeaderOffset());
				} else {
					headerPush(0L);
				}
				simulateTouchEvent(pager, downtime, SystemClock.uptimeMillis(),
						MotionEvent.ACTION_MOVE, 250, y + getMaxHeaderOffset());
			}
			countPullEnd++;
		} else if (headerTranslationY >= 0) { // push end

			FrameLayout.LayoutParams fl = (LayoutParams) headerLayout
					.getLayoutParams();
			fl.height = (int) (headerHeight + headerTranslationY / pushHeight
					* pushHeight);
			fl.topMargin = (int) -headerTranslationY - PixelUtil.dp2px(1);
			headerLayout.setLayoutParams(fl);

			ViewCompat.animate(headerLayout).translationY(headerTranslationY)
					.setDuration(0).setListener(this).start();
			ViewCompat.animate(tabbarLayout).translationY(headerTranslationY)
					.setDuration(0).setListener(this).start();
			ViewCompat
					.animate(pagerLayout)
					.translationY(
							tabbarHeight + headerHeight + headerTranslationY)
					.setDuration(0).start();

		} else if (headerTranslationY <= -headerHeight + toolbarHeight
				+ statusBarHeight) { // push end
			headerFold(0L);
			if (countPushEnd >= 1) {
				if (countPushEnd == 1) {
					downtime = SystemClock.uptimeMillis();
					simulateTouchEvent(pager, downtime,
							SystemClock.uptimeMillis(),
							MotionEvent.ACTION_DOWN, 250, y
									+ getMaxHeaderOffset());
				}
				simulateTouchEvent(pager, downtime, SystemClock.uptimeMillis(),
						MotionEvent.ACTION_MOVE, 250, y + getMaxHeaderOffset());
			}
			countPushEnd++;

		} else {
			ViewCompat.animate(headerLayout).translationY(headerTranslationY)
					.setDuration(0).setListener(this).start();
			ViewCompat.animate(tabbarLayout).translationY(headerTranslationY)
					.setDuration(0).setListener(this).start();
			ViewCompat
					.animate(pagerLayout)
					.translationY(
							tabbarHeight + headerHeight + headerTranslationY)
					.setDuration(0).start();
		}

		if (loading) {
			loadingProgress.setVisibility(View.VISIBLE);
			flagView.setVisibility(View.GONE);
		} else {
			loadingProgress.setVisibility(View.GONE);
			flagView.setVisibility(View.VISIBLE);
			if (headerTranslationY > 0) {
				flagView.setImageResource(R.drawable.loading11);
				float degree = Math.abs(headerTranslationY + 0.0f) * 0.3f;
				ViewCompat.setRotation(flagView, degree);
			} else {
				flagView.setImageResource(R.drawable.ic_bar_web_more_normal);
				ViewCompat.setRotation(flagView, 0.0f);
			}
		}

	}

	@Override
	public void onMoveEnded(boolean isFling, float flingVelocityY) {
		// TODO Auto-generated method stub
		countPushEnd = countPullEnd = 0;

		float headerY = ViewCompat.getTranslationY(headerLayout); // 0閸掓媽绀嬮弫锟�
		if (headerY > pushHeight * 0.8f && !loading) {
			onRefresh();
			loading = true;
		}
		if (loading) {
			loadingProgress.setVisibility(View.VISIBLE);
			flagView.setVisibility(View.GONE);
		} else {
			loadingProgress.setVisibility(View.GONE);
			flagView.setVisibility(View.VISIBLE);
			flagView.setImageResource(R.drawable.ic_bar_web_more_normal);
			ViewCompat.setRotation(flagView, 0.0f);
		}
		if (headerY == 0
				|| headerY == -headerHeight + toolbarHeight + statusBarHeight) {
			return;
		}

		if (pagerHeaderHelper.getInitialMotionY()
				- pagerHeaderHelper.getLastMotionY() < -touchSlop) {
			headerExpand(headerMoveDuration(true, headerY, isFling,
					flingVelocityY));
		} else if (pagerHeaderHelper.getInitialMotionY()
				- pagerHeaderHelper.getLastMotionY() > touchSlop) {
			headerFold(headerMoveDuration(false, headerY, isFling,
					flingVelocityY));
		} else {
			if (headerY > -getMaxHeaderOffset() / 2f) { // headerY > header/2
														// = expand
				headerExpand(headerMoveDuration(true, headerY, isFling,
						flingVelocityY));
			} else { // headerY < header/2= fold
				headerFold(headerMoveDuration(false, headerY, isFling,
						flingVelocityY));
			}
		}
	}

	private void headerFold(long duration) {
		ViewCompat.animate(headerLayout)
				.translationY(-headerHeight + toolbarHeight + statusBarHeight)
				.setDuration(duration).setInterpolator(mInterpolator)
				.setListener(this).start();
		ViewCompat.animate(tabbarLayout)
				.translationY(-headerHeight + toolbarHeight + statusBarHeight)
				.setDuration(duration).setInterpolator(mInterpolator)
				.setListener(this).start();
		ViewCompat.animate(pagerLayout)
				.translationY(toolbarHeight + statusBarHeight + tabbarHeight)
				.setDuration(duration).setInterpolator(mInterpolator).start();
		pagerHeaderHelper.setHeaderExpand(false);
		scaleRunnable.startAnimation(duration);
	}

	private void headerExpand(long duration) {
		ViewCompat.animate(headerLayout).translationY(0).setDuration(duration)
				.setInterpolator(mInterpolator).setListener(this).start();
		ViewCompat.animate(tabbarLayout).translationY(0).setDuration(duration)
				.setInterpolator(mInterpolator).setListener(this).start();
		ViewCompat.animate(pagerLayout)
				.translationY(headerHeight + tabbarHeight)
				.setDuration(duration).setInterpolator(mInterpolator).start();
		pagerHeaderHelper.setHeaderExpand(true);

		scaleRunnable.startAnimation(duration);

	}

	class ScalingRunnable implements Runnable {
		protected long mDuration;
		protected boolean mIsFinished = true;
		protected float mScale;
		protected long mStartTime;

		ScalingRunnable() {
		}

		public void abortAnimation() {
			mIsFinished = true;
		}

		public boolean isFinished() {
			return mIsFinished;
		}

		public void run() {
			if (headerLayout != null) {
				float f2;
				FrameLayout.LayoutParams localLayoutParams;
				if ((!mIsFinished) && (mScale > 1.0D)) {
					float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime)
							/ (float) mDuration;
					f2 = mScale - (mScale - 1.0F)
							* mScaleInterpolator.getInterpolation(f1);
					localLayoutParams = (LayoutParams) headerLayout
							.getLayoutParams();
					Logger.d(TAG, "ScalingRunnable --> f2 = " + f2);
					if (f2 > 1.0F && f1 <= 1f) {
						localLayoutParams.height = 0 + ((int) (f2 * headerHeight));
						float translateY = (ViewCompat
								.getTranslationY(headerLayout));
						translateY = Math.max(translateY, (f2 - 1f)
								* headerHeight);
						localLayoutParams.topMargin = (int) -translateY;
						headerLayout.setLayoutParams(localLayoutParams);
						headerLayout.post(this);
						return;
					}

					localLayoutParams.height = (int) (headerHeight);
					localLayoutParams.topMargin = 0;
					headerLayout.setLayoutParams(localLayoutParams);
					mIsFinished = true;
				}
			}
		}

		public void startAnimation(long paramLong) {
			if (headerLayout != null) {
				float s = (headerLayout.getHeight() + 0f) / headerHeight;
				if (s < 1f) {
					mScale = 1.0001f;
				} else {
					mScale = Math.min(1f + (pushHeight + 0f) / headerHeight, s);
				}
				Logger.e("---------------- SCALE:  " + mScale);
				mStartTime = SystemClock.currentThreadTimeMillis();
				mDuration = paramLong / 2;
				mIsFinished = false;
				headerLayout.post(this);
			}
		}
	}

	private void headerPush(long duration) {
		ViewCompat.animate(headerLayout).translationY(pushHeight)
				.setDuration(duration).setInterpolator(mInterpolator)
				.setListener(this).start();
		ViewCompat.animate(tabbarLayout).translationY(pushHeight)
				.setDuration(duration).setInterpolator(mInterpolator)
				.setListener(this).start();
		ViewCompat.animate(pagerLayout)
				.translationY(headerHeight + tabbarHeight + pushHeight)
				.setDuration(duration).setInterpolator(mInterpolator).start();
		pagerHeaderHelper.setHeaderExpand(true);

		FrameLayout.LayoutParams fl = (LayoutParams) headerLayout
				.getLayoutParams();
		fl.height = (int) (headerHeight + pushHeight);
		fl.topMargin = (int) -pushHeight;
		headerLayout.setLayoutParams(fl);
	}

	private long headerMoveDuration(boolean isExpand, float currentHeaderY,
			boolean isFling, float velocityY) {

		long defaultDuration = DEFAULT_DURATION;

		if (isFling) {

			float distance = isExpand ? Math.abs(getMaxHeaderOffset())
					- Math.abs(currentHeaderY) : Math.abs(currentHeaderY);
			velocityY = Math.abs(velocityY) / 1000;

			defaultDuration = (long) (distance / velocityY * DEFAULT_DAMPING);

			defaultDuration = defaultDuration > DEFAULT_DURATION ? DEFAULT_DURATION
					: defaultDuration;
		}

		return defaultDuration;
	}

	private int getMaxHeaderOffset() {
		return headerHeight - toolbarHeight - statusBarHeight;
	}

	private void simulateTouchEvent(View dispatcher, long downTime,
			long eventTime, int action, float x, float y) {
		MotionEvent motionEvent = MotionEvent.obtain(
				SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action,
				x, y, 0);
		try {
			dispatcher.dispatchTouchEvent(motionEvent);
		} catch (Throwable e) {
			Log.e(TAG, "simulateTouchEvent error: " + e.toString());
		} finally {
			motionEvent.recycle();
		}
	}

	@Override
	public void onAnimationCancel(View arg0) {
		// TODO Auto-generated method stub
		caculateToolBarState();

	}

	@Override
	public void onAnimationEnd(View arg0) {
		// TODO Auto-generated method stub
		caculateToolBarState();

	}

	@Override
	public void onAnimationStart(View arg0) {
		// TODO Auto-generated method stub
		caculateToolBarState();

	}

	private void caculateToolBarState() {
		float headerY = ViewCompat.getTranslationY(headerLayout);
		float distance = getMaxHeaderOffset() - Math.abs(headerY);
		if (distance > tabbarHeight / 10) {
			topbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0,
					actionbarColor));
			ViewHelper.setAlpha(title, 0.0f);
		} else {
			float persent = 1f - (distance - tabbarHeight / 2);
			persent = Math.min(1.0f, persent);
			topbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(persent,
					actionbarColor));
			ViewHelper.setAlpha(title, persent);
		}
	}

	private void onRefresh() {
		headerLayout.postDelayed(new Runnable() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				loading = false;
				loadingProgress.setVisibility(View.GONE);
				flagView.setVisibility(View.VISIBLE);
				flagView.setImageResource(R.drawable.ic_bar_web_more_normal);
				flagView.setRotation(0);
			}
		}, 2000);
	}

	private void caculateDrawable(Bitmap bmp, final ImageView view) {
		Palette.generateAsync(bmp, new Palette.PaletteAsyncListener() {
			@Override
			public void onGenerated(Palette palette) {
				Palette.Swatch swatch = palette.getVibrantSwatch();
				Logger.e("------------------------------start caculate -----------------------------");

				if (null != swatch) {
					Logger.e("------------------------------");
					GradientDrawable drawable = new GradientDrawable(
							GradientDrawable.Orientation.TL_BR, new int[] {
									swatch.getPopulation(), swatch.getRgb() });
					// view.setBackgroundDrawable(drawable);
					view.setImageDrawable(drawable);
				}
			}
		});
	}

}
