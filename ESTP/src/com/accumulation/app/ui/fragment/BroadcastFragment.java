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
import com.accumulation.app.ui.broadcast.AddCyclopediaActivity;
import com.accumulation.app.ui.broadcast.AddDisccussActivity;
import com.accumulation.app.ui.broadcast.AddTopicActivity;
import com.accumulation.app.ui.broadcast.LocationMeActivity;
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

public class BroadcastFragment extends Fragment {

	private static final List<Broadcast> broadcastList = new ArrayList<Broadcast>();
	private BaseAdapter adapter;
	private int pageSize = 10;
	private int currentPage = 0;
	private int maxPage = 100;
	private RefreshLayout refreshListView;
	private ListView listView;
	private FloatingActionButton add_broadcast, add_attendance, add_article,add_topic;
	private FloatingActionsMenu add_menu;
	private View alpha_mask;
	private TextView broadcast_count;
	private View filter_broadcast;
	private PopupWindow filterPopupWindow;
	private boolean showLoading=true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final ViewGroup view = (ViewGroup) inflater.inflate(
				R.layout.fragment_broadcast, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		listView.addHeaderView(inflater.inflate(
				R.layout.include_filter_broadcst, listView, false));
		broadcast_count = (TextView) listView
				.findViewById(R.id.broadcast_count);
		filter_broadcast = listView.findViewById(R.id.filter_broadcast);
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
		filter_broadcast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (filterPopupWindow != null && filterPopupWindow.isShowing()) {
					filterPopupWindow.dismiss();
				} else {
					View popupView = initMorePopWindow();
					popupView.measure(MeasureSpec.UNSPECIFIED,
							MeasureSpec.UNSPECIFIED);
					int popupWidth = popupView.getMeasuredWidth();
					int popupHeight = popupView.getMeasuredHeight();
					int[] location = new int[2];
					filter_broadcast.getLocationOnScreen(location);
					int X = AccumulationAPP.getInstance().width - popupWidth
							- PixelUtil.dp2px(5);
					int Y = location[1] + filter_broadcast.getHeight()
							+ PixelUtil.dp2px(5);
					filterPopupWindow.showAtLocation(filter_broadcast,
							Gravity.NO_GRAVITY, X, Y);
				}
			}
		});
		add_broadcast = ViewHolder.get(view, R.id.add_broadcast);
		add_attendance = ViewHolder.get(view, R.id.add_attendance);
		add_article = ViewHolder.get(view, R.id.add_article);
		add_topic = ViewHolder.get(view, R.id.add_topic);
		add_menu = ViewHolder.get(view, R.id.add_menu);
		alpha_mask = ViewHolder.get(view, R.id.alpha_mask);
		add_article.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add_menu.collapse();
				Intent intent=new Intent(getActivity(),AddCyclopediaActivity.class);
				startActivity(intent);
			}
		});
		add_topic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add_menu.collapse();
				Intent intent=new Intent(getActivity(),AddTopicActivity.class);
				startActivity(intent);
			}
		});
		add_attendance.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add_menu.collapse();
				
				Intent intent=new Intent(getActivity(),LocationMeActivity.class);
				startActivity(intent);
			}
		});
		alpha_mask.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (ViewHelper.getAlpha(alpha_mask) != 0) {
					add_menu.collapse();
					return true;
				} else {
					return false;
				}
			}
		});
		add_menu.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {

			@Override
			public void onMenuExpanded() {
				// TODO Auto-generated method stub
				ViewCompat.animate(alpha_mask).alpha(0.8f).setDuration(500)
						.start();
			}

			@Override
			public void onMenuCollapsed() {
				// TODO Auto-generated method stub
				ViewCompat.animate(alpha_mask).alpha(0.0f).setDuration(500)
						.start();
			}
		});
		add_broadcast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add_menu.collapse();
				Intent mIntent = new Intent(getActivity(),
						AddDisccussActivity.class);
				getActivity().startActivity(mIntent);
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
						if (dialog!=null&&dialog.isShowing()) {
							dialog.dismiss();
							dialog=null;
						}
					}

					@Override
					public void onRequestStart() {
						// TODO Auto-generated method stub
						super.onRequestStart();
						if(showLoading){
							showLoading=false;
							dialog=	UIUtils.createLoadingDialog(getActivity(), true);
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
							broadcast_count.setText("全部动态( " + total + " )");
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

	private View initMorePopWindow() {
		View customView = LayoutInflater.from(getActivity()).inflate(
				R.layout.pop_filter_broadcast, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度

		filterPopupWindow = new PopupWindow(customView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		filterPopupWindow.setAnimationStyle(R.style.Animations_Pop);
		filterPopupWindow.setTouchable(true);
		filterPopupWindow.setFocusable(true);
		// 自定义view添加触摸事件
		customView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (filterPopupWindow != null && filterPopupWindow.isShowing()) {
					filterPopupWindow.dismiss();
					filterPopupWindow = null;
				}
				return false;
			}
		});

		return customView;
	}
}
