package com.accumulation.app.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.adapter.BroadcastAdapter;
import com.accumulation.app.config.Config;
import com.accumulation.app.ui.broadcast.AddArticleActivity;
import com.accumulation.app.ui.broadcast.AddDisccussActivity;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.util.UIUtils;
import com.accumulation.app.view.RefreshLayout;
import com.accumulation.app.view.RefreshLayout.DataCaculateListener;
import com.accumulation.app.view.RefreshLayout.OnLoadListener;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.Broadcast;
import com.accumulation.lib.sociability.data.JLBroadcast;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.ui.ViewHolder;
import com.accumulation.lib.ui.floating.FloatingActionButton;
import com.accumulation.lib.ui.floating.FloatingActionsMenu;
import com.accumulation.lib.ui.floating.FloatingActionsMenu.OnFloatingActionsMenuUpdateListener;
import com.nineoldandroids.view.ViewHelper;

public class ChannelFragment extends Fragment {

	private static final List<Broadcast> broadcastList = new ArrayList<Broadcast>();
	private BaseAdapter adapter;
	private int pageSize = 10;
	private int currentPage = 0;
	private int maxPage = 100;
	private RefreshLayout refreshListView;
	private ListView listView;
	private boolean showLoading = true;

	public static ChannelFragment newInstance(String id) {
		ChannelFragment f = new ChannelFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final ViewGroup view = (ViewGroup) inflater.inflate(
				R.layout.fragment_channel, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		listView.addHeaderView(inflater.inflate(R.layout.include_channel_head,
				listView, false));
		listView.setAdapter(adapter = new BroadcastAdapter(inflater
				.getContext(), broadcastList));
		// 获取RefreshLayout实例
		refreshListView = (RefreshLayout) view.findViewById(R.id.swipe_layout);
		// 设置下拉刷新时的颜色值,颜色值需要定义在xml中
		refreshListView.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// 设置下拉刷新监听器
		refreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadData(false);
			}
		});

		// 加载监听器
		refreshListView.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
				loadData(true);
			}
		});
		refreshListView.setDataCaculateListener(new DataCaculateListener() {

			@Override
			public boolean hasMoreData() {
				// TODO Auto-generated method stub
				return currentPage < maxPage - 1;
			}
		});
		loadData(false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	private void loadData(final boolean more) {
		SociabilityClient.getClient().getBroadcastByType(
				SociabilityClient.BROADCAST_TYPE_ID_ALL, currentPage, pageSize,
				new BaseCallback<JLBroadcast>() {

					private Dialog dialog;

					@Override
					public void onRequestEnd() {
						// TODO Auto-generated method stub
						super.onRequestEnd();
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
						}
					}

					@Override
					public void onRequestStart() {
						// TODO Auto-generated method stub
						super.onRequestStart();
						if (showLoading) {
							showLoading = false;
							dialog = UIUtils.createLoadingDialog(getActivity(),
									true);
							dialog.show();
						}
					}

					@Override
					public void onResultCallback(int code, String message,
							JLBroadcast result) {
						if (code >= 0) {
							if (!more) {
								currentPage = 0;
								broadcastList.clear();
							} else {
								currentPage++;
							}
							int total = result.data.recordCount;
							maxPage = total / pageSize;
							if (total % pageSize == 0) {
								maxPage = Math.max(0, maxPage - 1);
							}
							broadcastList.addAll(result.getCollectionData());
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();
						}
						if (!more) {
							// 更新完后调用该方法结束刷新
							refreshListView.setRefreshing(false);
						} else {
							// 加载完后调用该方法
							refreshListView.setLoading(false);
						}
					}
				}, JLBroadcast.class);
	}

	public void onResume() {
		super.onResume();
		if (AccumulationAPP.getInstance().isSyncBroadcast) {
			AccumulationAPP.getInstance().isSyncBroadcast = false;
			adapter.notifyDataSetChanged();
		}
		getActivity().registerReceiver(networkChangeReceiver,
				new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		getActivity().registerReceiver(broadcastChangeReceiver,
				new IntentFilter(Config.REFRESH_BROADCAST_ACTION));
	};

	public void onPause() {
		super.onPause();
		if (networkChangeReceiver != null) {
			getActivity().unregisterReceiver(networkChangeReceiver);
		}
		if (broadcastChangeReceiver != null) {
			getActivity().unregisterReceiver(broadcastChangeReceiver);
		}
	};

	BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			View invalidNetwork = listView.findViewWithTag("network");
			if (!CommonUtils.isNetworkAvailable(context)) {
				if (invalidNetwork == null) {
					invalidNetwork = LayoutInflater.from(getActivity())
							.inflate(R.layout.include_no_network, listView,
									false);
					invalidNetwork.setTag("network");
					listView.addHeaderView(invalidNetwork);
					listView.setHeaderDividersEnabled(false);
					invalidNetwork.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(
									Settings.ACTION_WIRELESS_SETTINGS);
							startActivity(intent);
						}
					});
				}
			} else {
				if (invalidNetwork != null) {
					listView.removeHeaderView(invalidNetwork);
					listView.setHeaderDividersEnabled(true);
					loadData(false);
				}
			}
		}
	};
	BroadcastReceiver broadcastChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Broadcast broadcast = (Broadcast) intent
					.getSerializableExtra("data");
			if (broadcast != null) {
				broadcastList.add(0, broadcast);
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	};
}
