package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.data.YLLocation;
import com.accumulation.app.view.RefreshLayout;
import com.accumulation.app.view.RefreshLayout.DataCaculateListener;
import com.accumulation.app.view.RefreshLayout.OnLoadListener;
import com.accumulation.lib.ui.ViewHolder;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class LocationMeActivity extends BaseActivity {

	private List<YLLocation> locations = new ArrayList<YLLocation>();
	private LocationClient locationClient;
	private LocationListener locationListener = new LocationListener();
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";
	double x, y;
	private BaseAdapter adapter;
	private int pageSize = 10;
	private int currentPage = 0;
	private int maxPage = 100;
	private RefreshLayout refreshListView;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		locations.add(new YLLocation("求实大厦", "竹子林紫竹六道9号"));
		locations.add(new YLLocation("联泰大厦", "竹子林紫竹六道9号"));

		locationClient = AccumulationAPP.getInstance().locationClient;
		locationClient.registerLocationListener(locationListener);
		initLocation();
		locationClient.start();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		locationClient.stop();
		super.onStop();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		listView = (ListView) rootLayout.findViewById(R.id.listview);
		listView.setAdapter(adapter = new LocationAdapter());

		// 获取RefreshLayout实例
		refreshListView = (RefreshLayout) rootLayout
				.findViewById(R.id.swipe_layout);
		// 设置下拉刷新时的颜色值,颜色值需要定义在xml中
		refreshListView.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// 设置下拉刷新监听器
		refreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshListView.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						refreshListView.setRefreshing(false);
					}
				}, 100);
			}
		});

		// 加载监听器
		refreshListView.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
				refreshListView.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						refreshListView.setLoading(false);
					}
				}, 100);
			}
		});
		refreshListView.setDataCaculateListener(new DataCaculateListener() {

			@Override
			public boolean hasMoreData() {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_location_me;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "我在这";
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType(tempcoor);// 可选，默认gcj02，设置返回的定位结果坐标系，
		int span = 0;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(false);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		locationClient.setLocOption(option);
	}

	public class LocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			x = location.getLatitude();
			y = location.getLongitude();
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
			// toast((location.getAddrStr()));
			}
		}
	}

	class LocationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return locations.size() + 1;
		}

		@Override
		public YLLocation getItem(int position) {
			// TODO Auto-generated method stub
			if (position == locations.size()) {
				return null;
			}
			return locations.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.item_location_list, null);
			}
			View info_layout = ViewHolder.get(convertView, R.id.info_layout);
			View create_layout = ViewHolder
					.get(convertView, R.id.create_layout);
			final YLLocation entry = getItem(position);
			create_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (x == 0 && y == 0) {
						toast("定位中");
						return;
					}
					Intent intent = new Intent(getContext(),
							CreateLocationActivity.class);
					intent.putExtra("x", x);
					intent.putExtra("y", y);
					startActivityForResult(intent, 0);
				}
			});
			TextView location_name = ViewHolder.get(convertView,
					R.id.location_name);
			TextView location_description = ViewHolder.get(convertView,
					R.id.location_description);

			if (entry == null) {
				info_layout.setVisibility(View.GONE);
				create_layout.setVisibility(View.VISIBLE);
			} else {
				info_layout.setVisibility(View.VISIBLE);
				create_layout.setVisibility(View.GONE);
				location_name.setText(entry.name);
				location_description.setText(entry.description);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (entry != null) {
						Intent intent = new Intent(getContext(),
								AddDisccussActivity.class);
						intent.putExtra("location", entry);
						startActivity(intent);
					}
				}
			});
			return convertView;
		}

	}

}
