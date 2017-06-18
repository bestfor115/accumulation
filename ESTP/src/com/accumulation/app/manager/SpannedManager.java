package com.accumulation.app.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.config.Config;
import com.accumulation.lib.sociability.data.Broadcast.Group;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.debug.Logger;

public class SpannedManager {
	private static final int BROADCAST_MAX_CHAR = 200;
	private static final String imageReg = "\\[img\\ssrc=\"([^\\]]*)\"\\]";
	private static final String fileReg = "\\[file name=\"([^\"]*)\"\\s*path=\"([^\"]*)\"\\]";

	private static Pattern buildPattern() {
		return Pattern.compile(Config.FACE_PATTERN, Pattern.CASE_INSENSITIVE);
	}

	private static Pattern buildImagePattern() {
		return Pattern.compile(Config.IMAGE_PATTERN, Pattern.CASE_INSENSITIVE);
	}

	@SuppressLint("NewApi")
	public static void changeRawString(final TextView txt, String text,
			List<Group> sendTo, boolean cut) {
		FontMetrics fm = txt.getPaint().getFontMetrics();
		int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
		float space = txt.getLineSpacingMultiplier();
		txt.setText(changeRawItemLoal(txt.getContext(), text, textHeight,
				sendTo, cut, space));
		txt.setMovementMethod(ClickableMovementMethod.getInstance());
	}

