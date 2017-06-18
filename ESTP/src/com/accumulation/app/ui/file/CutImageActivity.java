package com.accumulation.app.ui.file;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.view.ClipSquareImageView;

public class CutImageActivity extends BaseActivity {
	private ClipSquareImageView imageView;
	String path;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_cut_image;
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		path = intent.getStringExtra("file");
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "剪切";
	}

	@Override
	protected void onCommit() {
		// TODO Auto-generated method stub
		super.onCommit();
		Bitmap bitmap = imageView.clip();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] bitmapByte = baos.toByteArray();
		Intent intent = new Intent();
		intent.putExtra("bitmap", bitmapByte);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		String url = "file://" + path;
		imageView = (ClipSquareImageView) findViewById(R.id.clipSquareIV);
		imageView.setImageBitmap(zoomImage(path,
				AccumulationAPP.getInstance().width));
		// ImageLoader.getInstance().displayImage(url, imageView);
	}

	private static Bitmap zoomImage(String path, int maxWidth) {
		// 获取这个图片的宽和高
		int[] WH = AccumulationAPP.getInstance().getImageWH(path);
		float scale = maxWidth > 0 ? (maxWidth + 0f) / WH[0] : 0f;
		int wRatio = (int) Math.ceil(WH[0] / (float) maxWidth); // 图片是宽度的几倍
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = wRatio;
		return BitmapFactory.decodeFile(path, opts);
	}
}
