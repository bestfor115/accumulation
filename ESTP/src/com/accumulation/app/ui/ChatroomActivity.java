package com.accumulation.app.ui;

import android.widget.ListView;

import com.accumulation.app.R;
import com.accumulation.app.adapter.GroupAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.ui.ViewHolder;

public class ChatroomActivity extends BaseActivity {
	ListView rankList;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_chatroom;
	}
	
	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "聊天室";
	}
	
	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "新建群";
	}
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		rankList = ViewHolder.get(rootLayout, R.id.group_list);
		rankList.setAdapter(new GroupAdapter(this));

	}

}
