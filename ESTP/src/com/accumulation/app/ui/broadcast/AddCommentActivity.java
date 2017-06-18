package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.accumulation.app.adapter.EmoticonsPagerAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.base.DialogCallback;
import com.accumulation.app.config.Config;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.ui.file.BrowserSelectActivity;
import com.accumulation.app.ui.file.ChoosePictureActivity;
import com.accumulation.app.util.InputTools;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.view.EmoticonsEditText;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.FaceHolder;
import com.accumulation.lib.sociability.data.Subject;
import com.accumulation.lib.sociability.data.Target;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.net.data.JsonData;
import com.accumulation.lib.ui.indicator.CirclePageIndicator;
import com.accumulation.lib.ui.scrollable.ObservableScrollView;
import com.accumulation.lib.ui.scrollable.ObservableScrollViewCallbacks;
import com.accumulation.lib.ui.scrollable.ScrollState;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddCommentActivity extends BaseActivity implements
		KeyClickListener {

	public static final int REQUEST_CODE_BROWSER_IMAGE = 1;
	public static final int REQUEST_CODE_CHOOSE_IMAGE = 2;
	private final ArrayList<ChooseFile> paths = new ArrayList<ChooseFile>();
	private ArrayList<Subject> subjects = new ArrayList<Subject>();
	private LinearLayout emoticonsLayout;
	private EmoticonsEditText input;
	private ImageView emoticonsButton;
	private LinearLayout moreLayout, emoLayout, addLayout;
	private ObservableScrollView inputScroll;
	private ViewPager emoPager;
	private GridView picGrid;
	private PictureAdapter picAdapter;
	private int keyboardHeight;
	private String bid;
	private String cid;
	private String hint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		hideSoftInputView();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideSoftInputView();
		input.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (emoLayout.getVisibility() != View.VISIBLE
						&& addLayout.getVisibility() != View.VISIBLE) {
					showSoftInputView();
					caculateEmotionShow();
				} else {
					hideSoftInputView();
				}
			}
		}, 200);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_comment;
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "发布";
	}

	@Override
	protected String getTopTitle() {
		if (!TextUtils.isEmpty(cid)) {
			return "回复评论";
		} else {
			return "发评论";
		}
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		bid = getIntent().getStringExtra("broadcastId");
		cid = getIntent().getStringExtra("commentId");
		hint = getIntent().getStringExtra("hint");
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		initEmoView();
		input = (EmoticonsEditText) findViewById(R.id.broadcast_content);
		if(hint!=null){
			input.setHint(hint);
		}
		emoticonsLayout = (LinearLayout) findViewById(R.id.emotion_container);
		moreLayout = (LinearLayout) findViewById(R.id.layout_more);
		emoLayout = (LinearLayout) findViewById(R.id.layout_emo);
		addLayout = (LinearLayout) findViewById(R.id.layout_add);
		emoticonsButton = (ImageView) findViewById(R.id.btn_chat_emo);
		inputScroll = (ObservableScrollView) findViewById(R.id.input_scroll);
		inputScroll
				.setTouchInterceptionViewGroup((ViewGroup) findViewById(R.id.scroll_container));
		inputScroll
				.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {

					@Override
					public void onUpOrCancelMotionEvent(ScrollState scrollState) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onScrollChanged(int scrollY,
							boolean firstScroll, boolean dragging) {
						// TODO Auto-generated method stub
						if (keyboardHeight != 0) {
							addLayout.setVisibility(View.GONE);
							emoLayout.setVisibility(View.GONE);
							moreLayout.setVisibility(View.GONE);
							caculateEmotionShow();
						}
					}

					@Override
					public void onDownMotionEvent() {
						// TODO Auto-generated method stub

					}
				});
		input.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (moreLayout.getVisibility() == View.VISIBLE) {
					addLayout.setVisibility(View.GONE);
					emoLayout.setVisibility(View.GONE);
					moreLayout.setVisibility(View.GONE);
					caculateEmotionShow();
				}
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
		findViewById(R.id.input_other).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (moreLayout.getVisibility() == View.GONE) {
							moreLayout.setVisibility(View.VISIBLE);
							addLayout.setVisibility(View.VISIBLE);
							emoLayout.setVisibility(View.GONE);
							hideSoftInputView();
						} else {
							if (emoLayout.getVisibility() == View.VISIBLE) {
								emoLayout.setVisibility(View.GONE);
								addLayout.setVisibility(View.VISIBLE);
							} else {
								moreLayout.setVisibility(View.GONE);
							}
						}
					}
				});

		findViewById(R.id.input_emo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				input.setVisibility(View.VISIBLE);
				input.requestFocus();
				if (moreLayout.getVisibility() == View.GONE) {
					moreLayout.setVisibility(View.VISIBLE);
					emoLayout.setVisibility(View.GONE);
					addLayout.setVisibility(View.GONE);
					hideSoftInputView();
					emoticonsButton
							.setImageResource(R.drawable.btn_chat_keyboard_selector);
					emoticonsButton.postDelayed(new Runnable() {
						public void run() {
							emoLayout.setVisibility(View.VISIBLE);
						}
					}, 200);
				} else {
					if (addLayout.getVisibility() == View.VISIBLE) {
						addLayout.setVisibility(View.GONE);
						emoLayout.setVisibility(View.VISIBLE);
					} else {
						moreLayout.setVisibility(View.GONE);
						emoLayout.setVisibility(View.GONE);
						emoticonsButton.postDelayed(new Runnable() {
							public void run() {
								input.setVisibility(View.VISIBLE);
								input.requestFocus();
								showSoftInputView();
							}
						}, 200);
					}
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
						emoticonsLayout.setPadding(0, 0, 0, heightDifference);
						findViewById(R.id.scroll_container).setPadding(0, 0, 0,
								heightDifference + PixelUtil.dp2px(45));
					}
				});

		picGrid = (GridView) findViewById(R.id.pictures);
		picGrid.setAdapter(picAdapter = new PictureAdapter());
	}

	@Override
	public void onMenu() {
		String inputContent = input.getText().toString();
		if ("".equals(inputContent.trim())&&paths.size()==0) {
			toast("请输入内容");
			return;
		}
		int N=paths.size();
		ArrayList<String> choosePath=new ArrayList<String>();
		for (int i = 0; i < N; i++) {
			choosePath.add(paths.get(i).path);
		}
		SociabilityClient.getClient().addBroadcastReply(bid, cid,
				inputContent,choosePath, new DialogCallback<JsonData>(this) {
					@Override
					public void onResultCallback(int code, String message,
							JsonData result) {
						if (code < 0) {
							toast(message);
						} else {
							setResult(RESULT_OK);
							finish();
						}
					}
				}, JsonData.class);
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

	private void caculateEmotionShow() {
		emoticonsButton
				.setImageResource(emoLayout.isShown() ? R.drawable.btn_chat_keyboard_selector
						: R.drawable.compose_emoticonbutton_background);
	}

	@Override
	public void keyClickedIndex(final FaceHolder index) {

		if (-1 == index.index) {
			KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0,
					0, 0, KeyEvent.KEYCODE_ENDCALL);
			input.dispatchKeyEvent(event);
			return;
		}

		String key = index.face.toString();
		try {
			if (input != null && !TextUtils.isEmpty(key)) {
				int start = input.getSelectionStart();
				CharSequence content = input.getText().insert(start, key);
				input.setText(content);
				// 定位光标位置
				CharSequence info = input.getText();
				if (info instanceof Spannable) {
					Spannable spanText = (Spannable) info;
					Selection.setSelection(spanText, start + key.length());
				}
			}
		} catch (Exception e) {

		}
	}

	public void showSoftInputView() {
		View v = input;
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	public void hideSoftInputView() {
		View v = input;
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}

	class PictureAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (paths.size() == 0) {
				return 0;
			}
			return Math.min(Config.MAX_COMMENT_PIC_CHOOSE_COUNT, paths.size() + 1);
		}

		@Override
		public ChooseFile getItem(int arg0) {
			// TODO Auto-generated method stub
			if (paths.size() < Config.MAX_COMMENT_PIC_CHOOSE_COUNT) {
				if (arg0 >= paths.size()) {
					return new ChooseFile("", -1);
				}
				return paths.get(arg0);
			} else {
				return paths.get(arg0);
			}

		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (arg1 == null) {
				arg1 = LayoutInflater.from(getContext()).inflate(
						R.layout.item_grid_image, null);
			}
			ImageView image = (ImageView) arg1.findViewById(R.id.pic_img);
			ImageView delete = (ImageView) arg1.findViewById(R.id.delete_pic);
			float density = getResources().getDisplayMetrics().density;
			int size = 100;
			// if(pic.size()>=6){
			// size=70;
			// }
			arg1.setLayoutParams(new AbsListView.LayoutParams(
					(int) (size * density), (int) (size * density)));
			image.setScaleType(ScaleType.CENTER_CROP);
			final ChooseFile url = getItem(arg0);
			image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (url.flagTime < 0) {
						startChoosePicActivity();
					} else {
						Intent intent = new Intent(getApplication(),
								BrowserSelectActivity.class);
						intent.putExtra("photos", paths);
						intent.putExtra("position", arg0);
						startActivityForResult(intent,
								REQUEST_CODE_BROWSER_IMAGE);
					}
				}
			});
			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					paths.remove(url);
					picAdapter.notifyDataSetChanged();
				}
			});
			image.setBackgroundColor(Color.TRANSPARENT);
			if (url.flagTime < 0) {
				ImageLoader.getInstance().displayImage("file://" + url.path,
						image);
				if (AccumulationAPP.getInstance().isNightTheme()) {
					image.setImageResource(R.drawable.add_pic_night);
				} else {
					image.setImageResource(R.drawable.add_pic);
				}
				delete.setVisibility(View.GONE);
			} else {
				delete.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage("file://" + url.path,
						image);
			}
			return arg1;
		}
	}

	private void startChoosePicActivity() {
		Intent mIntent = new Intent(getApplication(),
				ChoosePictureActivity.class);
		mIntent.putExtra("time", System.currentTimeMillis());
		mIntent.putExtra("max", Config.MAX_COMMENT_PIC_CHOOSE_COUNT - paths.size());
		startActivityForResult(mIntent, REQUEST_CODE_CHOOSE_IMAGE);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == REQUEST_CODE_CHOOSE_IMAGE) {
			if (arg1 == RESULT_OK) {
				ArrayList<ChooseFile> selects = (ArrayList<ChooseFile>) arg2
						.getSerializableExtra("result");
				paths.addAll(selects);
				picAdapter.notifyDataSetChanged();
			}
		} else if (arg0 == REQUEST_CODE_BROWSER_IMAGE) {
			if (arg1 == RESULT_OK) {
				paths.clear();
				ArrayList<ChooseFile> selects = (ArrayList<ChooseFile>) arg2
						.getSerializableExtra("result");
				paths.addAll(selects);
				picAdapter.notifyDataSetChanged();
			}
		}
	}

}
