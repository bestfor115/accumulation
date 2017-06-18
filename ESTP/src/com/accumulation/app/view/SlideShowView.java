package com.accumulation.app.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.accumulation.app.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class SlideShowView extends FrameLayout {

	// 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
	private ImageLoader imageLoader = ImageLoader.getInstance();
	// 自动轮播的时间间隔
	private final static int TIME_INTERVAL = 8 * 1000;
	// 自动轮播启用开关
	private final static boolean isAutoPlay = true;
	// 自定义轮播图的资源
	List<String> imageUris = new ArrayList<String>();
	private ViewPager viewPager;
	private LinearLayout dotLayout;
	// Handler
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (canAutoSwitch) {
				int current = viewPager.getCurrentItem();
				viewPager.setCurrentItem(++current, true);
			}
			caculateNextSwitch();
		}
	};

	public SlideShowView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initUI(context);
	}

	/**
	 * 初始化Views等UI
	 */
	private void initUI(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
				true);
		dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		viewPager.setAdapter(new SwitchAdapter());
		viewPager.setOnPageChangeListener(new SwitchPageChangeListener());
	}

	public void setData(List<String> uris) {
		this.imageUris.clear();
		imageUris.addAll(uris);
		viewPager.setAdapter(new SwitchAdapter());
		viewPager.setOnPageChangeListener(new SwitchPageChangeListener());
	}

	public void caculateNextSwitch() {
		handler.removeMessages(0);
		handler.sendEmptyMessageDelayed(0, TIME_INTERVAL);
	}

	public void cancelNextSwitch() {
		handler.removeMessages(0);
	}

	/**
	 * 填充ViewPager的页面适配器
	 * 
	 */
	private class SwitchAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageView view = new ImageView(getContext());
			view.setScaleType(ScaleType.FIT_XY);
			imageLoader.displayImage(
					imageUris.get(position % imageUris.size()), view);
			((ViewPager) container).addView(view);
			return view;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (imageUris.size() == 0) {
				return 0;
			}
			return imageUris.size() * 1000;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

	boolean canAutoSwitch = true;

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 * 
	 */
	private class SwitchPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
				canAutoSwitch = true;
			} else {
				canAutoSwitch = false;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub

		}

	}

}