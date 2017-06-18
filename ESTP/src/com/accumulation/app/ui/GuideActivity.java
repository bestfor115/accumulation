package com.accumulation.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.lib.ui.indicator.CirclePageIndicator;

public class GuideActivity extends BaseActivity {
	public static final int[] ICONS = { R.drawable.splash_update_0,
		R.drawable.splash_update_1,R.drawable.splash_update_2,R.drawable.splash_update_3 };
	private CirclePageIndicator indicator;
	private ViewPager viewpager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// …Ë÷√»´∆¡
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		SaveManager.getInstance(this).setDidGuide(true);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_guide;
	}

	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		indicator.setRadius(5 * density);
		indicator.setFillColor(0xFF000000);
		indicator.setStrokeWidth(1 * density);
		viewpager.setOffscreenPageLimit(3);
		viewpager.setAdapter(new GuidePagerAdapter());
		indicator.setViewPager(viewpager);
	}

	class GuidePagerAdapter extends PagerAdapter {

		public GuidePagerAdapter() {
		}

		@Override
		public int getCount() {
			return ICONS.length;
		}

		@Override
		public Object instantiateItem(View collection, int position) {

			View layout = getLayoutInflater().inflate(
					R.layout.item_guide_pager, null);
			View icon = layout.findViewById(R.id.guide_icon);
			Button go = (Button) layout.findViewById(R.id.go);

			go.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isLogined()){
						gotoMainActivity();
					}else{
						gotoLoginActivity();
					}
					gotoLoginActivity();
				}
			});
			go.setVisibility(position == ICONS.length - 1 ? View.VISIBLE
					: View.GONE);
			icon.setBackgroundResource(ICONS[position]);
			((ViewPager) collection).addView(layout);
			return layout;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	private void gotoMainActivity() {
		Intent mIntent = new Intent(this, MainActivity.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mIntent);
	}

	private void gotoLoginActivity() {
		Intent mIntent = new Intent(this, LoginActivity.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mIntent);
	}

}
