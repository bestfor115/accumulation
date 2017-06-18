package com.zyl.push.app.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.app.ui.fragment.BroadcastFragment;
import com.accumulation.app.ui.fragment.DiscoveryFragment;
import com.accumulation.app.ui.fragment.MessageFragmnet;
import com.accumulation.app.ui.user.SelfCenterActivity;
import com.accumulation.lib.ui.indicator.IconPagerAdapter;
import com.accumulation.lib.ui.indicator.SlidingIconPageIndicator;

public class UIActivity extends BaseActivity {
	private ViewPager mViewPager;
	private SlidingIconPageIndicator mIndicator;
	private ImageView userHeader;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_ui;
	}

	@Override
	protected void initView() {
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mIndicator = (SlidingIconPageIndicator) findViewById(R.id.indicator);
		userHeader = (ImageView) findViewById(R.id.user_header);
		Integer[] res = { R.drawable.tab_first_selector,
				R.drawable.tab_second_selector, R.drawable.tab_third_selector };
		mIndicator.setTabItemTitles(Arrays.asList(res));

		List<Fragment> fragments = initFragments();
		FragmentAdapter adapter = new FragmentAdapter(fragments,
				getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mIndicator.setViewPager(mViewPager, 0);
		AccumulationAPP.getInstance().loadImage(
				SaveManager.getInstance(getContext()).getSaveHeader(),
				userHeader);
		findViewById(R.id.user).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(),
						SelfCenterActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.search).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(), SearchActivity.class);
				startActivity(intent);
			}
		});

	}

	private List<Fragment> initFragments() {
		List<Fragment> fragments = new ArrayList<Fragment>();

		Fragment userFragment = new BroadcastFragment();
		fragments.add(userFragment);

		Fragment contactFragment = new DiscoveryFragment();
		fragments.add(contactFragment);
		Fragment messageFragment = new MessageFragmnet();
		fragments.add(messageFragment);
		return fragments;
	}

	class FragmentAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {
		private List<Fragment> mFragments;

		public FragmentAdapter(List<Fragment> fragments, FragmentManager fm) {
			super(fm);
			mFragments = fragments;
		}

		@Override
		public Fragment getItem(int i) {
			return mFragments.get(i);
		}

		@Override
		public int getIconResId(int index) {
			return 0;
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}
	}

}
