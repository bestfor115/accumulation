package com.accumulation.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.ui.fragment.ChannelFragment;
import com.accumulation.app.ui.fragment.TestFragment;
import com.accumulation.app.view.SlidingTabLayout;
import com.accumulation.lib.ui.ViewHolder;
import com.accumulation.lib.ui.grid.drag.DragGridView;
import com.accumulation.lib.ui.grid.drag.SimpleScrollingStrategy;
import com.accumulation.lib.ui.grid.drag.SpanVariableGridView;
import com.accumulation.lib.ui.grid.drag.SpanVariableGridView.CalculateChildrenPosition;

public class ChannelSquareActivity extends BaseActivity implements
		SpanVariableGridView.OnItemLongClickListener,
		DragGridView.DragAndDropListener,
		SpanVariableGridView.OnItemClickListener {
	public boolean managing = false;
	private ViewPager pager;
	private GridView unorder_channel;
	private DragGridView order_channel;
	private ScrollView scrollView;
	private SlidingTabLayout tabs;
	private List<String> totals = new ArrayList<String>();
	private List<String> channels = new ArrayList<String>();
	private List<String> orderChannels = new ArrayList<String>();
	private List<String> unOrderChannels = new ArrayList<String>();

	private BaseAdapter orderAdapter, unOrderAdapter;
	private View channel_article_layout, channel_manage_layout;
	private View commit_manage;
	private TextView manage_dsn;
	private View choose_part;
	private String current;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_channel_square;
	}

	public void initManageData() {
		orderChannels.clear();
		unOrderChannels.clear();
		int N = totals.size();
		current = channels.get(pager.getCurrentItem());
		for (int i = 0; i < N; i++) {
			if (channels.contains(totals.get(i))) {
				orderChannels.add(totals.get(i));
			} else {
				unOrderChannels.add(totals.get(i));
			}
		}
	}

	public void saveManageData() {
		channels.clear();
		channels.addAll(orderChannels);
		int focus = orderChannels.indexOf(current);
		pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		tabs.setViewPager(pager);
		pager.setCurrentItem(focus);
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		totals.add("推荐");
		totals.add("糖尿病");
		totals.add("电脑病");
		totals.add("职业病");
		totals.add("近视");
		totals.add("中医");
		totals.add("过敏");
		totals.add("亚健康");
		totals.add("男人女人");
		totals.add("急救知识");
		totals.add("老人健康");
		totals.add("食品安全");

		for (int i = 0; i < 6; i++) {
			channels.add(totals.get(i));
		}
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "慢友频道";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		pager = ViewHolder.get(rootLayout, R.id.viewpager);
		pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		tabs.setViewPager(pager);
		order_channel = ViewHolder.get(rootLayout, R.id.order_channel);
		unorder_channel = ViewHolder.get(rootLayout, R.id.unorder_channel);
		commit_manage = ViewHolder.get(rootLayout, R.id.commit_manage);
		scrollView = ViewHolder.get(rootLayout, R.id.channel_manage_layout);
		manage_dsn = ViewHolder.get(rootLayout, R.id.manage_dsn);
		choose_part = ViewHolder.get(rootLayout, R.id.choose_part);

		channel_article_layout = ViewHolder.get(rootLayout,
				R.id.channel_article_layout);
		channel_manage_layout = ViewHolder.get(rootLayout,
				R.id.channel_manage_layout);
		commit_manage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				caculateManageUI(false);
			}
		});
		ViewHolder.get(rootLayout, R.id.colume_down).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						initManageData();
						channel_article_layout.setVisibility(View.GONE);
						channel_manage_layout.setVisibility(View.VISIBLE);
						caculateManageUI(false);
					}
				});
		ViewHolder.get(rootLayout, R.id.colume_up).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						channel_article_layout.setVisibility(View.VISIBLE);
						channel_manage_layout.setVisibility(View.GONE);
						saveManageData();
					}
				});
		order_channel.setAdapter(orderAdapter = new OrderChannelAdapter());
		order_channel.setScrollingStrategy(new SimpleScrollingStrategy(
				scrollView));
		order_channel.setDragAndDropListener(this);
		order_channel.setOnItemLongClickListener(this);
		unorder_channel
				.setAdapter(unOrderAdapter = new UnOrderChannelAdapter());

	}

	@Override
	protected void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		Intent intent = new Intent(getContext(), ChannelManageActivity.class);
		startActivity(intent);
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			return ChannelFragment.newInstance(position + "");
		}

		@Override
		public int getCount() {
			return channels.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return channels.get(position);
		}
	}

	class OrderChannelAdapter extends BaseAdapter implements
			CalculateChildrenPosition {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return orderChannels.size();
		}

		@Override
		public String getItem(int arg0) {
			// TODO Auto-generated method stub
			return orderChannels.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.item_channel_grid, null);
			}
			final String entry = getItem(position);
			SpanVariableGridView.LayoutParams lp = new SpanVariableGridView.LayoutParams(
					LayoutParams.MATCH_PARENT);
			lp.span = 1;
			convertView.setLayoutParams(lp);
			TextView channel_name = ViewHolder.get(convertView,
					R.id.channel_name);
			ImageView delete_channel = ViewHolder.get(convertView,
					R.id.delete_channel);
			delete_channel.setVisibility(managing ? View.VISIBLE : View.GONE);
			channel_name.setText(getItem(position));
			delete_channel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					current = orderChannels.get(Math.max(0, position - 1));
					unOrderChannels.add(orderChannels.remove(position));
					orderAdapter.notifyDataSetChanged();
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!managing) {
						current = entry;
						channel_article_layout.setVisibility(View.VISIBLE);
						channel_manage_layout.setVisibility(View.GONE);
						saveManageData();
					}

				}
			});
			View channel_bg = ViewHolder.get(convertView, R.id.channel_bg);
			if (entry.equals(current)) {
				channel_bg.setBackgroundResource(R.drawable.channel_select);
				channel_name.setTextColor(Color.WHITE);
			} else {
				channel_bg.setBackgroundResource(R.drawable.channel_drawable);
				channel_name.setTextColor(getResources().getColorStateList(
						R.color.rank_test_color));
			}
			return convertView;
		}

		@Override
		public void onCalculatePosition(View view, int position, int row,
				int column) {
			// TODO Auto-generated method stub

		}

	}

	class UnOrderChannelAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return unOrderChannels.size();
		}

		@Override
		public String getItem(int arg0) {
			// TODO Auto-generated method stub
			return unOrderChannels.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.item_channel_grid, null);
			}
			TextView channel_name = ViewHolder.get(convertView,
					R.id.channel_name);
			channel_name.setText(getItem(position));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					orderChannels.add(unOrderChannels.remove(position));
					orderAdapter.notifyDataSetChanged();
					unOrderAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragItem(int from) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraggingItem(int from, int to) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDropItem(int from, int to) {
		// TODO Auto-generated method stub
		if (from != to) {
			orderChannels.add(to, orderChannels.remove(from));
			orderAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean isDragAndDropEnabled(int position) {
		// TODO Auto-generated method stub
		return managing;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		caculateManageUI(true);
		order_channel.startDragAndDrop();
		return true;
	}

	private void caculateManageUI(boolean manage) {
		this.managing = manage;
		if (managing) {
			manage_dsn.setText("拖动排序");
		} else {
			manage_dsn.setText("切换栏目");
		}
		orderAdapter.notifyDataSetChanged();
		unOrderAdapter.notifyDataSetChanged();
		choose_part.setVisibility(managing ? View.GONE : View.VISIBLE);
		commit_manage.setVisibility(managing ? View.VISIBLE : View.GONE);
		ViewHolder.get(rootLayout, R.id.colume_up).setVisibility(
				managing ? View.GONE : View.VISIBLE);
	}

}
