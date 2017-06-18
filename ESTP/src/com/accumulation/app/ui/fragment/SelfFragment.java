package com.accumulation.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.data.UserProfile;
import com.accumulation.lib.ui.ViewHolder;

public class SelfFragment extends Fragment {

	private ImageView user_avatar;
	private TextView user_name;
	private static UserProfile data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final ViewGroup view = (ViewGroup) inflater.inflate(
				R.layout.fragment_self, container, false);
		user_avatar = ViewHolder.get(view, R.id.user_avatar);
		user_name = ViewHolder.get(view, R.id.user_name);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		loadData();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		caculateShow();
	}

	private void loadData() {
//		ESTPRequestBuilder requestBuilder = new ESTPRequestBuilder();
//		requestBuilder.setURL(URIConfig.SELF_PROFILE_URL);
//		RequestParams param = new RequestParams();
//		requestBuilder.setParams(param);
//		ESTPResponseBuilder<JDUserProfile> responseBuilder = new ESTPResponseBuilder<JDUserProfile>();
//		responseBuilder.setResponseResultHandlerT(
//				new ResponseHandlerT<JDUserProfile>() {
//					@Override
//					public void onResponse(boolean success, JDUserProfile result) {
//
//						if (result != null && success && result.isSuccess()) {
//							data = result.data;
//							caculateShow();
//						} 
//					}
//				}, JDUserProfile.class);
//		AccumulationAPP.getInstance().requestNetData(requestBuilder.create(),
//				responseBuilder.create());
	}

	private void caculateShow() {
		if (data == null) {
			return;
		}
		AccumulationAPP.getInstance().loadImage(data.Avatar, user_avatar);
		user_name.setText(data.Name + " (" + data.Title + " )");
	}
}