	public static CharSequence formatRawString(final TextView txt, String text) {
		FontMetrics fm = txt.getPaint().getFontMetrics();
		int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));

		return changeRawItemLoal(txt.getContext(), text, textHeight, null,
				false, 1.0f);
	}

	public static CharSequence formatRawString(final TextView txt,
			CharSequence text) {
		FontMetrics fm = txt.getPaint().getFontMetrics();
		int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
		return changeRawItemLoal(txt.getContext(), text.toString(), textHeight,
				null, false, 1.0f);
	}

	@SuppressLint("NewApi")
	public static void changeRawString2(final TextView txt, String text,
			List<Group> sendTo, boolean cut) {
		FontMetrics fm = txt.getPaint().getFontMetrics();
		int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
		float space = txt.getLineSpacingMultiplier();

		// GifImageSpan[] spans=txt.getEditableText().getSpans(0,
		// txt.getEditableText().length(), GifImageSpan.class);
		// int N=spans==null?0:spans.length;
		// for (int i = 0; i < N; i++) {
		// GifImageSpan s=spans[i];
		// if(s.getDrawable() instanceof GifDrawable){
		// GifDrawable gif=(GifDrawable) s.getDrawable();
		// gif.stop();
		// }
		// }
		txt.setText(changeRawItemLoal(txt.getContext(), text, textHeight,
				sendTo, cut, space));
	}

	public static String formatShareContent(Context context, String text) {
		return changeRawItemLoal(context, text, 10, null, true, 1.0f)
				.toString();
	}

	private static String changeGroupToLink(Group g) {
		StringBuffer sb = new StringBuffer();
		sb.append("<a href=\"");
		sb.append("/Group/Home/");
		sb.append(g.Name);
		sb.append("\">");
		sb.append(g.Name);
		sb.append("</a>");
		return sb.toString();
	}

	public static String changeGroupToLink(String name) {
		StringBuffer sb = new StringBuffer();
		sb.append("<a href=\"");
		sb.append("/Group/Home/");
		sb.append(name);
		sb.append("\">");
		sb.append(name);
		sb.append("</a>");
		return sb.toString();
	}

	@SuppressLint("NewApi")
	public static CharSequence changeRawItemLoal(Context cxt, String text,
			int textHeight, List<Group> sendTo, boolean cut, float space) {
		try {
			if (CommonUtils.isEmpty(text)) {
				text = "";
			}
			HashMap<String, TextMatch> result = new HashMap<String, TextMatch>();
			String emptyStr = "";
			StringBuffer sendToStr = new StringBuffer();
			sendToStr.append(emptyStr);
			String arrow = Config.SYMBOL_PATTERN;
			if (sendTo != null) {
				int N = sendTo.size();
				for (int i = 0; i < N; i++) {
					Group group = sendTo.get(i);
					sendToStr.append(arrow);
					sendToStr.append(changeGroupToLink(group));
					if (i == N - 1) {
						sendToStr.append(" : ");
					} else {
						sendToStr.append(" ");
					}
				}
			}
			text = sendToStr.toString() + text;
			text = parseLabel(text, result);
			SpannableString spannableString = new SpannableString(text);
			// 鏍囪琛ㄦ儏
			// replace emotion drawable
			int start = 0;
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(1, faceText.lastIndexOf("]"));
				Drawable bitmap = AccumulationAPP.getInstance()
						.getBitmapByTitle(key);
				ImageSpan imageSpan = new GifImageSpan(bitmap,
						DynamicDrawableSpan.ALIGN_BASELINE);
				imageSpan.getDrawable().setBounds(0, 0, textHeight, textHeight);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}

			start = 0;
			pattern = buildImagePattern();
			matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				Logger.e(faceText);
				String key = faceText.substring(2, faceText.lastIndexOf("@]"));
				FileImageSpan imageSpan = new FileImageSpan(key, cxt);
				// // 获取屏幕的宽高
				// Bitmap bitmap = BitmapFactory.decodeFile(key);
				// // int paddingLeft = textView.getPaddingLeft();
				// // int paddingRight = textView.getPaddingRight();
				// int paddingLeft = 0;
				// int paddingRight = 0;
				// int bmWidth = bitmap.getWidth();// 图片高度
				// int bmHeight = bitmap.getHeight();// 图片宽度
				// int zoomWidth = AccumulationAPP.getInstance().width
				// - (paddingLeft + paddingRight);
				// int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth)
				// * bmHeight);
				// Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
				// ImageSpan imgSpan = new ImageSpan(cxt, newBitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}

			List<Map.Entry<String, TextMatch>> list = new ArrayList<Map.Entry<String, TextMatch>>(
					result.entrySet());
			Collections.sort(list,
					new Comparator<Map.Entry<String, TextMatch>>() {
						public int compare(
								Map.Entry<String, TextMatch> mapping1,
								Map.Entry<String, TextMatch> mapping2) {
							return mapping1.getValue().index
									- mapping2.getValue().index;
						}
					});
			ClickableSpan url = null;
			Iterator<Map.Entry<String, TextMatch>> it = list.iterator();
			start = 0;
			while (it.hasNext()) {
				Map.Entry<String, TextMatch> entry = it.next();
				// replace company link
				if (entry.getValue().type == MyURLSpan.COMPANY_TYPE) {
					String key = entry.getKey();
					int startIndex = text.indexOf(key, start);
					int endIndex = startIndex + key.length();
					if (startIndex >= 0) {
						String show = "link://company?id="
								+ entry.getValue().raw;
						url = new MyURLSpan(show, entry.getValue().type);
						spannableString.setSpan(url, startIndex, endIndex,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					start = (endIndex - 1);
					// replace vote link
				} else if (entry.getValue().type == MyURLSpan.VOTE_TYPE) {
					String key = entry.getKey();
					int startIndex = text.indexOf(key, start);
					int endIndex = startIndex + key.length();
					if (startIndex >= 0) {
						String show = "link://vote?id=" + entry.getValue().raw;
						url = new MyURLSpan(show, entry.getValue().type);
						spannableString.setSpan(url, startIndex, endIndex,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					start = (endIndex - 1);
					// replace group link
				} else if (entry.getValue().type == MyURLSpan.GROUP_TYPE) {
					String key = entry.getKey();
					Logger.d(entry.getKey());

					int startIndex = text.indexOf(key, start);
					int endIndex = startIndex + key.length();
					if (startIndex >= 0) {
						String show = "link://group?name="
								+ entry.getValue().raw;
						url = new MyURLSpan(show, entry.getValue().type);
						spannableString.setSpan(url, startIndex, endIndex,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					start = (endIndex - 1);
				} else if (entry.getValue().type == MyURLSpan.ARTICLE_TYPE) {
					String key = entry.getKey();
					Logger.d(entry.getKey());

					int startIndex = text.indexOf(key, start);
					int endIndex = startIndex + key.length();
					if (startIndex >= 0) {
						String show = "link://article?id="
								+ entry.getValue().raw;
						url = new MyURLSpan(show, entry.getValue().type);
						spannableString.setSpan(url, startIndex, endIndex,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					start = (endIndex - 1);
				}
			}
			// clear email @
			pattern = buildLinkPattern();
			matcher = pattern.matcher(text);
			start = 0;
			Object tagSpan = null;
			while (matcher.find()) {
				String faceText = matcher.group();
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0) {
					String show = "link://user?email=" + faceText;
					url = new MyURLSpan(show, MyURLSpan.USER_TYPE);
					tagSpan = url;
					spannableString.setSpan(url, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				start = (endIndex - 1);
			}

			pattern = buildTopicPattern();
			matcher = pattern.matcher(text);
			start = 0;
			while (matcher.find()) {
				String faceText = matcher.group();
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0) {
					String show = "link://user?email=" + faceText;
					url = new MyURLSpan(show, MyURLSpan.TOPIC_TYPE);
					tagSpan = url;
					spannableString.setSpan(url, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				start = (endIndex - 1);
			}

			// clear space
			pattern = Pattern.compile(Config.EMPTY_PATTERN,
					Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(text);
			start = 0;
			while (matcher.find()) {
				String faceText = matcher.group();
				Drawable bitmap = new ColorDrawable(Color.TRANSPARENT);
				ImageSpan imageSpan = new ImageSpan(bitmap);
				imageSpan.getDrawable().setBounds(0, 0, 0, 0);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0) {
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				start = (endIndex - 1);
			}

			// replace article icon;
			pattern = Pattern.compile(Config.ARTICLE_ICON_LABEL,
					Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(text);
			start = 0;
			while (matcher.find()) {
				String faceText = matcher.group();
				Drawable bitmap = cxt.getResources().getDrawable(
						R.drawable.userinfo_relationship_indicator_message);
				ImageSpan imageSpan = new ImageSpan(bitmap,DynamicDrawableSpan.ALIGN_BASELINE);
				imageSpan.getDrawable().setBounds(0, 0,
						(int) (textHeight * 1.0f),
						(int) (textHeight * 0.8f) + 0);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0) {
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				start = (endIndex - 1);
			}
			// 鍘绘帀鏍囪
			pattern = Pattern.compile(Config.TYPE_LABLE_PATTERN,
					Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(text);
			start = 0;
			while (matcher.find()) {
				String faceText = matcher.group();
				Drawable bitmap = new ColorDrawable(Color.TRANSPARENT);
				ImageSpan imageSpan = new ImageSpan(bitmap);
				imageSpan.getDrawable().setBounds(0, 0, 0, 0);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0) {
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				start = (endIndex - 1);
			}
			// 淇グ鐗规畩绗﹀彿
			pattern = Pattern.compile(Config.SYMBOL_PATTERN,
					Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(text);
			start = 0;
			while (matcher.find()) {
				String faceText = matcher.group();
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0) {
					url = new SymbolSpan(faceText, MyURLSpan.USER_TYPE);
					spannableString.setSpan(url, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				start = (endIndex);
			}
			// 鏍囪瓒呴摼鎺�
			pattern = Pattern.compile(Config.URL_LINK_PATTERN,
					Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(text);
			start = 0;
			while (matcher.find()) {
				String faceText = matcher.group();
				Drawable bitmap = cxt.getResources().getDrawable(
						R.drawable.url_link);

				ClickableImageSpan imageSpan = new ClickableImageSpan(bitmap,
						faceText);
				int offset = 0;
				imageSpan.getDrawable().setBounds(0, offset,
						(int) (textHeight * 4.5f),
						(int) (textHeight * 0.8f) + offset);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0) {
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				start = (endIndex - 1);
			}
			// 鐪佺暐鍙�
			int len = spannableString.length();
			int start_index = tagSpan == null ? 0 : spannableString
					.getSpanEnd(tagSpan);
			start_index = Math.min(len / 2, start_index);
			if (cut && (len - start_index) > BROADCAST_MAX_CHAR) {
				Drawable bitmap = cxt.getResources().getDrawable(
						R.drawable.navigationbar_more);
				ImageSpan imageSpan = new ImageSpan(bitmap);
				imageSpan.getDrawable().setBounds(0, 0,
						(int) (textHeight * 1.0f),
						(int) (textHeight * 0.8f) + 0);
				spannableString.setSpan(imageSpan, start_index
						+ BROADCAST_MAX_CHAR - 3, len,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				return spannableString.subSequence(0, BROADCAST_MAX_CHAR);
			}
			return spannableString;
		} catch (Exception e) {
			return text;
		}
	}

	private static String parseLabel(String s, HashMap<String, TextMatch> result) {
		result.clear();
		String brReg = "\\[br\\]";
		String brReg1 = "<br />";
		String linkReg = "<a href=[^>]*>([^<]*)</a>";
		String emptyStr = "";
		s = s.replaceAll(imageReg, emptyStr).replaceAll(fileReg, emptyStr)
				.replaceAll(brReg, emptyStr).replaceAll(brReg1, "\n");
		Pattern linkPattern = Pattern.compile(linkReg);
		Matcher matchedLinks = linkPattern.matcher(s);
		int index = 0;
		while (matchedLinks.find()) {
			int startIndex = matchedLinks.start();
			int endIndex = matchedLinks.end();
			String linkStr = s.substring(startIndex, endIndex);
			int type = MyURLSpan.COMPANY_TYPE;
			String flag = "";
			if (linkStr.contains("/Company/Home/")) {
				flag = "/Company/Home/";
				type = MyURLSpan.COMPANY_TYPE;
			} else if (linkStr.contains("/Vote/View/")) {
				flag = "/Vote/View/";
				type = MyURLSpan.VOTE_TYPE;
			} else if (linkStr.contains("/Group/Home/")) {
				flag = "/Group/Home/";
				type = MyURLSpan.GROUP_TYPE;
			} else if (linkStr.contains("/article/index")) {
				flag = "/article/index?id=";
				type = MyURLSpan.ARTICLE_TYPE;
			} else {
				Logger.d(matchedLinks.group(1));
				continue;
			}
			String linkText = "" + matchedLinks.group(1) + "#TYPE_LABLE" + type
					+ "#";
			if (type == MyURLSpan.ARTICLE_TYPE) {
				linkText = Config.ARTICLE_ICON_LABEL + linkText;
			}
			String id = linkStr.substring(
					linkStr.indexOf(flag) + flag.length(),
					linkStr.indexOf(">") - 1);
			result.put(linkText, new TextMatch(type, id, linkStr, index++));
		}
		Iterator<Map.Entry<String, TextMatch>> it = result.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, TextMatch> entry = it.next();
			s = s.replace(entry.getValue().tag, entry.getKey() + " ");
		}
		return s;
	}

	public static String changeUserName(String name) {
		return (name + "").split("\\(")[0];
	}

	private static Pattern buildLinkPattern() {
		return Pattern.compile(Config.LINK_PATTERN, Pattern.CASE_INSENSITIVE);
	}

	private static Pattern buildTopicPattern() {
		return Pattern.compile(Config.TOPIC_PATTERN, Pattern.CASE_INSENSITIVE);
	}

	public static class TextMatch {
		public int type;
		public String raw;
		public String tag;
		public int index;

		public TextMatch(int type, String raw, String tag) {
			super();
			this.type = type;
			this.raw = raw;
			this.tag = tag;
		}

		public TextMatch(int type, String raw, String tag, int index) {
			super();
			this.type = type;
			this.raw = raw;
			this.tag = tag;
			this.index = index;
		}

		public TextMatch() {

		}
	}

	/**
	 * 
	 * @param bitmap
	 * @param filePath
	 * @param start
	 * @param end
	 */
	public void addImage(TextView textView, Bitmap bitmap, String filePath,
			int start, int end) {
		Logger.d("imgpath", filePath);
		String pathTag = "<img src=\"" + filePath + "\"/>";
		SpannableString spanString = new SpannableString(pathTag);
		// 获取屏幕的宽高
		int paddingLeft = textView.getPaddingLeft();
		int paddingRight = textView.getPaddingRight();
		int bmWidth = bitmap.getWidth();// 图片高度
		int bmHeight = bitmap.getHeight();// 图片宽度
		int zoomWidth = textView.getWidth() - (paddingLeft + paddingRight);
		int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight);
		Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
		ImageSpan imgSpan = new ImageSpan(textView.getContext(), newBitmap);
		spanString.setSpan(imgSpan, 0, pathTag.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// Editable editable = textView.getText(); // 获取edittext内容
		// editable.delete(start, end);// 删除
		// editable.insert(start, spanString); // 设置spanString要添加的位置
	}

	/**
	 * 对图片进行缩放
	 * 
	 * @param bgimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 如果宽度为0 保持原图
		if (newWidth == 0) {
			newWidth = width;
			newHeight = height;
		}
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}
}
