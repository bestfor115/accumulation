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
 * ���棺ѡ��̬�ķ��ͷ�Χ
 * */
public class ChooseTargetActivity extends BaseActivity {

	private String[] titles = new String[] { "�ҵ�Ⱥ��", "���� " };
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
		// ����Ĭ��ͼ��Ϊ����ʾ״̬
		expandableListView.setGroupIndicator(null);
		// Ϊ�б������Դ
		expandableListView.setAdapter(adapter);
		// ����һ��item����ļ�����
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// ˢ�½���
				expandStates[groupPosition] = !expandStates[groupPosition];
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				return false;
			}
		});

		// ���ö���item����ļ�����
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
		return "ѡ��Χ";
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
		// �ر�Activity
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
		// һ����ǩ�ϵ�logoͼƬ����Դ
		// һ����ǩ�ϵı�������Դ

		int[] group_state_array = new int[] { R.drawable.list_indecator_button,
				R.drawable.list_indecator_button_down };

		// ��дExpandableListAdapter�еĸ�������
		/**
		 * ��ȡһ����ǩ����
		 */
		@Override
		public int getGroupCount() {
			return titles.length;
		}

		/**
		 * ��ȡһ����ǩ����
		 */
		@Override
		public String getGroup(int groupPosition) {
			return titles[groupPosition];
		}

		/**
		 * ��ȡһ����ǩ��ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * ��ȡһ����ǩ�¶�����ǩ������
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
		 * ��ȡһ����ǩ�¶�����ǩ������
		 */
		@Override
		public Target getChild(int groupPosition, int childPosition) {
			switch (groupPosition) {
			case 0:
				return targets.get(childPosition);
				// case 1:
				// if (childPosition == 0) {
				// return "��λ";
				// } else {
				// return "�㳡";
				// }
			}
			return null;
		}

		/**
		 * ��ȡ������ǩ��ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * ָ��λ����Ӧ������ͼ
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * ��һ����ǩ��������
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// Ϊ��ͼ����ָ������
			convertView = (LinearLayout) LinearLayout.inflate(getBaseContext(),
					R.layout.group_layout, null);

			LinearLayout myLayout = (LinearLayout) convertView
					.findViewById(R.id.group_layout);

			/**
			 * ������ͼ��Ҫ��ʾ�Ŀؼ�
			 */
			// �½�һ��TextView����������ʾһ����ǩ�ϵı�����Ϣ
			TextView group_title = (TextView) convertView
					.findViewById(R.id.group_title);
			// �½�һ��TextView����������ʾһ����ǩ�ϵĴ�����������Ϣ
			ImageView group_state = (ImageView) convertView
					.findViewById(R.id.group_state);
			group_title.setText(titles[groupPosition]);
			group_state
					.setBackgroundResource(group_state_array[expandStates[groupPosition] ? 1
							: 0]);
			// ����һ�����ֶ���
			return convertView;
		}

		/**
		 * ��һ����ǩ�µĶ�����ǩ��������
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			// Ϊ��ͼ����ָ������
			convertView = (RelativeLayout) RelativeLayout.inflate(
					getBaseContext(), R.layout.child_layout, null);
			/**
			 * ������ͼ��Ҫ��ʾ�Ŀؼ�
			 */
			// �½�һ��TextView����������ʾ��������
			TextView child_text = (TextView) convertView
					.findViewById(R.id.child_text);

			ImageView child_check = (ImageView) convertView
					.findViewById(R.id.child_check);

			final Target entry = getChild(groupPosition, childPosition);
			// ����Ҫ��ʾ���ı���Ϣ
			if (entry != null) {
				child_text.setText(entry.Name);
			} else {
				if (childPosition == 0) {
					child_text.setText("����Ȧ");
				} else {
					child_text.setText("�㳡");
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
		 * ��ѡ���ӽڵ��ʱ�򣬵��ø÷���
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	};

}
