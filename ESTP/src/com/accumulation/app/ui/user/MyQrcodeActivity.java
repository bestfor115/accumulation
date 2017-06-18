package com.accumulation.app.ui.user;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.config.Config;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.JDUserProfile;
import com.accumulation.lib.tool.time.TimeFormateTool;
import com.accumulation.lib.ui.ViewHolder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MyQrcodeActivity extends BaseActivity {

	private ImageView my_2dcode;
	private TextView userInfo;
	private TextView userName;
	private ImageView userAvatar;
	private PopupWindow avatorPop;
	private View container;
	int colors[] = { 0xff000000, 0xff0000ff, 0xff00ff00, 0xffff00ff };
	int bgs[] = { R.drawable.bg_skin_thumb6, R.drawable.bg_skin_thumb7,
			R.drawable.bg_skin_thumb10, R.drawable.bg_skin_thumb14 };
	int colorIndex = 0;
	
	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		colorIndex = SaveManager.getInstance(this).getSaveQRCodeIndex();
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		loadData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		caculateShow();
	}
	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "我的二维码";
	}

	@Override
	public void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		showMenuPop();
	}

	@SuppressLint("NewApi")
	private void showMenuPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_qrcode_menu,
				null);
		View operation_layout = view.findViewById(R.id.operation_layout);
		avatorPop = new PopupWindow(view);
		view.findViewById(R.id.cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						avatorPop.dismiss();
					}
				});
		View save = view.findViewById(R.id.save);
		View change = view.findViewById(R.id.change);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				avatorPop.dismiss();
				captureScreen();
			}
		});
		change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				avatorPop.dismiss();
				colorIndex = (colorIndex + 1) % colors.length;
				SaveManager.getInstance(getApplicationContext())
						.saveQRCodeIndex(colorIndex);
				createQrcode();
			}
		});
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				avatorPop.dismiss();
			}
		});
		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从底部弹起
		avatorPop.setAnimationStyle(R.style.Animations_Pop);
		avatorPop.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
		int Y = PixelUtil.dp2px(200);
		operation_layout.setTranslationY(Y);
		operation_layout.animate().translationY(0);
	}

	@Override
	protected int getMenuRes() {
		// TODO Auto-generated method stub
		return R.drawable.ic_bar_web_more_normal;
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		my_2dcode = (ImageView) findViewById(R.id.my_2dcode);
		container = findViewById(R.id.code_container);
		userAvatar = ViewHolder.get(rootLayout, R.id.user_avatar);
		userName = ViewHolder.get(rootLayout, R.id.user_name);
		userInfo = ViewHolder.get(rootLayout, R.id.user_info);
		createQrcode();
	}

	private void createQrcode() {
		String contentString = Config.QRCODE_SCAN_USER_TAG
				+ SaveManager.getInstance(this).getLoginId();
		createImage(contentString, my_2dcode);
		container.setBackgroundResource(bgs[colorIndex]);
	}

	/**
	 * 用字符串生成二维码
	 * 
	 * @param str
	 * @author zhouzhe@lenovo-cw.com
	 * @return
	 * @throws WriterException
	 */
	public Bitmap Create2DCode(String str) throws WriterException {
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		int W = PixelUtil.dp2px(200);
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, W, W);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// if (matrix.get(x, y)) {
				// pixels[y * width + x] = 0xff000000;
				// }
				pixels[y * width + x] = (pixels[y * width + x] + (int) (Math
						.random() * 100)) % 255;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	// 生成QR图
	private void createImage(String text, ImageView image) {
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();
			if (text == null || "".equals(text) || text.length() < 1) {
				return;
			}
			int QR_WIDTH = PixelUtil.dp2px(200);
			int QR_HEIGHT = PixelUtil.dp2px(200);
			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
					QR_WIDTH, QR_HEIGHT);

			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = colors[colorIndex];
					} else {
						pixels[y * QR_WIDTH + x] = 0xccffffff;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);

			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			image.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	private void caculateShow() {
		// TODO Auto-generated method stub
		if(AccumulationAPP.getInstance().data==null){
			return ;
		}
		AccumulationAPP.getInstance().loadImage(
				AccumulationAPP.getInstance().data.Avatar, userAvatar);
		userName.setText(AccumulationAPP.getInstance().data.Name);
		String birthday = AccumulationAPP.getInstance().data.Brithday;
		try {
			Calendar bc = Calendar.getInstance();
			bc.setTime(TimeFormateTool.changeToDate(birthday));
			long birth_year = bc.get(Calendar.YEAR);
			long now_year = Calendar.getInstance().get(Calendar.YEAR);
			String info = "{0} {1}岁 {2}";
			userInfo.setText(MessageFormat.format(info,
					(0 == AccumulationAPP.getInstance().data.Sex) ? "男" : "女",
					now_year - birth_year,
					AccumulationAPP.getInstance().data.Title));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void loadData() {
		SociabilityClient.getClient().getMyProfile(
				new BaseCallback<JDUserProfile>() {

					@Override
					public void onResultCallback(int code, String message,
							JDUserProfile result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							AccumulationAPP.getInstance().data = result.data;
							caculateShow();
						}
					}
				}, JDUserProfile.class);
	}

	private void captureScreen() {
		/** process screen capture on background thread */
		Runnable action = new Runnable() {
			@Override
			public void run() {
				/**
				 * activity's root layout id, you can change the
				 * android.R.id.content to your root layout id
				 */
				Bitmap bitmap = null;
				ByteArrayOutputStream baos = null;
				try {
					bitmap = Bitmap.createBitmap(container.getWidth(),
							container.getHeight(), Bitmap.Config.ARGB_4444);
					// bitmap = BMapUtil.getBitmapFromViews(contentView);
					container.draw(new Canvas(bitmap));
					baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					// savePic(bitmap,"sdcard/DCM/Camera");
					savePic(bitmap, "sdcard/short.png");

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						/** no need to close, actually do nothing */
						if (null != baos)
							baos.close();
						if (null != bitmap && !bitmap.isRecycled()) {
							// bitmap.recycle();
							bitmap = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		try {
			action.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 保存到sdcard
	 * 
	 * @param Bitmap
	 *            b
	 * @param String
	 *            strFileName 保存地址
	 */
	private void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				boolean success = b
						.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
				if (success)
					Toast.makeText(MyQrcodeActivity.this, "已保存到相册",
							Toast.LENGTH_SHORT).show();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveToCamera(new File(strFileName));
	}

	private void saveToCamera(File file) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		sendBroadcast(intent);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_qrcode;
	}

}
