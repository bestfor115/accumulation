package com.accumulation.app.ui.user;

import android.view.View;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.refresh.ptr.PtrClassicFrameLayout;
import com.accumulation.lib.refresh.ptr.PtrDefaultHandler;
import com.accumulation.lib.refresh.ptr.PtrFrameLayout;
import com.accumulation.lib.refresh.ptr.PtrHandler;
import com.accumulation.lib.refresh.ptr.header.StoreHouseHeader;
import com.accumulation.lib.ui.ViewHolder;

public class MyFollowActivity extends BaseActivity {
	PtrClassicFrameLayout ptrFrameLayout;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_test;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		ptrFrameLayout = ViewHolder.get(rootLayout,
				R.id.rotate_header_web_view_frame);

//		StoreHouseHeader header = new StoreHouseHeader(this);
//		header.initWithString("Ultra PTR");

		ptrFrameLayout.setDurationToCloseHeader(1500);
		ptrFrameLayout.setPullToRefresh(false);
//		ptrFrameLayout.setHeaderView(header);
//		ptrFrameLayout.addPtrUIHandler(header);
		ptrFrameLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
					View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						content, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				ptrFrameLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						ptrFrameLayout.refreshComplete();
					}
				}, 1500);
			}
		});
	}

}
