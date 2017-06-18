package com.accumulation.app.ui;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.accumulation.app.R;
import com.accumulation.app.adapter.RankAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.tool.base.IOUtils;
import com.accumulation.lib.ui.ViewHolder;
import com.accumulation.lib.ui.filmloader.FillableLoader;
import com.accumulation.lib.ui.filmloader.FillableLoaderBuilder;
import com.accumulation.lib.ui.filmloader.Paths;
import com.accumulation.lib.ui.filmloader.clippingtransforms.WavesClippingTransform;

public class RankActivity extends BaseActivity {

	public static final int RANK_TYPE_HOT = 0;
	public static final int RANK_TYPE_WEALTH = 1;
	public static final int RANK_TYPE_COMTRIBUTION = 2;

	ListView rankList;
	Button rank_hot, rank_wealth, rank_contribution;


	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_rank;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "≈≈––∞Ò";
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "∞Ô÷˙";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		rankList = ViewHolder.get(rootLayout, R.id.rank_list);
		rank_hot = ViewHolder.get(rootLayout, R.id.rank_hot);
		rank_contribution = ViewHolder.get(rootLayout, R.id.rank_contribution);
		rank_wealth = ViewHolder.get(rootLayout, R.id.rank_wealth);

		rank_hot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rank_contribution.setSelected(false);
				rank_wealth.setSelected(false);
				rank_hot.setSelected(true);
				loadData(RANK_TYPE_HOT);
			}
		});
		rank_contribution.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rank_wealth.setSelected(false);
				rank_contribution.setSelected(true);
				rank_hot.setSelected(false);
				loadData(RANK_TYPE_COMTRIBUTION);
			}
		});
		rank_wealth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rank_wealth.setSelected(true);
				rank_contribution.setSelected(false);
				rank_hot.setSelected(false);

				loadData(RANK_TYPE_WEALTH);
			}

		});
		rank_hot.setSelected(true);
		rankList.setAdapter(new RankAdapter(this, null));
	}

	private void loadData(int rankTypeWealth) {
		// TODO Auto-generated method stub

	}

	

}
