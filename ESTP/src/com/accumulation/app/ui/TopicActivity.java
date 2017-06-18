package com.accumulation.app.ui;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.util.UIUtils;
import com.accumulation.lib.tool.base.IOUtils;
import com.accumulation.lib.ui.filmloader.FillableLoader;
import com.accumulation.lib.ui.filmloader.FillableLoaderBuilder;
import com.accumulation.lib.ui.filmloader.clippingtransforms.WavesClippingTransform;

public class TopicActivity extends BaseActivity {
	FillableLoader fillableLoader;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_topic;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		createAnimPath();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		fillableLoader.reset();
//
//		// We wait a little bit to start the animation, to not contaminate the
//		// drawing effect
//		// by the activity creation animation.
//		fillableLoader.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				fillableLoader.start();
//			}
//		}, 250);
		UIUtils.createLoadingDialog(this, true).show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void createAnimPath() {
		int viewSize =PixelUtil.dp2px(200);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				viewSize, viewSize);
		params.gravity = Gravity.CENTER;
		String path = IOUtils.loadAssetText(this, "anim_path.txt");
		FillableLoaderBuilder loaderBuilder = new FillableLoaderBuilder();
		fillableLoader = loaderBuilder.parentView((FrameLayout) rootLayout)
				.svgPath(path).layoutParams(params)
				.originalDimensions(250, 100)
				.strokeColor(Color.parseColor("#1c9ade"))
				.fillColor(Color.parseColor("#FF3300"))
				.strokeDrawingDuration(2000)
				.clippingTransform(new WavesClippingTransform())
				.fillDuration(10000).build();
	}
}
