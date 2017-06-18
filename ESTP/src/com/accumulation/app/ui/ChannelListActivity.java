package com.accumulation.app.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.lib.ui.ViewHolder;

public class ChannelListActivity extends BaseActivity {

	ListView channels;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_channel_list;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "ÆµµÀ¹ã³¡";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		channels = ViewHolder.get(rootLayout, R.id.channels);
		channels.setAdapter(new ChannelAdapter());
	}

	class ChannelAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 7;
		}

		@Override
		public ChooseFile getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.item_channel_lsit, null);
			}
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getContext(),LikeQQActivity.class);
					startActivity(intent);
				}
			});
			
			return convertView;
		}
	}
}
