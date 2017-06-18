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
		locations.add(new YLLocation("��ʵ����", "��������������9��"));
		locations.add(new YLLocation("��̩����", "��������������9��"));

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

		// ��ȡRefreshLayoutʵ��
		refreshListView = (RefreshLayout) rootLayout
				.findViewById(R.id.swipe_layout);
		// ��������ˢ��ʱ����ɫֵ,��ɫֵ��Ҫ������xml��
		refreshListView.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// ��������ˢ�¼�����
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

		// ���ؼ�����
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
		return "������";
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// ��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
		option.setCoorType(tempcoor);// ��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ��
		int span = 0;
		option.setScanSpan(span);// ��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
		option.setIsNeedAddress(true);// ��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
		option.setOpenGps(false);// ��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
		option.setLocationNotify(false);// ��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		option.setIgnoreKillProcess(true);// ��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��
		option.setEnableSimulateGps(false);// ��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
		option.setIsNeedLocationDescribe(false);// ��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
		option.setIsNeedLocationPoiList(false);// ��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
		locationClient.setLocOption(option);
	}

	public class LocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			x = location.getLatitude();
			y = location.getLongitude();
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// ���綨λ���
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
						toast("��λ��");
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
