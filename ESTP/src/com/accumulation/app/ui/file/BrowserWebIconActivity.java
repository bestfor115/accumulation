package com.accumulation.app.ui.file;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.util.HttpHelper;
import com.accumulation.app.util.ImageLoadOptions;
import com.accumulation.app.view.CustomViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 图片浏览
 * 
 * @ClassName: ImageBrowserActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-19 下午8:22:49
 */
public class BrowserWebIconActivity extends BaseActivity implements
		OnPageChangeListener {

	private CustomViewPager viewPager;
	private ImageBrowserAdapter adapter;
	LinearLayout imageLayout;
	private int position;
	private ArrayList<String> photos;
	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		photos =intent.getStringArrayListExtra("photos");
		position =intent.getIntExtra("position", 0);
	}
	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_show_webicon;
	}

	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		viewPager = (CustomViewPager) findViewById(R.id.pagerview);
		adapter = new ImageBrowserAdapter(this);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(position, false);
		viewPager.setOnPageChangeListener(this);
	}
	
	@Override
	public void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		String path=photos.get(viewPager.getCurrentItem());
		ImageLoader.getInstance().loadImage(path, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// TODO Auto-generated method stub
				toast("已成功保存到目录"+HttpHelper.saveBitmap(loadedImage));
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
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
			photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
				
				@Override
				public void onPhotoTap(View arg0, float arg1, float arg2) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			final String imgUrl = photos.get(position);
			String path = !imgUrl.startsWith("http") ? ("file://" + imgUrl)
					: imgUrl;
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
