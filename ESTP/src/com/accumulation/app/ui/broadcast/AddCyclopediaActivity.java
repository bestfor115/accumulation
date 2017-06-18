package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.accumulation.app.adapter.EmoticonsPagerAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.base.DialogCallback;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.ui.file.ChoosePictureActivity;
import com.accumulation.app.ui.file.CutImageActivity;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.view.EmoticonsEditText;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.FaceHolder;
import com.accumulation.lib.sociability.data.JDArticle;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.ui.ViewHolder;
import com.accumulation.lib.ui.indicator.CirclePageIndicator;

public class AddCyclopediaActivity extends BaseActivity implements
		KeyClickListener {
	public static final int REQUEST_CODE_CHOOSE_IMAGE = 2;
	public static final int REQUEST_CODE_CHOOSE_COVER = 3;
	public static final int REQUEST_CODE_CUT_IMAGE = 4;

	// private final ArrayList<ChooseFile> paths = new ArrayList<ChooseFile>();
	private EditText articleTitle;
	private EmoticonsEditText articleContent;
	private LinearLayout emoticonsLayout;
	private LinearLayout content_layout;
	private LinearLayout moreLayout, emoLayout;
	private ImageView emoticonsButton;
	private TextView cover_tip;
	private int keyboardHeight;
	private ViewPager emoPager;

	@Override
	public void keyClickedIndex(FaceHolder index) {
		// TODO Auto-generated method stub

		if (-1 == index.index) {
			KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0,
					0, 0, KeyEvent.KEYCODE_ENDCALL);
			articleContent.dispatchKeyEvent(event);
			return;
		}
		View current_input = getCurrentFocus();
		if (current_input instanceof EmoticonsEditText) {
			EmoticonsEditText emoInput = (EmoticonsEditText) current_input;
			String key = index.face.toString();
			try {
				if (emoInput != null && !TextUtils.isEmpty(key)) {
					int start = emoInput.getSelectionStart();
					CharSequence content = emoInput.getText()
							.insert(start, key);
					emoInput.setText(content);
					// 定位光标位置
					CharSequence info = emoInput.getText();
					if (info instanceof Spannable) {
						Spannable spanText = (Spannable) info;
						Selection.setSelection(spanText, start + key.length());
					}
				}
			} catch (Exception e) {

			}
		}

	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_cyclopedia;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "写百科";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		initEmoView();
		articleTitle = ViewHolder.get(rootLayout, R.id.article_title);
		articleContent = ViewHolder.get(rootLayout, R.id.article_content);
		moreLayout = (LinearLayout) findViewById(R.id.layout_more);
		emoLayout = (LinearLayout) findViewById(R.id.layout_emo);
		emoticonsLayout = (LinearLayout) findViewById(R.id.emotion_container);
		emoticonsButton = (ImageView) findViewById(R.id.btn_chat_emo);
		content_layout = ViewHolder.get(rootLayout, R.id.content_layout);
		cover_tip = ViewHolder.get(rootLayout, R.id.cover_tip);
		articleContent.setMinHeight(AccumulationAPP.getInstance().height
				- statusBarHeight - PixelUtil.dp2px(285));
		articleTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		articleContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (moreLayout.getVisibility() == View.VISIBLE) {
					emoLayout.setVisibility(View.GONE);
					moreLayout.setVisibility(View.GONE);
					caculateEmotionShow();
				}
			}
		});
		rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						rootLayout.getWindowVisibleDisplayFrame(r);
						int screenHeight = rootLayout.getRootView().getHeight();
						int heightDifference = screenHeight - (r.bottom);
						keyboardHeight = heightDifference;
						if (articleTitle.hasFocus()) {
							emoticonsLayout.setVisibility(View.GONE);
						} else {
							emoticonsLayout.setVisibility(View.VISIBLE);
						}
						emoticonsLayout.setPadding(0, 0, 0, heightDifference);
						findViewById(R.id.scroll_container).setPadding(0, 0, 0,
								heightDifference + PixelUtil.dp2px(45));
					}
				});
		findViewById(R.id.input_image).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						startChoosePicActivity();
					}
				});
		findViewById(R.id.input_emo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (articleTitle.hasFocus()) {
					articleContent.setVisibility(View.VISIBLE);
					articleContent.requestFocus();
				}

				if (moreLayout.getVisibility() == View.GONE) {
					moreLayout.setVisibility(View.VISIBLE);
					emoLayout.setVisibility(View.GONE);
					hideSoftInputView();
					emoticonsButton
							.setImageResource(R.drawable.btn_chat_keyboard_selector);
					emoticonsButton.postDelayed(new Runnable() {
						public void run() {
							emoLayout.setVisibility(View.VISIBLE);
						}
					}, 200);
				} else {
					moreLayout.setVisibility(View.GONE);
					emoLayout.setVisibility(View.GONE);
					emoticonsButton.postDelayed(new Runnable() {
						public void run() {
							if (articleTitle.hasFocus()) {
								articleContent.setVisibility(View.VISIBLE);
								articleContent.requestFocus();
							}
							showSoftInputView();
						}
					}, 200);
					caculateEmotionShow();
				}
			}
		});
	}

	private void caculateEmotionShow() {
		emoticonsButton
				.setImageResource(emoLayout.isShown() ? R.drawable.btn_chat_keyboard_selector
						: R.drawable.compose_emoticonbutton_background);
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "完成";
	}
	
	@Override
	protected void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		String title=articleTitle.getText().toString();
		if(CommonUtils.isEmpty(title)){
			toast("请输入标题");
			return ;
		}
		String content=formatArticle();
		SociabilityClient.getClient().addArticle(title, content, new DialogCallback<JDArticle>(this) {

			@Override
			public void onResultCallback(int code, String message,
					JDArticle result) {
				if(code<0){
					toast(message);
				}else{
					Intent intent=new Intent();
					intent.putExtra("article", result.data);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		}, JDArticle.class);
	}

	private void startChoosePicActivity() {
		Intent mIntent = new Intent(getApplication(),
				ChoosePictureActivity.class);
		mIntent.putExtra("time", System.currentTimeMillis());
		mIntent.putExtra("max", -1);
		startActivityForResult(mIntent, REQUEST_CODE_CHOOSE_IMAGE);
	}

	private void startChooseCoverActivity() {
		Intent mIntent = new Intent(getApplication(),
				ChoosePictureActivity.class);
		mIntent.putExtra("time", System.currentTimeMillis());
		mIntent.putExtra("max", 1);
		startActivityForResult(mIntent, REQUEST_CODE_CHOOSE_COVER);
	}

	public void showSoftInputView() {
		View v = articleContent;
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	public void hideSoftInputView() {
		View v = articleContent;
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}

	private void initEmoView() {
		emoPager = (ViewPager) findViewById(R.id.pager_emo);
		CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setRadius(5 * density);
		mIndicator.setFillColor(0xFF000000);
		mIndicator.setStrokeWidth(1 * density);
		emoPager.setOffscreenPageLimit(3);
		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(this,
				AccumulationAPP.getInstance().faces, this);
		emoPager.setAdapter(adapter);
		mIndicator.setViewPager(emoPager);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == REQUEST_CODE_CHOOSE_IMAGE) {
			if (arg1 == RESULT_OK) {
				ArrayList<ChooseFile> selects = (ArrayList<ChooseFile>) arg2
						.getSerializableExtra("result");
				// paths.addAll(selects);
				if (getCurrentFocus() instanceof EditText) {
					EditText current_input = (EditText) getCurrentFocus();
					int index = content_layout.indexOfChild(current_input);
					if (selects.size() == 1) {
						final RelativeLayout inert_image = new RelativeLayout(
								getContext());
						inert_image.setBackgroundColor(Color
								.parseColor("#ededed"));
						LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						ImageView image = new ImageView(getContext());
						RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						inert_image.addView(image, rl);
						ImageView delete = new ImageView(getContext());
						delete.setImageResource(R.drawable.compose_photo_close);
						RelativeLayout.LayoutParams delete_rl = new RelativeLayout.LayoutParams(
								PixelUtil.dp2px(40), PixelUtil.dp2px(40));
						int padding = PixelUtil.dp2px(5);
						delete.setPadding(padding, padding, padding, padding);
						delete_rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						inert_image.addView(delete, delete_rl);
						delete.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								int delete_index = content_layout
										.indexOfChild(inert_image);
								if (content_layout.getChildAt(delete_index - 1) instanceof EditText) {
									if (content_layout
											.getChildAt(delete_index + 1) instanceof EditText) {
										EditText pre_edit = (EditText) content_layout
												.getChildAt(delete_index - 1);
										EditText post_edit = (EditText) content_layout
												.getChildAt(delete_index + 1);
										Editable new_show = pre_edit.getText();
										new_show.append(post_edit.getText());
										pre_edit.setText(new_show);
										content_layout.removeView(post_edit);
									}
								}
								content_layout.removeView(inert_image);
							}
						});
						// image.setImageBitmap(BitmapFactory.decodeFile(selects
						// .get(0).path));
						image.setImageBitmap(zoomImage(selects.get(0).path,
								content_layout.getMeasuredWidth()));
						inert_image.setTag(selects.get(0).path);
						content_layout.addView(inert_image, ++index, ll);
						int start = current_input.getSelectionStart();
						int total = current_input.getText().length();
						CharSequence left = current_input.getText()
								.subSequence(0, start);
						CharSequence right = start < total ? current_input
								.getText().subSequence(start, total) : "";
						current_input.setText(left);
						EditText insert_edit = createEditText();
						insert_edit.setText(right);
						LinearLayout.LayoutParams edit_ll = new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						content_layout.addView(insert_edit, ++index, edit_ll);
					} else {
						for (int i = 0; i < selects.size(); i++) {
							final RelativeLayout inert_image = new RelativeLayout(
									getContext());
							inert_image.setBackgroundColor(Color
									.parseColor("#ededed"));
							LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							ImageView image = new ImageView(getContext());
							RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							inert_image.addView(image, rl);
							ImageView delete = new ImageView(getContext());
							delete.setImageResource(R.drawable.compose_photo_close);
							RelativeLayout.LayoutParams delete_rl = new RelativeLayout.LayoutParams(
									PixelUtil.dp2px(40), PixelUtil.dp2px(40));
							int padding = PixelUtil.dp2px(5);
							delete.setPadding(padding, padding, padding,
									padding);
							delete_rl
									.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
							// inert_image.setGravity(Gravity.RIGHT);
							inert_image.addView(delete, delete_rl);
							delete.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									int delete_index = content_layout
											.indexOfChild(inert_image);
									if (content_layout
											.getChildAt(delete_index - 1) instanceof EditText) {
										if (content_layout
												.getChildAt(delete_index + 1) instanceof EditText) {
											EditText pre_edit = (EditText) content_layout
													.getChildAt(delete_index - 1);
											EditText post_edit = (EditText) content_layout
													.getChildAt(delete_index + 1);
											Editable new_show = pre_edit
													.getText();
											new_show.append(post_edit.getText());
											pre_edit.setText(new_show);
											content_layout
													.removeView(post_edit);
										}
									}
									content_layout.removeView(inert_image);
								}
							});
							image.setImageBitmap(zoomImage(selects.get(i).path,
									content_layout.getMeasuredWidth()));
							inert_image.setTag(selects.get(i).path);
							content_layout.addView(inert_image, ++index, ll);
							if (i < selects.size() - 1) {
								EditText insert_edit = createEditText();
								insert_edit.setText("");
								LinearLayout.LayoutParams edit_ll = new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT);
								content_layout.addView(insert_edit, ++index,
										edit_ll);
							}
						}

						int start = current_input.getSelectionStart();
						int total = current_input.getText().length();
						CharSequence left = current_input.getText()
								.subSequence(0, start);
						CharSequence right = start < total ? current_input
								.getText().subSequence(start, total) : "";
						current_input.setText(left);
						EditText insert_edit = createEditText();
						insert_edit.setText(right);
						LinearLayout.LayoutParams edit_ll = new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						content_layout.addView(insert_edit, ++index, edit_ll);
					}

					if (content_layout.getChildCount() > 1) {
						articleContent.setHint("");
						articleContent.setMinHeight(0);
					} else {
						articleContent.setHint("正文");
						articleContent.setMinHeight(AccumulationAPP
								.getInstance().height
								- statusBarHeight
								- PixelUtil.dp2px(285));
					}
				}
			}
		} else if (arg0 == REQUEST_CODE_CHOOSE_COVER) {
			if (arg1 == RESULT_OK) {
				ArrayList<ChooseFile> selects = (ArrayList<ChooseFile>) arg2
						.getSerializableExtra("result");
				if (selects.size() == 1) {
					Intent intent = new Intent(getContext(),
							CutImageActivity.class);
					intent.putExtra("file", selects.get(0).path);
					startActivityForResult(intent, REQUEST_CODE_CUT_IMAGE);
				}
			}
		} 
	}

	private EditText createEditText() {
		EditText insert_edit = new EmoticonsEditText(getContext());
		// insert_edit.setTextSize(getContext().getResources().getDimension(
		// getThemeResource(R.attr.mediumFontSize)));
		// insert_edit.setTextColor(getContext().getResources().getColor(
		// getThemeResource(R.attr.fontPrimary)));
		insert_edit.setTextSize(16);
		insert_edit.setBackgroundColor(Color.TRANSPARENT);
		insert_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (moreLayout.getVisibility() == View.VISIBLE) {
					emoLayout.setVisibility(View.GONE);
					moreLayout.setVisibility(View.GONE);
					caculateEmotionShow();
				}
			}
		});
		return insert_edit;
	}

	/**
	 * 对图片进行缩放
	 * 
	 * @param bgimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private static Bitmap zoomImage(String path, int maxWidth) {
		// 获取这个图片的宽和高
		int[] WH = AccumulationAPP.getInstance().getImageWH(path);
		float scale = maxWidth > 0 ? (maxWidth + 0f) / WH[0] : 0f;
		int wRatio = (int) Math.ceil(WH[0] / (float) maxWidth); // 图片是宽度的几倍
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = wRatio;
		return BitmapFactory.decodeFile(path, opts);
	}
	
	private String formatArticle(){
		StringBuffer sb=new StringBuffer();
		sb.append("<html>");
		sb.append(" <head>");
		sb.append("<meta name=\"viewport\" content=\"width=device-width\"/>  ");
		sb.append("</head>");
		sb.append(" <body>");
		int N=content_layout.getChildCount();
		for (int i = 0; i < N; i++) {
			View child=content_layout.getChildAt(i);
			if(child instanceof EditText){
				EditText edit=(EditText) child;
				if(!CommonUtils.isEmpty(edit.getText())){
					sb.append("  <p>");
					sb.append(edit.getText().toString());
					sb.append("</p>");
				}
			}
			if(child instanceof  RelativeLayout){
				String path=child.getTag()+"";
				if(!CommonUtils.isEmpty(path)){
					sb.append("  <p>");
					sb.append("<img src=\""+path+"\" _src=\""+path+"\" />");
					sb.append("</p>");
				}
			}
		}
		sb.append(" </body>");
		sb.append("</html>");
		return sb.toString();
	}
}
