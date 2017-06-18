package com.accumulation.app.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.accumulation.app.R;
import com.accumulation.app.adapter.MessageAdapter;
import com.accumulation.app.config.Config;
import com.accumulation.app.ui.chat.UserChatActivity;
import com.accumulation.app.view.RefreshLayout;
import com.accumulation.app.view.RefreshLayout.DataCaculateListener;
import com.accumulation.app.view.RefreshLayout.OnLoadListener;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JLMessage;
import com.accumulation.lib.sociability.data.Message;

public class MessageFragmnet extends Fragment {
	private BaseAdapter adapter;
	private RefreshLayout refreshListView;
	private List<Message> datas = new ArrayList<Message>();
	private ListView listView;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final ViewGroup view = (ViewGroup) inflater.inflate(
				R.layout.fragment_message, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		View defaultMessage = LayoutInflater.from(getActivity()).inflate(
				R.layout.include_message_header, listView, false);
		listView.addHeaderView(defaultMessage);
		listView.setAdapter(adapter = new MessageAdapter(inflater.getContext(),
				datas));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Message message = datas.get(position - 1);
				if (!message.hasRead) {
					message.hasRead = true;
					adapter.notifyDataSetChanged();
				}
				if (Config.GROUP_TYPE.equals(message.type)) {
					// Intent intent = new Intent(getContext(),
					// DisplayDiscussActivity.class);
					// Bundle mBundle = new Bundle();
					// mBundle.putString("talk_id", message.groupId);
					// mBundle.putString("talk_name", message.target);
					// intent.putExtras(mBundle);
					// startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(),
							UserChatActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putString("target_name", message.target);
					mBundle.putString("target_id",
							message.sentByMySelf ? message.toUserId
									: message.senderId);
					intent.putExtras(mBundle);
					startActivity(intent);
				}
			}
		});
		// 获取RefreshLayout实例
		refreshListView = (RefreshLayout) view.findViewById(R.id.swipe_layout);
		// 设置下拉刷新时的颜色值,颜色值需要定义在xml中
		refreshListView.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		refreshListView.setDataCaculateListener(new DataCaculateListener() {

			@Override
			public boolean hasMoreData() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		// 设置下拉刷新监听器
		refreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadData();
			}
		});

		// 加载监听器
		refreshListView.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
			}
		});
		refreshListView.setDataCaculateListener(new DataCaculateListener() {

			@Override
			public boolean hasMoreData() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		loadData();
	}

	private void loadData() {
		SociabilityClient.getClient().getRecentChat(
				new BaseCallback<JLMessage>() {

					@Override
					public void onResultCallback(int code, String message,
							JLMessage result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							datas.clear();
							datas.addAll(result.data.list);
							adapter.notifyDataSetChanged();
						}
						refreshListView.setRefreshing(false);
					}
				}, JLMessage.class);
	}
}
