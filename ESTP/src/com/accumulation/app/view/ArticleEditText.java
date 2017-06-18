package com.accumulation.app.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.accumulation.app.R;
import com.accumulation.app.config.Config;
import com.accumulation.app.util.ImageLoadOptions;
import com.accumulation.lib.tool.debug.Logger;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ArticleEditText extends EditText {

	private Context mContext;

	private Editable mEditable;

	public ArticleEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public ArticleEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public ArticleEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	/**
	 * ���һ��ͼƬ
	 * 
	 * @param bitmap
	 * @param uri
	 */
	public void addImage(Bitmap bitmap, String filePath) {
		Log.i("imgpath", filePath);
		String pathTag = "<img src=\"" + filePath + "\" />";
		SpannableString spanString = new SpannableString(pathTag);
		// ��ȡ��Ļ�Ŀ��
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int bmWidth = bitmap.getWidth();// ͼƬ�߶�
		int bmHeight = bitmap.getHeight();// ͼƬ���
		int zoomWidth = getWidth() - (paddingLeft + paddingRight);
		int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight);
		Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
		ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
		spanString.setSpan(imgSpan, 0, pathTag.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (mEditable == null) {
			mEditable = this.getText(); // ��ȡedittext����
		}
		int start = this.getSelectionStart(); // ��������ӵ�λ��
		mEditable.insert(start, spanString); // ����spanStringҪ��ӵ�λ��
		this.setText(mEditable);
		this.setSelection(start, spanString.length());
	}

	/**
	 * 
	 * @param bitmap
	 * @param filePath
	 * @param start
	 * @param end
	 */
	public void addImage(Bitmap bitmap, String filePath, int start, int end) {
		Logger.e("imgpath", filePath);
		String pathTag = "<img src=\"" + filePath + "\" />";
		SpannableString spanString = new SpannableString(pathTag);
		// ��ȡ��Ļ�Ŀ��
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int bmWidth = bitmap.getWidth();// ͼƬ�߶�
		int bmHeight = bitmap.getHeight();// ͼƬ���
		int zoomWidth = getWidth() - (paddingLeft + paddingRight);
		int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight);
		Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
		ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
		spanString.setSpan(imgSpan, 0, pathTag.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		Editable editable = this.getText(); // ��ȡedittext����
		editable.delete(start, end);
		editable.insert(start, spanString); // ����spanStringҪ��ӵ�λ��
	}

	/**
	 * 
	 * @param bitmap
	 * @param filePath
	 * @param start
	 * @param end
	 */
	public void addDefaultImage(String filePath, int start, int end) {
		Log.i("imgpath", filePath);
//		String pathTag = "<img src=\"" + filePath + "\"/>";
		String pathTag = "<img src=\"" + filePath + "\" />";

		SpannableString spanString = new SpannableString(pathTag);
		// ��ȡ��Ļ�Ŀ��
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.message_placeholder_picture);
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int bmWidth = bitmap.getWidth();// ͼƬ�߶�
		int bmHeight = bitmap.getHeight();// ͼƬ���
		int zoomWidth = getWidth() - (paddingLeft + paddingRight);
		int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight);
		Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
		ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
		spanString.setSpan(imgSpan, 0, pathTag.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (mEditable == null) {
			mEditable = this.getText(); // ��ȡedittext����
		}
		mEditable.delete(start, end);// ɾ��
		mEditable.insert(start, spanString); // ����spanStringҪ��ӵ�λ��
	}

	/**
	 * ��ͼƬ��������
	 * 
	 * @param bgimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		// ��ȡ���ͼƬ�Ŀ�͸�
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// ������Ϊ0 ����ԭͼ
		if (newWidth == 0) {
			newWidth = width;
			newHeight = height;
		}
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ������������
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	public void addFileImage(String path) {
		path = !path.startsWith("http") ? ("file://" + path) : path;
		String test = "<img src=\"" + path + "\" />";
		setRichText(test);
	}

	public void setRichText(String text) {
		int start = getSelectionStart();
		text = getText().insert(start, text).toString();
		this.setText(text);
		Logger.e("+++++++++++++"+text);
		this.mEditable = this.getText();
		// ��������
		String str = "<img src=\"([/\\w\\W/\\/.]*?)\"\\s+/>";
		Pattern pattern = Pattern.compile(str,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			final String localFilePath = matcher.group(1);
			String matchString = matcher.group();
			Logger.e("----------------"+localFilePath);
			Logger.e(matchString);

			final int matchStringStartIndex = text.indexOf(matchString);
			final int matchStringEndIndex = matchStringStartIndex
					+ matchString.length();
			ImageLoader.getInstance().loadImage(localFilePath,
					ImageLoadOptions.getOptions(), new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String uri, View arg1) {
							// TODO Auto-generated method stub
							// ����һ��Ĭ��ͼƬ
							addDefaultImage(localFilePath,
									matchStringStartIndex, matchStringEndIndex);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingComplete(String uri, View arg1,
								Bitmap bitmap) {
							// TODO Auto-generated method stub
							addImage(bitmap, uri, matchStringStartIndex,
									matchStringEndIndex);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}
					});
		}
		this.setText(mEditable);
	}

	private DisplayImageOptions getDisplayImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.message_placeholder_picture)
				.showImageForEmptyUri(R.drawable.message_placeholder_picture)
				.showImageOnFail(R.drawable.message_placeholder_picture)
				.cacheOnDisc(true).cacheInMemory(true)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.build();
		return options;
	}

	public String getRichText() {
		return this.getText().toString();
	}
}
