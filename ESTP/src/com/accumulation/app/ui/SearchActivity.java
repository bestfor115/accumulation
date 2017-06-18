package com.accumulation.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.view.OnItemClick;
import com.accumulation.app.view.SearchTipsGroupView;
import com.accumulation.app.view.SearchTipsGroupView.TipGridAdapter;

public class SearchActivity extends BaseActivity implements OnItemClick {
	private String items[] = { "视频", "么么哒", "动画", "音乐", "猜你喜欢", "最近热门", "影院",
			"游戏", "好得多" };

	private SearchTipsGroupView view;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_search;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		view = (SearchTipsGroupView) findViewById(R.id.search_tips);
		view.setAdapter(new SearchTIpAdapter());
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public void onClick(int position) {
		// TODO Auto-generated method stub

	}

	class SearchTIpAdapter extends TipGridAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_textview, null);
			TextView itemView =(TextView) convertView;
			itemView.setText(items[position]);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			return convertView;
		}

	}
}
