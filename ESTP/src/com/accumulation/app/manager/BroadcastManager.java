package com.accumulation.app.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.config.URIConfig;
import com.accumulation.app.ui.file.BrowserWebIconActivity;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.lib.sociability.data.Broadcast;
import com.accumulation.lib.sociability.data.Image;
import com.accumulation.lib.sociability.data.Subject;
import com.accumulation.lib.tool.debug.Logger;

public class BroadcastManager {
	private static final String imageReg = "\\[img\\ssrc=\"([^\\]]*)\"\\]";
	private static final String fileReg = "\\[file name=\"([^\"]*)\"\\s*path=\"([^\"]*)\"\\]";
	private static final Pattern imagePattern = Pattern.compile(imageReg);
	private static final Pattern filePattern = Pattern.compile(fileReg);

	public static class Accessory {
		public String Name;
		public String Path;
	}

	public static void showImageInContainer(Broadcast bc,
			LinearLayout rootLayout, final Context context) {
		showImageInContainer(bc.ImageList, rootLayout, context);
	}

	public static void showZipInContainer(Broadcast b, LinearLayout rootLayout,
			final Context context) {
		showZipInContainer(b.content, rootLayout, context);
	}

	public static void showZipInContainer(String ss, LinearLayout rootLayout,
			final Context context) {
		List<Accessory> accessorys = getAccessoryFromContent(ss);
		int N = accessorys.size();
		rootLayout.setVisibility(N > 0 ? View.VISIBLE : View.GONE);
		if (rootLayout.getChildCount() != N) {
			rootLayout.removeAllViews();
			for (int i = 0; i < N; i++) {
				TextView line = new TextView(context);
				line.setTextSize(15);
				line.setSingleLine();
				line.setEllipsize(TextUtils.TruncateAt.END);
				line.setTextColor(context.getResources().getColor(
						R.color.button_color_selector));
				LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT);
				line.setLayoutParams(lp);
				line.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
				line.getPaint().setAntiAlias(true);// 抗锯齿
				rootLayout.addView(line);
			}
		}

