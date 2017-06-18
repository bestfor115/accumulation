package com.accumulation.app.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.ui.CampaignListActivity;
import com.accumulation.app.ui.ChannelSquareActivity;
import com.accumulation.app.ui.LikeQQActivity;
import com.accumulation.app.ui.RankActivity;
import com.accumulation.app.ui.TopicListActivity;
import com.accumulation.app.ui.broadcast.ArticleHomeActivity;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.view.SlideShowView;
import com.accumulation.lib.ui.ViewHolder;
import com.accumulation.lib.ui.grid.WeightGridLayout;
import com.accumulation.lib.ui.grid.WeightGridLayout.WeightGridAdapter;
import com.zbar.lib.CaptureActivity;

public class DiscoveryFragment extends Fragment {
	WeightGridLayout discovery_grid;
	BaseAdapter adapter;
	SlideShowView slideShowView;

	String[] imageUrls = new String[] {
			"http://down1.sucaitianxia.com/psd02/psd158/psds28266.jpg",
			"http://pic5.nipic.com/20100111/4172825_160633007094_2.jpg",
			"http://down1.sucaitianxia.com/psd02/psd158/psds27988.jpg" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final ViewGroup view = (ViewGroup) inflater.inflate(
				R.layout.fragment_discovery, container, false);
		discovery_grid = ViewHolder.get(view, R.id.discovery_grid);
		discovery_grid.setAdapter(new DiscoveryAdapter(inflater.getContext()));
		slideShowView = ViewHolder.get(view, R.id.slideshowView);
		List<String> ads = new ArrayList<String>();
		Collections.addAll(ads, imageUrls);
		slideShowView.setData(ads);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		slideShowView.caculateNextSwitch();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		slideShowView.cancelNextSwitch();
	}

	class DiscoveryAdapter extends WeightGridAdapter {

		private Context context;

		public DiscoveryAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 9;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getXSpace() {
			// TODO Auto-generated method stub
			return PixelUtil.dp2px(10);
		}

		@Override
		public int getYSpace() {
			// TODO Auto-generated method stub
			return PixelUtil.dp2px(10);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_discovery_grid_item, null);
			}
			TextView discovery_name = (TextView) convertView
					.findViewById(R.id.discovery_name);
			ImageView discovery_image = (ImageView) convertView
					.findViewById(R.id.discovery_image);
			discovery_name.getPaint().setFakeBoldText(true);
			if (position == 0) {
				discovery_name.setText("排行榜");
				convertView.setBackgroundColor(Color.parseColor("#a65fc6"));
				discovery_image.setVisibility(View.VISIBLE);
				discovery_image.setImageResource(R.drawable.icon_rank_label);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								RankActivity.class);
						startActivity(intent);
					}
				});
			} else if (position == 1) {
				discovery_name.setText("慢友频道");
				convertView.setBackgroundColor(Color.parseColor("#BC8F8F"));
				discovery_image.setVisibility(View.VISIBLE);
				discovery_image.setImageResource(R.drawable.icon_channel_label);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								ChannelSquareActivity.class);
						startActivity(intent);
					}
				});

			} else if (position == 2) {
				discovery_name.setText("热搜话题");
				convertView.setBackgroundColor(Color.parseColor("#0296ce"));
				discovery_image.setVisibility(View.VISIBLE);
				discovery_image
						.setImageResource(R.drawable.icon_favorite_label);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								TopicListActivity.class);
						startActivity(intent);
					}
				});
				
			} else if (position == 3) {
				discovery_name.setText("健康百科");
				convertView.setBackgroundColor(Color.parseColor("#13a600"));
				discovery_image.setVisibility(View.VISIBLE);
				discovery_image.setImageResource(R.drawable.icon_health_label);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								ArticleHomeActivity.class);
						startActivity(intent);
					}
				});
			} else if (position == 4) {
				convertView.setBackgroundResource(R.drawable.icon_score_center);
			} else if (position == 5) {
				discovery_name.setText("附近的");
				convertView.setBackgroundColor(Color.parseColor("#CD5C5C"));
				discovery_image.setVisibility(View.VISIBLE);
				discovery_image.setImageResource(R.drawable.icon_local_label);
			} else if (position == 6) {
				discovery_name.setText("扫一扫");
				convertView.setBackgroundColor(Color.parseColor("#48D1CC"));
				discovery_image.setVisibility(View.VISIBLE);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								CaptureActivity.class);
						startActivity(intent);
					}
				});
			} else if (position == 7) {
				discovery_name.setText("精彩活动");
				convertView.setBackgroundColor(Color.parseColor("#48D1CC"));
				discovery_image.setVisibility(View.VISIBLE);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								CampaignListActivity.class);
						startActivity(intent);
					}
				});
				
			} else if (position == 8) {
				discovery_name.setText("更多");
				convertView.setBackgroundColor(Color.parseColor("#48D1CC"));
				discovery_image.setVisibility(View.VISIBLE);
			} else {
				convertView.setBackgroundColor(Color.WHITE);
			}

			// discovery_name.setText("名人榜");
			return convertView;
		}

		@Override
		public int getChildXSize(int position) {
			// TODO Auto-generated method stub
			return X[position];
		}

		@Override
		public int getChildYSize(int position) {
			// TODO Auto-generated method stub
			return Y[position];
		}

		@Override
		public int getXSize() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public int getYSize() {
			// TODO Auto-generated method stub
			return 3;
		}
	}

	int X[] = { 1, 1, 1, 1, 1, 1, 1,1,1 };
	int Y[] = { 1, 1, 1, 1, 1, 1, 1 ,1,1};
}
