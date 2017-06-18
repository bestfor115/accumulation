package com.accumulation.app.ui.file;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.util.ImageLoadOptions;
import com.accumulation.app.view.CustomViewPager;
import com.accumulation.lib.tool.debug.Logger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 图片浏览
 * 
 * @ClassName: ImageBrowserActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-19 下午8:22:49
 */
public class previewImagerActivity extends BaseActivity implements
		OnPageChangeListener {
	private CustomViewPager viewPager;
	private ImageBrowserAdapter adapter;
	private int position;
	private int max;
	private ArrayList<ChooseFile> selects;
	private ArrayList<ChooseFile> photos;
	private View check;
	private TextView select_count;
	private ImageView select_state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_showpicture;
	}

	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		photos = (ArrayList<ChooseFile>) intent.getSerializableExtra("photos");
		selects = (ArrayList<ChooseFile>) intent
				.getSerializableExtra("seleccts");
		position = intent.getIntExtra("position", 0);
		max = intent.getIntExtra("max", 0);

	}

	@Override
	protected void initView() {
		viewPager = (CustomViewPager) findViewById(R.id.pagerview);
		adapter = new ImageBrowserAdapter(this);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(position, false);
		viewPager.setOnPageChangeListener(this);
		check =  findViewById(R.id.select);
		select_count=(TextView) findViewById(R.id.select_count);
		select_state=(ImageView) findViewById(R.id.select_state);

		check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!selects.contains(photos.get(position))){
					if (max>=0&&selects.size() >=max) {
						toast(R.string.msg_choose_pic_count);
						return;
					}
					selects.add(photos.get(position));
				}else{
					selects.remove(photos.get(position));
				}
				caculateCurrentState();
			}
		});
		caculateCurrentState();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		position = arg0;
		caculateCurrentState();
	}

	@Override
	protected void onBack() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putExtra("result", selects);
		// 设置返回数据
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putExtra("result", selects);
		// 设置返回数据
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	protected void onCommit() {
		// TODO Auto-generated method stub
		super.onCommit();
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putExtra("result", selects);
		intent.putExtra("commit", true);
		// 设置返回数据
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void caculateCurrentState() {
		titleView.setText(position + 1 + " / " + photos.size());
		if(max>=0){
			select_count.setText("已选 ( "+selects.size()+" / "+max+" )");
		}else{
			select_count.setText("已选 ( "+selects.size()+" )");
		}
		select_state.setImageResource(selects.contains(photos.get(position))?R.drawable.checkbox_white_selected:R.drawable.checkbox_white_normal);
	}

	private class ImageBrowserAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		public ImageBrowserAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// TODO Auto-generated method stub
			return view == object;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			View imageLayout = inflater.inflate(R.layout.item_show_picture,
					container, false);
			final PhotoView photoView = (PhotoView) imageLayout
					.findViewById(R.id.photoview);
			final ProgressBar progress = (ProgressBar) imageLayout
					.findViewById(R.id.progress);

			final ChooseFile imgUrl = photos.get(position);
			String path = !imgUrl.path.startsWith("http") ? ("file://" + imgUrl.path)
					: imgUrl.path;
			ImageLoader.getInstance().displayImage(path, photoView,
					ImageLoadOptions.getOptions(),
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							// TODO Auto-generated method stub
							progress.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// TODO Auto-generated method stub
							progress.setVisibility(View.GONE);

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							progress.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							// TODO Auto-generated method stub
							progress.setVisibility(View.GONE);

						}
					});

			container.addView(imageLayout, 0);

			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

}
