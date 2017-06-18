package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.manager.BroadcastManager;
import com.accumulation.app.manager.SpannedManager;
import com.accumulation.app.view.RefreshLayout;
import com.accumulation.app.view.RefreshLayout.DataCaculateListener;
import com.accumulation.app.view.RefreshLayout.OnLoadListener;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.Comment;
import com.accumulation.lib.sociability.data.JDUserProfile;
import com.accumulation.lib.sociability.data.UserProfile;
import com.accumulation.lib.ui.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RewardActivity extends BaseActivity implements OnClickListener{
	private final List<Comment> comments = new ArrayList<Comment>();
	RefreshLayout refreshListView;
	TextView user_name;
	TextView user_dsn;
	ImageView user_avatar;
	ListView listView;
	View lastSelect;
	private String userId;
	private UserProfile data;
	private RewardAdapter adapter;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_reward;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "打赏作者";
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "怎么玩?";
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		userId = intent.getStringExtra("user");
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		loadUserProfile();
	}
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		listView = (ListView) findViewById(R.id.listview);
		listView.addHeaderView(LayoutInflater.from(this).inflate(
				R.layout.include_reward_head, listView, false));
		user_name=(TextView) findViewById(R.id.user_name);
		user_dsn=(TextView) findViewById(R.id.user_dsn);
		user_avatar=(ImageView) findViewById(R.id.user_avatar);
		findViewById(R.id.reward_1).setOnClickListener(this);
		findViewById(R.id.reward_10).setOnClickListener(this);
		findViewById(R.id.reward_50).setOnClickListener(this);
		findViewById(R.id.reward_100).setOnClickListener(this);
		listView.setAdapter(adapter = new RewardAdapter(this, comments));

		// 获取RefreshLayout实例
		refreshListView = (RefreshLayout) findViewById(R.id.swipe_layout);
		// 设置下拉刷新时的颜色值,颜色值需要定义在xml中
		refreshListView.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// 设置下拉刷新监听器
		refreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshListView.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						refreshListView.setRefreshing(false);
					}
				}, 500);
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
		refreshListView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void loadUserProfile() {
		SociabilityClient.getClient().getUserProfile(userId, null,
				new BaseCallback<JDUserProfile>() {

					@Override
					public void onResultCallback(int code, String message,
							JDUserProfile result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							data = result.data;
							user_dsn.setText(result.data.Intro);
							user_name.setText(result.data.Name);
							AccumulationAPP.getInstance().loadImage(
									result.data.Avatar, user_avatar);
						}
					}
				}, JDUserProfile.class);
	}
	@SuppressLint("NewApi")
	public class RewardAdapter extends BaseAdapter {
		private List<Comment> datas;
		private Context cxt;

		public RewardAdapter(Context cxt, List<Comment> datas) {
			this.cxt = cxt;
			this.datas = datas;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(cxt).inflate(
						R.layout.item_broadcast_comment, null);
			}
			ImageView imageAvatar = (ImageView) convertView
					.findViewById(R.id.broadcast_comment_item_ImageAvatar);
			TextView textUserName = (TextView) convertView
					.findViewById(R.id.broadcast_comment_item_TextUserName);
			TextView textPublishTime = (TextView) convertView
					.findViewById(R.id.broadcast_comment_item_TextPublishTime);
			TextView textContent = (TextView) convertView
					.findViewById(R.id.broadcast_comment_item_TextContent);
			View line = convertView.findViewById(R.id.line);
			line.setVisibility(position == getCount() - 1 ? View.INVISIBLE
					: View.VISIBLE);
			final Comment entry = (Comment) getItem(position);
			LinearLayout image_container = ViewHolder.get(convertView,
					R.id.image_container);
			LinearLayout zip_container = ViewHolder.get(convertView,
					R.id.zip_container);
			BroadcastManager.showImageInContainer(entry.ImageList,
					image_container, cxt);
			BroadcastManager.showZipInContainer(entry.content, zip_container,
					cxt);
			imageAvatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				}
			});
			ImageLoader.getInstance().displayImage(entry.headPic, imageAvatar);
			textUserName.setText(SpannedManager.changeUserName(entry.sendBy));
			textPublishTime.setText(entry.sendTime);
			SpannedManager.changeRawString(textContent, entry.content, null,
					true);
			textContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				}
			});
			convertView.setTag(entry);
			return convertView;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(lastSelect!=null){
			ImageView image=(ImageView) lastSelect.findViewWithTag("check");
			image.setImageResource(R.drawable.mini_check_red);
		}
		lastSelect=v;
		ImageView select=(ImageView) v.findViewWithTag("check");
		select.setImageResource(R.drawable.mini_check_selected);
	}

}
