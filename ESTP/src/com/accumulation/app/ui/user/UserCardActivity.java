package com.accumulation.app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.ui.chat.UserChatActivity;
import com.accumulation.app.ui.fragment.TestFragment;
import com.accumulation.app.ui.fragment.UserBroadcastFragment;
import com.accumulation.app.ui.fragment.UserHomeFragment;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.view.SlidingTabLayout;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JDUserProfile;
import com.accumulation.lib.sociability.data.UserProfile;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.ui.header.TouchCallbackLayout;
import com.accumulation.lib.ui.header.tools.ScrollableFragmentListener;
import com.accumulation.lib.ui.header.tools.ScrollableListener;
import com.accumulation.lib.ui.header.tools.ViewPagerHeaderHelper;
import com.accumulation.lib.ui.scrollable.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

public class UserCardActivity extends BaseActivity implements
		TouchCallbackLayout.TouchEventListener, ScrollableFragmentListener,
		ViewPagerHeaderHelper.OnViewPagerTouchListener,
		ViewPropertyAnimatorListener {

	private static final long DEFAULT_DURATION = 300L;
	private static final float DEFAULT_DAMPING = 1.5f;
	private static final String TAG = "MainActivity";

	private SparseArrayCompat<ScrollableListener> mScrollableListenerArrays = new SparseArrayCompat<>();
	private ViewPager mViewPager;
	private View mHeaderLayoutView;
	private View topbar, title;
	private ImageView user_header;
	private TextView user_name;
	private UserProfile data;

	private ViewPagerHeaderHelper mViewPagerHeaderHelper;

	private int mTouchSlop;
	private int mTabHeight;
	private int mHeaderHeight;
	private int mToolbarHeight;
	private int mPushHeaght;
	
	private int pushHeight;
	private int headerHeight;
	private int toolbarHeight;

	private Interpolator mInterpolator = new DecelerateInterpolator();
	private int actionbarColor;

	private String id;
	private String email;

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		loadUserProfile();
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		id = intent.getStringExtra("id");
		email = intent.getStringExtra("email");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTranslucentStatus(true);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_user_card;
	}

	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		actionbarColor = getResources().getColor(R.color.toolbar_bg_color);
		mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
		mTabHeight = getResources().getDimensionPixelSize(R.dimen.tabs_height);
		mPushHeaght = getResources().getDimensionPixelSize(
				R.dimen.viewpager_push_height);
		mToolbarHeight = getResources().getDimensionPixelSize(
				R.dimen.topbar_btn_width)
				+ statusBarHeight;
		mHeaderHeight = getResources().getDimensionPixelSize(
				R.dimen.viewpager_header_height1)
				- mToolbarHeight + PixelUtil.dp2px(2);
		mViewPagerHeaderHelper = new ViewPagerHeaderHelper(this, this);
		TouchCallbackLayout touchCallbackLayout = (TouchCallbackLayout) findViewById(R.id.layout);
		touchCallbackLayout.setTouchEventListener(this);
		mHeaderLayoutView = findViewById(R.id.header);
		topbar = findViewById(R.id.topbar);
		title = findViewById(R.id.title);
		user_header = (ImageView) findViewById(R.id.user_header);
		user_name = (TextView) findViewById(R.id.user_name);
		topbar.setPadding(0, statusBarHeight, 0, 0);
		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
		// slidingTabLayout.setDistributeEvenly(true);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setPadding(0, statusBarHeight, 0, 0);

		mViewPager
				.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		slidingTabLayout.setViewPager(mViewPager);
		ViewCompat.setTranslationY(mViewPager, mHeaderHeight);

		findViewById(R.id.chat_user).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (data != null) {
					Intent mIntent = new Intent(UserCardActivity.this,
							UserChatActivity.class);
					Logger.e("+++++++++++++" + data.Name);
					Logger.e("+++++++++++++" + data.Id);

					mIntent.putExtra("target_name", data.Name);
					mIntent.putExtra("target_id", data.Id);
					startActivity(mIntent);
				}
			}
		});

	}

	@Override
	public boolean onLayoutInterceptTouchEvent(MotionEvent event) {

		return mViewPagerHeaderHelper.onLayoutInterceptTouchEvent(event,
				mTabHeight + mHeaderHeight);
	}

	@Override
	public boolean onLayoutTouchEvent(MotionEvent event) {
		return mViewPagerHeaderHelper.onLayoutTouchEvent(event);
	}

	@Override
	public boolean isViewBeingDragged(MotionEvent event) {
		return mScrollableListenerArrays.valueAt(mViewPager.getCurrentItem())
				.isViewBeingDragged(event);
	}

	@Override
	public void onMoveStarted(float y) {

	}

	@Override
	public void onMove(float y, float yDx) {
		float headerTranslationY = ViewCompat
				.getTranslationY(mHeaderLayoutView) + yDx;
		Logger.e("-------------------TY            " + (int) headerTranslationY);
		if (headerTranslationY >= mPushHeaght) { // pull end
			headerPush(0L);

			// Log.d("kaede", "pull end");
			if (countPullEnd >= 1) {
				if (countPullEnd == 1) {
					downtime = SystemClock.uptimeMillis();
					simulateTouchEvent(mViewPager, downtime,
							SystemClock.uptimeMillis(),
							MotionEvent.ACTION_DOWN, 250, y + mHeaderHeight);
				}
				simulateTouchEvent(mViewPager, downtime,
						SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE,
						250, y + mHeaderHeight);
			}
			countPullEnd++;
		} else if (headerTranslationY >= 0) { // push end
			ViewCompat.animate(mHeaderLayoutView)
					.translationY(headerTranslationY).setDuration(0)
					.setListener(this).start();
			FrameLayout.LayoutParams fl = (LayoutParams) mHeaderLayoutView
					.getLayoutParams();
			fl.height = (int) (PixelUtil.dp2px(225) + headerTranslationY
					/ mPushHeaght * mPushHeaght);
			fl.topMargin = (int) -headerTranslationY - 2;
			mHeaderLayoutView.setLayoutParams(fl);
			ViewCompat.animate(mViewPager)
					.translationY(headerTranslationY + mHeaderHeight)
					.setDuration(0).start();
		} else if (headerTranslationY <= -mHeaderHeight) { // push end
			headerFold(0L);
			// Log.d("kaede", "push end");
			// Log.d("kaede",
			// "kaede onMove y="+y+",yDx="+yDx+",headerTranslationY="+headerTranslationY);
			if (countPushEnd >= 1) {
				if (countPushEnd == 1) {
					downtime = SystemClock.uptimeMillis();
					simulateTouchEvent(mViewPager, downtime,
							SystemClock.uptimeMillis(),
							MotionEvent.ACTION_DOWN, 250, y + mHeaderHeight);
				}
				simulateTouchEvent(mViewPager, downtime,
						SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE,
						250, y + mHeaderHeight);
			}
			countPushEnd++;

		} else {

			// Log.d("kaede", "ing");
			/*
			 * if(!isHasDispatchDown3){
			 * simulateTouchEvent(mViewPager,SystemClock.uptimeMillis(),
			 * SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 250,
			 * y+mHeaderHeight); isHasDispatchDown3=true; }
			 */

			ViewCompat.animate(mHeaderLayoutView)
					.translationY(headerTranslationY).setDuration(0)
					.setListener(this).start();
			ViewCompat.animate(mViewPager)
					.translationY(headerTranslationY + mHeaderHeight)
					.setDuration(0).start();
		}
	}

	long downtime = -1;
	int countPushEnd = 0, countPullEnd = 0;

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
	public void onMoveEnded(boolean isFling, float flingVelocityY) {

		Logger.e("--------------------------- END----------------------------");
		// Log.d("kaede", "move end");
		countPushEnd = countPullEnd = 0;

		float headerY = ViewCompat.getTranslationY(mHeaderLayoutView); // 0閸掓媽绀嬮弫锟�
		if (headerY == 0 || headerY == -mHeaderHeight) {
			return;
		}

		if (mViewPagerHeaderHelper.getInitialMotionY()
				- mViewPagerHeaderHelper.getLastMotionY() < -mTouchSlop) {

			headerExpand(headerMoveDuration(true, headerY, isFling,
					flingVelocityY));
		} else if (mViewPagerHeaderHelper.getInitialMotionY()
				- mViewPagerHeaderHelper.getLastMotionY() > mTouchSlop) {
			headerFold(headerMoveDuration(false, headerY, isFling,
					flingVelocityY));
		} else {
			if (headerY > -mHeaderHeight / 2f) { // headerY > header/2 = expand
				headerExpand(headerMoveDuration(true, headerY, isFling,
						flingVelocityY));
			} else { // headerY < header/2= fold
				headerFold(headerMoveDuration(false, headerY, isFling,
						flingVelocityY));
			}
		}
	}

	private long headerMoveDuration(boolean isExpand, float currentHeaderY,
			boolean isFling, float velocityY) {

		long defaultDuration = DEFAULT_DURATION;

		if (isFling) {

			float distance = isExpand ? Math.abs(mHeaderHeight)
					- Math.abs(currentHeaderY) : Math.abs(currentHeaderY);
			velocityY = Math.abs(velocityY) / 1000;

			defaultDuration = (long) (distance / velocityY * DEFAULT_DAMPING);

			defaultDuration = defaultDuration > DEFAULT_DURATION ? DEFAULT_DURATION
					: defaultDuration;
		}

		return defaultDuration;
	}

	private void headerFold(long duration) {
		ViewCompat.animate(mHeaderLayoutView).translationY(-mHeaderHeight)
				.setDuration(duration).setInterpolator(mInterpolator)
				.setListener(this).start();

		ViewCompat.animate(mViewPager).translationY(0).setDuration(duration)
				.setInterpolator(mInterpolator).start();

		mViewPagerHeaderHelper.setHeaderExpand(false);
	}

	private void headerExpand(long duration) {
		ViewCompat.animate(mHeaderLayoutView).translationY(0)
				.setDuration(duration).setInterpolator(mInterpolator)
				.setListener(this).start();

		ViewCompat.animate(mViewPager).translationY(mHeaderHeight)
				.setDuration(duration).setInterpolator(mInterpolator).start();
		mViewPagerHeaderHelper.setHeaderExpand(true);
	}

	private void headerPush(long duration) {
		ViewCompat.animate(mHeaderLayoutView).translationY(mPushHeaght)
				.setDuration(duration).setInterpolator(mInterpolator)
				.setListener(this).start();

		ViewCompat.animate(mViewPager)
				.translationY(mHeaderHeight + mPushHeaght)
				.setDuration(duration).setInterpolator(mInterpolator).start();
		mViewPagerHeaderHelper.setHeaderExpand(true);
	}

	@Override
	public void onFragmentAttached(ScrollableListener listener, int position) {
		mScrollableListenerArrays.put(position, listener);
	}

	@Override
	public void onFragmentDetached(ScrollableListener listener, int position) {
		mScrollableListenerArrays.remove(position);
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			if (position == 0) {
				return UserHomeFragment.newInstance(id, email);
			} else if (position == 2) {
				return TestFragment.newInstance(position);
			}
			return UserBroadcastFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "主要";
			case 1:
				return "动态";
			}
			return "测试";
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
		float headerY = ViewCompat.getTranslationY(mHeaderLayoutView); // 0閸掓媽绀嬮弫锟�
		float distance = Math.abs(Math.abs(mHeaderHeight) - Math.abs(headerY));
		if (distance > mToolbarHeight / 10) {
			topbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0,
					actionbarColor));
			ViewHelper.setAlpha(title, 0.0f);
		} else {
			float persent = 1f - (distance - mToolbarHeight / 2);
			persent = Math.min(1.0f, persent);
			topbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(persent,
					actionbarColor));
			ViewHelper.setAlpha(title, persent);
		}
	}

	public void loadUserProfile() {
		SociabilityClient.getClient().getUserProfile(id, email,
				new BaseCallback<JDUserProfile>() {

					@Override
					public void onResultCallback(int code, String message,
							JDUserProfile result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							data = result.data;
							titleView.setText(result.data.Name);
							user_name.setText(result.data.Name);
							AccumulationAPP.getInstance().loadImage(
									result.data.Avatar, user_header);
						}
					}
				}, JDUserProfile.class);
	}

}
