package com.accumulation.app.ui.subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.base.DialogCallback;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JASubject;
import com.accumulation.lib.sociability.data.Subject;
import com.accumulation.lib.ui.ViewHolder;
import com.accumulation.lib.ui.tree.AndroidTreeView;
import com.accumulation.lib.ui.tree.model.TreeNode;
import com.accumulation.lib.ui.tree.model.TreeNode.TreeNodeClickListener;

public class ChooseSubjectActivity extends BaseActivity {
	LinearLayout listContainer;
	AndroidTreeView list;
	TreeNode root;
	private List<Subject> datas = new ArrayList<Subject>();
	private List<Subject> choose = new ArrayList<Subject>();

	private HashMap<Subject, List<Subject>> mRootMaps = new HashMap<Subject, List<Subject>>();
	private HashMap<Subject, List<Subject>> mSubMaps = new HashMap<Subject, List<Subject>>();

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_choose_subject;
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		Serializable s = intent.getSerializableExtra("data");
		if (s != null) {
			List<Subject> list = (List<Subject>) s;
			choose.addAll(list);
		}
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		loadData();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		listContainer = ViewHolder.get(rootLayout, R.id.list_container);
		initChooseList();
	}

	private void checkValidState() {
		commitView.setEnabled(list.getSelected().size() > 0);
	}

	private void initChooseList() {
		root = TreeNode.root();
		list = new AndroidTreeView(getContext(), root);
		list.setSelectionModeEnabled(true);
		listContainer.addView(list.getView());
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "选择分类";
	}

	@Override
	public void onCommit() {
		// TODO Auto-generated method stub
		super.onCommit();
		if (list.getSelected().isEmpty()) {
			toast("请选择目录");
		} else {
			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("data",
					(Serializable) list.getSelectedValues(Subject.class));
			intent.putExtras(mBundle);
			this.setResult(RESULT_OK, intent);
			this.finish();
		}
	}

	private void loadData() {
		SociabilityClient.getClient().getAllSubject(
				new DialogCallback<JASubject>(this) {
					@Override
					public void onResultCallback(int code, String message,
							JASubject result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							datas.clear();
							datas.addAll(result.data);
							fillSubjectData();
							initTreeNodes();
						} else {
							toast(message);
						}
					}
				}, JASubject.class);
	}

	public class DestinationViewHolder extends
			TreeNode.BaseNodeViewHolder<Subject> {
		ImageView group_expand;
		ImageView group_state;

		public DestinationViewHolder(Context context) {
			super(context);
		}

		@Override
		public View createNodeView(TreeNode node, Subject value) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			final View view = inflater.inflate(
					R.layout.item_choose_destination, null, false);
			TextView tv = (TextView) view.findViewById(R.id.group_title);
			ImageView icon = (ImageView) view.findViewById(R.id.group_icon);
			group_expand = (ImageView) view.findViewById(R.id.group_expand);
			group_state = (ImageView) view.findViewById(R.id.group_state);
			group_state.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					List<TreeNode> selectes = list.getSelected();
					if (selectes.contains(mNode)) {
						list.selectNode(mNode, false);
					} else {
						list.selectNode(mNode, true);
					}
					checkValidState();
				}
			});
			mNode.setClickListener(new TreeNodeClickListener() {

				@Override
				public void onClick(TreeNode node, Object value) {
					// TODO Auto-generated method stub

				}
			});
			if (value == null) {
				tv.setText("根目录");
			} else {
				tv.setText(value.Name);
			}
			AccumulationAPP.getInstance().loadImage(value.IcoPath, icon);
			view.setPadding(PixelUtil.dp2px(10) * mNode.getLevel(), 0, 0, 0);
			if (mNode.isLeaf()) {
				group_expand.setImageDrawable(new ColorDrawable(0));
			} else {
				group_expand
						.setImageResource(R.drawable.skin_indicator_unexpanded);
			}
			if (list.getSelected().contains(mNode)) {
				group_state.setImageResource(R.drawable.mini_check_selected);
			} else {
				group_state.setImageResource(R.drawable.mini_check_red);
			}
			return view;
		}

		@Override
		public void toggle(boolean active) {
			if (mNode.isLeaf()) {
				group_expand.setImageDrawable(new ColorDrawable(0));
			} else {
				group_expand
						.setImageResource(!active ? R.drawable.skin_indicator_unexpanded
								: R.drawable.skin_indicator_expanded);
			}

		}

		@Override
		public void toggleSelectionMode(boolean editModeEnabled) {
			// TODO Auto-generated method stub
			// group_state
			// .setImageResource(editModeEnabled ?
			// R.drawable.mini_check_selected
			// : R.drawable.mini_check_red);
		}

		@Override
		public void toggleSelected(boolean selected) {
			// TODO Auto-generated method stub
			if (group_state != null) {
				group_state
						.setImageResource(selected ? R.drawable.mini_check_selected
								: R.drawable.mini_check_red);
			}
		}
	}

	private void fillSubjectData() {
		mRootMaps.clear();
		int N = datas.size();
		boolean[] flags = new boolean[N];
		for (int i = 0; i < N; i++) {
			Subject entry = datas.get(i);
			if (entry.ParentIdList == null || entry.ParentIdList.length == 0) {
				mRootMaps.put(entry, new ArrayList<Subject>());
				flags[i] = true;
			}
		}

		for (int i = 0; i < N; i++) {
			Subject entry = datas.get(i);
			if (!flags[i] && entry.ParentIdList != null
					&& entry.ParentIdList.length > 0) {
				Subject parent = getSubjectByID(entry.ParentIdList[0]);
				if (parent != null
						&& (parent.ParentIdList == null || parent.ParentIdList.length == 0)) {
					mRootMaps.get(parent).add(entry);
					flags[i] = true;
				}
			}
		}

		for (int i = 0; i < N; i++) {
			Subject entry = datas.get(i);
			if (!flags[i] && entry.ParentIdList != null
					&& entry.ParentIdList.length > 0) {
				Subject parent = getSubjectByID(entry.ParentIdList[0]);
				if (parent != null && parent.ParentIdList != null
						&& parent.ParentIdList.length > 0) {
					List<Subject> mSubList = mSubMaps.get(parent);
					if (mSubList == null) {
						mSubMaps.put(parent, new ArrayList<Subject>());
					}
					mSubMaps.get(parent).add(entry);
					flags[i] = true;
				}
			}
		}
	}

	private Subject getSubjectByID(String id) {
		int N = datas.size();
		for (int i = 0; i < N; i++) {
			Subject entry = datas.get(i);
			if (entry != null && id.equals(entry.Id)) {
				return entry;
			}
		}
		return null;
	}

	private void initTreeNodes() {
		list.selectNode(root, true);
		list.deselectAll();
		Iterator<Map.Entry<Subject, List<Subject>>> it = mRootMaps.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<Subject, List<Subject>> entry = it.next();
			Subject key = entry.getKey();
			TreeNode first = new TreeNode(key)
					.setViewHolder(new DestinationViewHolder(getContext()));
			list.addNode(root, first);
			List<Subject> value = entry.getValue();
			int N = value == null ? 0 : value.size();
			for (int i = 0; i < N; i++) {
				Subject key1 = value.get(i);
				TreeNode second = new TreeNode(key1)
						.setViewHolder(new DestinationViewHolder(getContext()));
				list.addNode(first, second);
				List<Subject> value1 = mSubMaps.get(key1);
				int M = value1 == null ? 0 : value1.size();
				for (int j = 0; j < M; j++) {
					Subject key2 = value1.get(j);
					TreeNode third = new TreeNode(key2)
							.setViewHolder(new DestinationViewHolder(
									getContext()));
					list.addNode(second, third);
				}
			}
		}
		list.caculateChoose(choose);
		list.collapseAll();
		checkValidState();
	}

}
