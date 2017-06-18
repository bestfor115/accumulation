package com.accumulation.app.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.accumulation.app.R;
import com.accumulation.app.adapter.BroadcastAdapter;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.Broadcast;
import com.accumulation.lib.sociability.data.JLBroadcast;
import com.accumulation.lib.ui.header.delegate.AbsListViewDelegate;

public class UserBroadcastFragment extends BaseViewPagerFragment implements
		AbsListView.OnItemClickListener {

	private ListView mListView;
	private BaseAdapter mAdapter;
	private AbsListViewDelegate mAbsListViewDelegate = new AbsListViewDelegate();
	private static final List<Broadcast> broadcastList = new ArrayList<Broadcast>();

	public static UserBroadcastFragment newInstance(int index) {
		UserBroadcastFragment fragment = new UserBroadcastFragment();
		Bundle args = new Bundle();
		args.putInt(BUNDLE_FRAGMENT_INDEX, index);
		fragment.setArguments(args);
		return fragment;
	}

	public UserBroadcastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadData(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_view, container,
				false);
		mListView = (ListView) view.findViewById(android.R.id.list);
		mAdapter = new BroadcastAdapter(inflater.getContext(), broadcastList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setEmptyView(view.findViewById(android.R.id.empty));
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public boolean isViewBeingDragged(MotionEvent event) {
		return mAbsListViewDelegate.isViewBeingDragged(event, mListView);
	}

	private void loadData(final boolean more) {
		SociabilityClient.getClient().getBroadcastByType(
				SociabilityClient.BROADCAST_TYPE_ID_ALL, 0, 10,
				new BaseCallback<JLBroadcast>() {
					@Override
					public void onResultCallback(int code, String message,
							JLBroadcast result) {
						if (code >= 0) {
							if (!more) {
								broadcastList.clear();
							}
							int total = result.data.recordCount;
							broadcastList.addAll(result.getCollectionData());
							mAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();
						}
					}
				}, JLBroadcast.class);
	}

}
