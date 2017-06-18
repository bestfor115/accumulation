package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.ESTPConfig;
import com.accumulation.app.R;
import com.accumulation.app.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.accumulation.app.adapter.EmoticonsPagerAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.config.Config;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.data.YLLocation;
import com.accumulation.app.dialog.DialogTips;
import com.accumulation.app.manager.MyURLSpan;
import com.accumulation.app.ui.LoginActivity;
import com.accumulation.app.ui.file.BrowserSelectActivity;
import com.accumulation.app.ui.file.ChoosePictureActivity;
import com.accumulation.app.util.FileUtil;
import com.accumulation.app.util.HttpHelper;
import com.accumulation.app.util.HttpHelper.TitleCallback;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.view.EmoticonsEditText;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.Article;
import com.accumulation.lib.sociability.data.FaceHolder;
import com.accumulation.lib.sociability.data.Subject;
import com.accumulation.lib.sociability.data.Target;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.ui.indicator.CirclePageIndicator;
import com.accumulation.lib.ui.scrollable.ObservableScrollView;
import com.accumulation.lib.ui.scrollable.ObservableScrollViewCallbacks;
import com.accumulation.lib.ui.scrollable.ScrollState;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddDisccussActivity extends BaseActivity implements
		KeyClickListener {

	public static final int REQUEST_CODE_CHOOSE_REWARD = 0;
	public static final int REQUEST_CODE_BROWSER_IMAGE = 1;
	public static final int REQUEST_CODE_CHOOSE_IMAGE = 2;
	public static final int REQUEST_CODE_CHOOSE_RANGE = 3;
	public static final int REQUEST_CODE_ADD_ARTICLE = 4;
	public static final int REQUEST_CODE_ADD_LOCATION = 5;
	public HashMap<String, String> articleMap = new HashMap<String, String>();

	private final ArrayList<ChooseFile> paths = new ArrayList<ChooseFile>();
	private ArrayList<Target> chooses = new ArrayList<Target>();
	private ArrayList<Subject> subjects = new ArrayList<Subject>();
	/** 转播动态的ID */
	private String bid;
	/** 已选群组的ID */
	private String gid;
	private LinearLayout emoticonsLayout;
	private EmoticonsEditText imput;
	private ImageView emoticonsButton;
	private LinearLayout moreLayout, emoLayout, addLayout;
	private ObservableScrollView input_scroll;
	private ViewPager emoPager;
	private GridView picGrid;
	private PictureAdapter picAdapter;
	private TextView reward_count;
	private View clear_reward;
	private TextView range_name;
	private ImageView range_image;
	private View location_layout;
	private TextView location_dsn;

	private int reward;
	private int keyboardHeight;
	private boolean isToAll = false;
	private boolean isToCompany = true;
	private YLLocation location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		long time = System.currentTimeMillis();
		if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
			final String extra_text = getIntent().getStringExtra(
					Intent.EXTRA_TEXT);
			if (extra_text.startsWith("http")) {
				HttpHelper.getHtmlTitle(extra_text, new TitleCallback() {
					@Override
					public void onGetTitle(String title) {
						imput.setText(title + extra_text);
					}
				});
			}
			imput.setText(extra_text);
		}
		if (getIntent().hasExtra(Intent.EXTRA_STREAM)) {
			Uri uri = Uri.parse(getIntent().getExtras()
					.get(Intent.EXTRA_STREAM) + "");
			String path = FileUtil.getImageAbsolutePath(this, uri);
			Logger.e("path" + path);
			if (path != null) {
				paths.add(new ChooseFile(path, time));
				picAdapter.notifyDataSetChanged();
			}
		}
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
			if (type.startsWith("image/")) {
				ArrayList<Uri> imageUris = intent
						.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
				if (imageUris != null) {
					int N = imageUris.size();
					for (int i = 0; i < N; i++) {
						if (i > Config.MAX_PIC_CHOOSE_COUNT) {
							break;
						}
						Uri uri = imageUris.get(i);
						String path = FileUtil.getImageAbsolutePath(this, uri);
						Logger.e("path" + path);
						if (path != null) {
							paths.add(new ChooseFile(path, time));
							picAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		}
		if (!SociabilityClient.getClient().isValidCookie()) {
			Toast.makeText(this, "登陆失效，请重新登陆", Toast.LENGTH_SHORT).show();
			Intent mIntent = new Intent(this, LoginActivity.class);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			this.startActivity(mIntent);
		}

		AccumulationAPP.getInstance().loadUserData();

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
		imput.postDelayed(new Runnable() {

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
		return R.layout.activity_add_discuss;
	}

	@Override
	protected String getMenuTitle() {
		// TODO Auto-generated method stub
		return "发布";
	}

	@Override
	protected String getTopTitle() {
		if (location != null) {
			return "签到";
		} else {
			if (!TextUtils.isEmpty(bid)) {
				return "转播";
			} else {
				return "发动态";
			}
		}
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		bid = getIntent().getStringExtra("bid");
		location = (YLLocation) intent.getSerializableExtra("location");
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		initEmoView();
		location_layout = findViewById(R.id.location_layout);
		location_dsn = (TextView) findViewById(R.id.location_dsn);
		imput = (EmoticonsEditText) findViewById(R.id.broadcast_content);
		if (location != null) {
			location_dsn.setText("我在" + location.name);
			imput.setHint("这里有什么新鲜事？");
		}
		location_layout.setVisibility(location == null ? View.GONE
				: View.VISIBLE);
		emoticonsLayout = (LinearLayout) findViewById(R.id.emotion_container);
		moreLayout = (LinearLayout) findViewById(R.id.layout_more);
		emoLayout = (LinearLayout) findViewById(R.id.layout_emo);
		addLayout = (LinearLayout) findViewById(R.id.layout_add);
		emoticonsButton = (ImageView) findViewById(R.id.btn_chat_emo);
		reward_count = (TextView) findViewById(R.id.reward_count);
		clear_reward = findViewById(R.id.clear_reward);
		input_scroll = (ObservableScrollView) findViewById(R.id.input_scroll);
		range_image = (ImageView) findViewById(R.id.range_image);
		range_name = (TextView) findViewById(R.id.range_name);
		input_scroll
				.setTouchInterceptionViewGroup((ViewGroup) findViewById(R.id.scroll_container));
		clear_reward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reward = 0;
				caculateRewardShow();
			}
		});
		input_scroll
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
		imput.setOnClickListener(new OnClickListener() {

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

		findViewById(R.id.input_article).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getContext(),
								AddArticleActivity.class);
						startActivityForResult(intent, REQUEST_CODE_ADD_ARTICLE);
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
		findViewById(R.id.choose_range).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getContext(),
								ChooseTargetActivity.class);
						intent.putExtra("data", chooses);
						intent.putExtra("toCompany", isToCompany);
						intent.putExtra("toAll", isToAll);
						startActivityForResult(intent,
								REQUEST_CODE_CHOOSE_RANGE);
					}
				});

		findViewById(R.id.choose_reward).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getContext(),
								ChooseRewardActivity.class);
						intent.putExtra("reward", reward);
						startActivityForResult(intent,
								REQUEST_CODE_CHOOSE_REWARD);
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
				imput.setVisibility(View.VISIBLE);
				imput.requestFocus();
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
								imput.setVisibility(View.VISIBLE);
								imput.requestFocus();
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
		caculateRewardShow();
		caculateRangeShow();
	}

	@Override
	public void onMenu() {

		if (!isToAll && !isToCompany && gid == null) {
			toast("请选择范围");
			return;
		}

		final String groupID = gid;
		String input = imput.getText().toString();
		if ("".equals(input.trim())) {
			toast("请输入内容");
			return;
		}

		Iterator<Map.Entry<String, String>> it = articleMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			input = input.replace(entry.getKey(), entry.getValue() + " ");
		}
		final String item = input;
		Logger.e("add broadcast :" + item);
		if (isToAll) {
			DialogTips tip = new DialogTips(this, "提示",
					"动态发布到广场后，所有人都会看到，是否继续发布?", "确定", true, true);
			// 设置成功事件
			tip.setOnSuccessListener(new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialogInterface, int userId) {
					commitBroadcast(groupID, item);
				}
			});
			// 显示确认对话框
			tip.show();
			tip = null;
		} else {
			commitBroadcast(groupID, item);
		}
	}

	private void commitBroadcast(final String groupID, final String item) {
		AccumulationAPP.getInstance().requestAddBroadcast(item, bid, gid,
				paths, subjects, chooses, isToAll, isToCompany);
		finish();
	}

	private void caculateRewardShow() {
		clear_reward.setVisibility(reward > 0 ? View.VISIBLE : View.GONE);
		if (reward > 0) {
			reward_count.setText("  " + reward + "  ");
		} else {
			reward_count.setText("奖金悬赏");
		}
	}

	private void caculateRangeShow() {
		String range = "";
		int resId = 0;
		if (isToAll) {
			range = "广场";
			resId = R.drawable.compose_publicbutton;
		} else if (isToCompany) {
			range = "好友圈";
			resId = R.drawable.compose_friendcircle;
		} else if (chooses.size() > 0) {
			range = "群组";
			resId = R.drawable.compose_group;
		} else {
			range = "广场";
			resId = R.drawable.compose_publicbutton;
		}
		int N = chooses.size();
		gid = null;
		for (int i = 0; i < N; i++) {
			if (i != 0) {
				gid += ",";
			}
			gid += chooses.get(i).Id;
		}
		range_image.setImageResource(resId);
		range_name.setText(range);
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
			imput.dispatchKeyEvent(event);
			return;
		}

		String key = index.face.toString();
		try {
			if (imput != null && !TextUtils.isEmpty(key)) {
				int start = imput.getSelectionStart();
				CharSequence content = imput.getText().insert(start, key);
				imput.setText(content);
				// 定位光标位置
				CharSequence info = imput.getText();
				if (info instanceof Spannable) {
					Spannable spanText = (Spannable) info;
					Selection.setSelection(spanText, start + key.length());
				}
			}
		} catch (Exception e) {

		}
	}

	public void showSoftInputView() {
		View v = imput;
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	public void hideSoftInputView() {
		View v = imput;
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
			return Math.min(Config.MAX_PIC_CHOOSE_COUNT, paths.size() + 1);
		}

		@Override
		public ChooseFile getItem(int arg0) {
			// TODO Auto-generated method stub
			if (paths.size() < Config.MAX_PIC_CHOOSE_COUNT) {
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
		mIntent.putExtra("max", Config.MAX_PIC_CHOOSE_COUNT - paths.size());
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
		} else if (arg0 == REQUEST_CODE_CHOOSE_REWARD) {
			if (arg1 == RESULT_OK) {
				reward = arg2.getIntExtra("reward", 0);
				caculateRewardShow();
			}
		} else if (arg0 == REQUEST_CODE_CHOOSE_RANGE) {
			if (arg1 == RESULT_OK) {
				isToAll = arg2.getBooleanExtra("toAll", false);
				isToCompany = arg2.getBooleanExtra("toCompany", false);
				chooses = (ArrayList<Target>) arg2
						.getSerializableExtra("chooseGroup");
				caculateRangeShow();
			}
		} else if (arg0 == REQUEST_CODE_ADD_LOCATION) {
			if (arg1 == RESULT_OK) {

			}
		} else if (arg0 == REQUEST_CODE_ADD_ARTICLE) {
			if (arg1 == RESULT_OK) {
				Article article = (Article) arg2
						.getSerializableExtra("article");
				String articleLink = "<a href=\"" + ESTPConfig.DOMAIN
						+ "/article/index?id=" + article.Id + "\">"
						+ article.Title + "</a>";
				String linkText = Config.ARTICLE_ICON_LABEL + article.Title
						+ "#TYPE_LABLE" + MyURLSpan.ARTICLE_TYPE + "#";
				articleMap.put(linkText, articleLink);
				int start = imput.getSelectionStart();
				CharSequence content = imput.getText().insert(start,
						articleLink);
				imput.setText(content);
				imput.setCursorToEnd();
			}
		}
	}
	
	

}
