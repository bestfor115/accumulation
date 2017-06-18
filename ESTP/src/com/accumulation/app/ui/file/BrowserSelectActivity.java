package com.accumulation.app.ui.file;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.util.FileUtils;
import com.accumulation.app.util.ImageLoadOptions;
import com.accumulation.app.view.CustomViewPager;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

/**
 * 图片浏览
 * 
 * @ClassName: ImageBrowserActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-19 下午8:22:49
 */
public class BrowserSelectActivity extends BaseActivity implements
		OnPageChangeListener {
	public static final int ACTION_REQUEST_EDITIMAGE = 9;

	private CustomViewPager viewPager;
	private ImageBrowserAdapter adapter;
	private LinearLayout imageLayout;
	private View operate_layout;
	private int position;
	private ArrayList<ChooseFile> photos;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_brower_select;
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "编辑";
	}

	@Override
	protected int getStatusBarResId() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return position + 1 + " / " + photos.size();
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		photos = (ArrayList<ChooseFile>) intent.getSerializableExtra("photos");
		position = intent.getIntExtra("position", 0);
	}

	@Override
	protected void initView() {
		viewPager = (CustomViewPager) findViewById(R.id.pagerview);
		operate_layout = findViewById(R.id.operate_layout);
		viewPager.setAdapter(adapter = new ImageBrowserAdapter(this));
		viewPager.setCurrentItem(position, false);
		viewPager.setOnPageChangeListener(this);
		viewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				View currentView = viewPager.findViewWithTag(viewPager
						.getCurrentItem());

				if (currentView != null) {
					PhotoView photo = (PhotoView) currentView
							.findViewById(R.id.photoview);
					return false;
					// return photo.getScale()!=1f;
				}
				return false;
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
		caculateTitle();
	}

	@Override
	protected void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		if (photos.size() > 0) {
			String path = photos.get(position).path;
			Intent it = new Intent(this, EditImageActivity.class);
			it.putExtra(EditImageActivity.FILE_PATH, path);
			File outputFile = FileUtils.getEmptyFile("tietu"
					+ System.currentTimeMillis() + ".jpg");
			it.putExtra(EditImageActivity.EXTRA_OUTPUT,
					outputFile.getAbsolutePath());
			startActivityForResult(it, ACTION_REQUEST_EDITIMAGE);
		}

		// photos.remove(position--);
		// if (photos.size() == 0) {
		// Intent intent = new Intent();
		// // 把返回数据存入Intent
		// intent.putExtra("result", photos);
		// // 设置返回数据
		// setResult(RESULT_OK, intent);
		// finish();
		// } else {
		// position = Math.min(photos.size(), position);
		// position = Math.max(0, position);
		// viewPager.setAdapter(adapter = new ImageBrowserAdapter(this));
		// viewPager.setCurrentItem(position, false);
		// caculateTitle();
		// }
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putExtra("result", photos);
		// 设置返回数据
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onBack() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		// 把返回数据存入Intent
		intent.putExtra("result", photos);
		// 设置返回数据
		setResult(RESULT_OK, intent);
		finish();
	}

	private void caculateTitle() {
		titleView.setText(position + 1 + " / " + photos.size());
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
					if (ViewHelper.getAlpha(toolbar) != 0) {
						ViewCompat.animate(toolbar).alpha(0.0f)
								.setDuration(300).start();
						ViewCompat.animate(operate_layout).alpha(0.0f)
								.setDuration(300).start();
					} else {
						ViewCompat.animate(toolbar).alpha(1.0f)
								.setDuration(300).start();
						ViewCompat.animate(operate_layout).alpha(1.0f)
								.setDuration(300).start();
					}
				}
			});
			final String imgUrl = photos.get(position).path;
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
			imageLayout.setTag(position);
			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
		
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // System.out.println("RESULT_OK");
            switch (requestCode) {
                case ACTION_REQUEST_EDITIMAGE://
                    String newFilePath = data.getStringExtra("save_file_path");
                    photos.get(position).path=newFilePath;
    				View currentView = viewPager.findViewWithTag(viewPager
    						.getCurrentItem());
    				if (currentView != null) {
    					PhotoView photo = (PhotoView) currentView
    							.findViewById(R.id.photoview);
    					String path = !newFilePath.startsWith("http") ? ("file://" + newFilePath)
    							: newFilePath;
    					AccumulationAPP.getInstance().loadImage(path, photo);
    				}
                    break;
            }// end switch
        }
    }

}
