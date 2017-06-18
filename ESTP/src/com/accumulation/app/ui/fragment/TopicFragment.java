package com.accumulation.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.accumulation.app.R;
import com.accumulation.app.adapter.TopicAdapter;
import com.accumulation.app.view.RefreshLayout;
import com.accumulation.app.view.RefreshLayout.DataCaculateListener;
import com.accumulation.app.view.RefreshLayout.OnLoadListener;

public class TopicFragment extends Fragment {

	private BaseAdapter adapter;
	private int pageSize = 10;
	private int currentPage = 0;
	private int maxPage = 100;
	private RefreshLayout refreshListView;
	private ListView listView;
	private boolean showLoading = true;

	public static TopicFragment newInstance(String id) {
		TopicFragment f = new TopicFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final ViewGroup view = (ViewGroup) inflater.inflate(
				R.layout.fragment_topic, container, false);
		listView = (ListView) view.findViewById(R.id.listview);

		listView.setAdapter(adapter = new TopicAdapter(inflater
				.getContext()));
		// ��ȡRefreshLayoutʵ��
		refreshListView = (RefreshLayout) view.findViewById(R.id.swipe_layout);
		// ��������ˢ��ʱ����ɫֵ,��ɫֵ��Ҫ������xml��
		refreshListView.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// ��������ˢ�¼�����
		refreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadData(false);
			}
		});

		// ���ؼ�����
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
				return false;
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

		refreshListView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!more) {
					// ���������ø÷�������ˢ��
					refreshListView.setRefreshing(false);
				} else {
					// ���������ø÷���
					refreshListView.setLoading(false);
				}
			}
		}, 1000);
	}

	public void onResume() {
		super.onResume();
	};

	public void onPause() {
		super.onPause();
	};
}
