package com.accumulation.app.ui;

import android.widget.ListView;

import com.accumulation.app.R;
import com.accumulation.app.adapter.CampaignAdapter;
import com.accumulation.app.adapter.GroupAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.ui.ViewHolder;

public class CampaignListActivity extends BaseActivity {
	ListView rankList;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_campaign_list;
	}
	
	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "活动列表";
	}
	
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		rankList = ViewHolder.get(rootLayout, R.id.list);
		rankList.setAdapter(new CampaignAdapter(this));

	}

}
