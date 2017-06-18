package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.base.DialogCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JATarget;
import com.accumulation.lib.sociability.data.Target;

/**
 * 界面：选择动态的发送范围
 * */
public class ChooseTargetActivity extends BaseActivity {

	private String[] titles = new String[] { "我的群组", "其他 " };
	private boolean[] expandStates = new boolean[titles.length];
	boolean toAll = true;
	boolean toCompany = false;
	private ArrayList<Target> chooses = new ArrayList<Target>();
	private ArrayList<Target> targets = new ArrayList<Target>();
	ExpandableListView expandableListView;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_mult_select;
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		toAll = intent.getBooleanExtra("toAll", true);
		toCompany = intent.getBooleanExtra("toCompany", false);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		expandableListView = (ExpandableListView) findViewById(R.id.list);
		// 设置默认图标为不显示状态
		expandableListView.setGroupIndicator(null);
		// 为列表绑定数据源
		expandableListView.setAdapter(adapter);
		// 设置一级item点击的监听器
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// 刷新界面
				expandStates[groupPosition] = !expandStates[groupPosition];
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				return false;
			}
		});

		// 设置二级item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				if (groupPosition == 0) {
					Target entry = (Target) v.getTag();
					if (chooses.contains(entry)) {
						chooses.remove(entry);
					} else {
						chooses.add(entry);
					}
				} else {
					if (childPosition == 1) {
						toAll = !toAll;
						toCompany = false;
					} else {
						toCompany = !toCompany;
						toAll = false;
					}
				}
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				checkValidState();
				return false;
			}
		});

		checkValidState();
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		SociabilityClient.getClient().getBroadcastTarget(
				new DialogCallback<JATarget>(this) {

					@Override
					public void onResultCallback(int code, String message,
							JATarget result) {
						// TODO Auto-generated method stub
						if (code < 0) {
							toast(message);
						} else {
							targets.addAll(result.getCollectionData());
							ArrayList<Target> selects = (ArrayList<Target>) getIntent()
									.getSerializableExtra("data");
							chooses.clear();
							chooses.addAll(selects);
							int groupCount = titles.length;
							for (int i = 0; i < groupCount; i++) {
								expandStates[i] = true;
								expandableListView.expandGroup(i);
							}
						}
					}
				}, JATarget.class);
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "选择范围";
	}

	@Override
	public void onCommit() {
		// TODO Auto-generated method stub
		super.onCommit();
		Intent intent = new Intent();
		intent.putExtra("toAll", toAll);
		intent.putExtra("toCompany", toCompany);
		intent.putExtra("chooseGroup", chooses);
		this.setResult(RESULT_OK, intent);
		// 关闭Activity
		this.finish();
	}

	private void checkValidState() {
		if (toAll || toCompany || chooses.size() > 0) {
			commitView.setEnabled(true);
		} else {
			commitView.setEnabled(false);
		}
	}

	final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
		// 一级标签上的logo图片数据源
		// 一级标签上的标题数据源

		int[] group_state_array = new int[] { R.drawable.list_indecator_button,
				R.drawable.list_indecator_button_down };

		// 重写ExpandableListAdapter中的各个方法
		/**
		 * 获取一级标签总数
		 */
		@Override
		public int getGroupCount() {
			return titles.length;
		}

		/**
		 * 获取一级标签内容
		 */
		@Override
		public String getGroup(int groupPosition) {
			return titles[groupPosition];
		}

		/**
		 * 获取一级标签的ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 获取一级标签下二级标签的总数
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			switch (groupPosition) {
			case 0:
				return targets.size() - 2;
			case 1:
				return 2;
			}
			return 0;
		}

		/**
		 * 获取一级标签下二级标签的内容
		 */
		@Override
		public Target getChild(int groupPosition, int childPosition) {
			switch (groupPosition) {
			case 0:
				return targets.get(childPosition);
				// case 1:
				// if (childPosition == 0) {
				// return "单位";
				// } else {
				// return "广场";
				// }
			}
			return null;
		}

		/**
		 * 获取二级标签的ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * 指定位置相应的组视图
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * 对一级标签进行设置
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 为视图对象指定布局
			convertView = (LinearLayout) LinearLayout.inflate(getBaseContext(),
					R.layout.group_layout, null);

			LinearLayout myLayout = (LinearLayout) convertView
					.findViewById(R.id.group_layout);

			/**
			 * 声明视图上要显示的控件
			 */
			// 新建一个TextView对象，用来显示一级标签上的标题信息
			TextView group_title = (TextView) convertView
					.findViewById(R.id.group_title);
			// 新建一个TextView对象，用来显示一级标签上的大体描述的信息
			ImageView group_state = (ImageView) convertView
					.findViewById(R.id.group_state);
			group_title.setText(titles[groupPosition]);
			group_state
					.setBackgroundResource(group_state_array[expandStates[groupPosition] ? 1
							: 0]);
			// 返回一个布局对象
			return convertView;
		}

		/**
		 * 对一级标签下的二级标签进行设置
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			// 为视图对象指定布局
			convertView = (RelativeLayout) RelativeLayout.inflate(
					getBaseContext(), R.layout.child_layout, null);
			/**
			 * 声明视图上要显示的控件
			 */
			// 新建一个TextView对象，用来显示具体内容
			TextView child_text = (TextView) convertView
					.findViewById(R.id.child_text);

			ImageView child_check = (ImageView) convertView
					.findViewById(R.id.child_check);

			final Target entry = getChild(groupPosition, childPosition);
			// 设置要显示的文本信息
			if (entry != null) {
				child_text.setText(entry.Name);
			} else {
				if (childPosition == 0) {
					child_text.setText("好友圈");
				} else {
					child_text.setText("广场");
				}
			}
			boolean state = false;
			if (groupPosition == 0) {
				state = chooses.contains(targets.get(childPosition));
				convertView.setTag(targets.get(childPosition));
			} else {
				if (childPosition == 0) {
					state = toCompany;
				} else {
					state = toAll;
				}
			}
			child_check.setImageResource(state ? R.drawable.mini_check_selected
					: R.drawable.mini_check_red);
			return convertView;
		}

		/**
		 * 当选择子节点的时候，调用该方法
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	};

}
