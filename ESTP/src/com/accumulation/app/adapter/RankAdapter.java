package com.accumulation.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.data.UserProfile;
import com.accumulation.lib.ui.ViewHolder;

public class RankAdapter extends BaseAdapter {

	private List<UserProfile> datas;
	private Context context;

	public RankAdapter(Context context, List<UserProfile> users) {
		this.context = context;
		this.datas = users;
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_rank, null);
		}
		TextView rank_number = ViewHolder.get(convertView, R.id.rank_number);
		rank_number.setText(position+1 + "");
		switch (position) {
		case 0:
			rank_number.setBackgroundResource(R.drawable.rank_number_first);
			break;
		case 1:
			rank_number.setBackgroundResource(R.drawable.rank_number_second);
			break;
		case 2:
			rank_number.setBackgroundResource(R.drawable.rank_number_third);
			break;
		default:
			rank_number.setBackgroundResource(R.drawable.rank_number_normal);
			break;
		}
		return convertView;
	}

}
