package com.accumulation.app.ui.fragment;

import java.io.Closeable;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.view.RefreshLayout;
import com.accumulation.app.view.RefreshLayout.DataCaculateListener;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JDUserProfile;
import com.accumulation.lib.ui.header.delegate.ScrollViewDelegate;

public class UserHomeFragment extends BaseViewPagerFragment {

	private ScrollView mScrollView;
	private ScrollViewDelegate mScrollViewDelegate = new ScrollViewDelegate();
	private TextView user_account;
	private TextView user_signature;

	public static UserHomeFragment newInstance(String id, String email) {
		UserHomeFragment fragment = new UserHomeFragment();
		Bundle args = new Bundle();
		args.putString("id", id);
		args.putString("email", email);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		loadUserProfile();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_scroll_view, container,
				false);
		mScrollView = (ScrollView) view.findViewById(R.id.scrollview);
		user_account = (TextView) view.findViewById(R.id.user_account);
		user_signature = (TextView) view.findViewById(R.id.user_signature);
		return view;
	}

	@Override
	public boolean isViewBeingDragged(MotionEvent event) {
		return mScrollViewDelegate.isViewBeingDragged(event, mScrollView);
	}

	public void closeSilently(Closeable c) {
		if (c == null) {
			return;
		}
		try {
			c.close();
		} catch (Throwable t) {
			// do nothing
		}
	}

	public void loadUserProfile() {
		String id = getArguments().getString("id");
		String email = getArguments().getString("email");

		SociabilityClient.getClient().getUserProfile(id, email,
				new BaseCallback<JDUserProfile>() {

					@Override
					public void onResultCallback(int code, String message,
							JDUserProfile result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							user_account.setText(result.data.Email);
							user_signature.setText(result.data.Intro);
						}
					}
				}, JDUserProfile.class);
	}
}