		for (int i = 0; i < N; i++) {
			final Accessory entry = accessorys.get(i);
			TextView line = (TextView) rootLayout.getChildAt(i);
			line.setText(accessorys.get(i).Name);
			line.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// DialogTips dialog = new DialogTips(context, "提示",
					// "现在开始下载文件：" + entry.Name + "?", "确定", true, true);
					// dialog.setOnSuccessListener(new
					// DialogInterface.OnClickListener() {
					// public void onClick(DialogInterface dialogInterface,
					// int userId) {
					// String url = URIConfig.DOMAIN + "/Knowledge/Down/"
					// + entry.Path;
					// new DownloadDialog(context, url, entry.Name)
					// .showDownloadDialog();
					// }
					// });
					// dialog.show();
					// dialog = null;
				}
			});
		}
	}

	@SuppressLint("NewApi")
	public static void showTagInContainer(Subject[] images,
			LinearLayout rootLayout, final Context context) {
		int N = images == null ? 0 : images.length;
		rootLayout.setVisibility(N > 0 ? View.VISIBLE : View.GONE);
		rootLayout.setVisibility(View.GONE);
		if (rootLayout.getChildCount() != N) {
			rootLayout.removeAllViews();
			for (int i = 0; i < N; i++) {
				TextView line = new TextView(context);
				line.setTextSize(13);
				line.setSingleLine();
				line.setGravity(Gravity.CENTER);
				// TypedValue typedValue = new TypedValue();
				// context.getTheme().resolveAttribute(R.attr.hollow_operation_bg,
				// typedValue, true);
				// line.setBackgroundResource(typedValue.resourceId);
				line.setBackground(context.getResources().getDrawable(
						R.drawable.tip_selector));
				line.setPadding(PixelUtil.dp2px(15), PixelUtil.dp2px(3),
						PixelUtil.dp2px(15), PixelUtil.dp2px(3));

				line.setTextColor(context.getResources().getColor(
						R.color.description_txt_color));
				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				lp.leftMargin = PixelUtil.dp2px(5);
				lp.topMargin = PixelUtil.dp2px(5);
				lp.bottomMargin = PixelUtil.dp2px(5);
				line.setLayoutParams(lp);
				rootLayout.addView(line);
			}
		}
		for (int i = 0; i < N; i++) {
			final Subject entry = images[i];
			TextView line = (TextView) rootLayout.getChildAt(i);
			line.setText(entry.Name);
			line.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Intent intent = new Intent((context),
					// SubjectCardActivity.class);
					// Bundle mBundle=new Bundle();
					// mBundle.putSerializable("data", entry);
					// intent.putExtras(mBundle);
					// context.startActivity(intent);
				}
			});
		}
	}

	public static void showImageInContainer(final List<Image> images,
			LinearLayout rootLayout, final Context context) {
		showImageInContainer(images, rootLayout, context, -1);
	}

	public static void showImageInContainer(final List<Image> images,
			LinearLayout rootLayout, final Context context, int fill) {
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.removeAllViews();
		int N = images == null ? 0 : images.size();
		rootLayout.setVisibility(N > 0 ? View.VISIBLE : View.GONE);
		if (images == null) {
			return;
		}
		final ArrayList<String> photos = new ArrayList<String>();
		for (int i = 0; i < images.size(); i++) {
			photos.add(images.get(i).AccessURL);
		}
		LinearLayout line = null;
		int row_size = 3;
		int max_width = rootLayout.getMeasuredWidth();
		if (N == 4) {
			row_size = 2;
		}
		for (int i = 0; i < images.size(); i++) {
			if (i % row_size == 0) {
				line = new LinearLayout(context);
				line.setOrientation(LinearLayout.HORIZONTAL);
				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				line.setLayoutParams(lp);
				rootLayout.addView(line);
			}
			final int index = i;
			ImageView imageView = createImageView(context, max_width, row_size,
					images.size(), images.get(i).Width, images.get(i).Height,
					fill);
			line.addView(imageView);
			final Image image = images.get(i);
			String url = "" + image.ThumbImg;
			if (!url.startsWith("http")) {
				url = "file://" + url;
			}
			AccumulationAPP.getInstance().loadImage(url, imageView);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent((context),
							BrowserWebIconActivity.class);
					intent.putStringArrayListExtra("photos", photos);
					intent.putExtra("position", index);
					context.startActivity(intent);
				}
			});
		}
	}

	public static ImageView createImageView(Context context, int max_width,
			int row_size, int total, int originalWidth, int originalHeight,
			int fill) {
		ImageView imageView = new ImageView(context);
		int margin = PixelUtil.dp2px(3);
		int width = PixelUtil.dp2px(90);
		LinearLayout.LayoutParams lp2 = null;
		if (total == 1) {
			int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
			if (fill > 0) {
				screenWidth = fill;
			}
			if (originalHeight != 0 && originalWidth != 0) {
				float scale = originalWidth / originalHeight;
				float factor = 1.0f;
				float ss = fill > 0 ? 1.0f
						: (originalWidth <= originalHeight ? 0.5f : 0.7f);
				float s1 = screenWidth * ss / originalWidth;
				if (s1 > 1) {
					originalWidth = (int) (originalWidth * s1);
					originalHeight = (int) (originalHeight * s1);
				}
				lp2 = new LayoutParams(originalWidth, originalHeight);
			} else {
				lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
			}
		} else {
			lp2 = new LayoutParams(width, width);
		}
		lp2.setMargins(margin, margin, 0, 0);
		imageView.setLayoutParams(lp2);
		if (!AccumulationAPP.getInstance().isNightTheme()) {
			imageView.setBackgroundResource(R.drawable.broadcast_image_border);
		} else {
			imageView.setBackgroundColor(Color.parseColor("#2c2c2c"));
		}
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		return imageView;
	}

	public static List<Accessory> getAccessoryFromContent(String content) {
		List<Accessory> accessories = new ArrayList<Accessory>();
		Matcher matchedFiles = filePattern.matcher(content + "");
		while (matchedFiles.find()) {
			int start = matchedFiles.start();
			int end = matchedFiles.end();
			String fileStr = content.substring(start, end);
			Accessory accessory = new Accessory();
			accessory.Name = matchedFiles.group(1);
			accessory.Path = matchedFiles.group(2);
			accessories.add(accessory);
		}
		return accessories;
	}

	public static List<Image> getImagesFromContent(String content) {
		Matcher matchedImages = imagePattern.matcher(content);
		List<Image> images = new ArrayList<Image>();
		while (matchedImages.find()) {
			int start = matchedImages.start();
			int end = matchedImages.end();
			String imageStr = content.substring(start, end);

			String imageSrc = matchedImages.group(1);
			Log.i("Image", String.format("imageStr:%s,imageSrc:%s", imageStr,
					imageSrc));
			Image img = new Image();
			img.SourceImg = imageSrc;
			img.ThumbImg = imageSrc;
			img.AccessURL = imageSrc;
			images.add(img);
		}
		return images;
	}

}
