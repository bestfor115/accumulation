package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;
import java.util.List;

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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.accumulation.app.adapter.EmoticonsPagerAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.config.Config;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.dialog.DialogTips;
import com.accumulation.app.ui.LoginActivity;
import com.accumulation.app.ui.file.BrowserSelectActivity;
import com.accumulation.app.ui.file.ChoosePictureActivity;
import com.accumulation.app.ui.subject.ChooseSubjectActivity;
import com.accumulation.app.util.FileUtil;
import com.accumulation.app.util.HttpHelper;
import com.accumulation.app.util.HttpHelper.TitleCallback;
import com.accumulation.app.view.EmoticonsEditText;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.FaceHolder;
import com.accumulation.lib.sociability.data.Subject;
import com.accumulation.lib.sociability.data.Target;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.ui.indicator.CirclePageIndicator;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 发布、转播动态
 * */
public class AddBroadcastActivity extends BaseActivity implements
		KeyClickListener {
	private final ArrayList<ChooseFile> paths = new ArrayList<ChooseFile>();
	private ArrayList<Subject> subjects = new ArrayList<Subject>();
	private ArrayList<Target> chooses = new ArrayList<Target>();

	private GridView picGrid;
	private PictureAdapter picAdapter;
	private TextView chooseTarget;
	private TextView chooseTag;
	private EmoticonsEditText inoput;
	private PopupWindow popupWindow;
	private boolean isKeyBoardVisible;
	private boolean isToAll = false;
	private boolean isToCompany = true;
	private boolean isUploading = false;
	private boolean emotionInput = false;
	/** 转播动态的ID */
	private String bid;
	/** 已选群组的ID */
	private String gid;
	private int keyboardHeight;
	private LinearLayout emoticonsLayout;
	private ImageView emoticonsButton;


	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_broadcast;
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		bid = getIntent().getStringExtra("bid");
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		doPictureViewInit();
		doEmotionViewInit();
		doTargetViewInit();
		doTagViewInit();
	}

	@Override
	protected String getTopTitle() {
		if (!TextUtils.isEmpty(bid)) {
			return "转播";
		} else {
			return "发动态";
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_UP
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onBackPressed() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			return;
		}
		if (paths.size() > 0 || !TextUtils.isEmpty(inoput.getText())) {
			DialogTips dialog = new DialogTips(this, "提示", "编辑还未完成，是否退出?",
					"确定", true, true);
			// 设置成功事件
			dialog.setOnSuccessListener(new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialogInterface, int userId) {
					finish();
				}
			});
			// 显示确认对话框
			dialog.show();
			dialog = null;
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onCommit() {

		if (!isToAll && !isToCompany && gid == null) {
			toast("请选择范围");
			return;
		}

		final String groupID = gid;
		String input = inoput.getText().toString();
		if ("".equals(input.trim())) {
			toast("请输入内容");
			return;
		}

		final String item = input;
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
		// final int N = paths.size();
		// if (N > 0) {
		// APingTaiAPP.getInstance().requestAddBroadcast(item, bid, gid,paths,
		// subjects, chooses, isToAll, isToCompany);
		// finish();
		// } else {
		// final Dialog dialog = UIUtils.showLoadingDialog(getContext());
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// int count = 0;
		// String ids = "";
		// isUploading = N != 0;
		// for (int i = 0; i < N; i++) {
		// if (pause || !dialog.isShowing() || !isUploading) {
		// isUploading = false;
		// return;
		// }
		// String url = paths.get(i);
		// String id = HttpHelper.uploadFile(
		// getApplicationContext(),
		// UriConfig.IMAGE_UPDADE_URL, url, true);
		// if (!TextUtils.isEmpty(id)) {
		// ids += "," + id;
		// count++;
		// }
		// }
		// isUploading = false;
		// if (count == N) {
		// Logger.d("last ids:" + ids);
		// ServiceHelper helper = ServiceHelper.getHelper();
		// RequestParams param = new RequestParams();
		// param.put("datatype", "json");
		// if (groupID != null) {
		// param.put("sendto", groupID);
		// }
		// String input=item;
		// int length = subjects.size();
		// for (int i = 0; i < length; i++) {
		// input += "#(" + subjects.get(i).Name + ") ";
		// }
		// param.put("content", input);
		// if (ids.length() > 0) {
		// param.put("images", ids.substring(1));
		// }
		// Logger.d("originalId:" + bid);
		// if (bid != null) {
		// helper.setRootUrl(UriConfig.BROADCAST_RESEND_URL);
		// param.put("originalId", bid);
		// } else {
		// helper.setRootUrl(UriConfig.BROADCAST_SEND_URL);
		// }
		// param.put("toCompany", isToCompany + "");
		// param.put("toAll", isToAll + "");
		//
		// helper.setSerializerType(SerializerType.JSON);
		// helper.callServiceAsync(getApplicationContext(), param,
		// JsonBase.class,
		// new ResponseHandlerT<JsonBase>() {
		// @SuppressWarnings("unused")
		// public void onResponse(boolean success,
		// JsonBase result) {
		// if (!pause) {
		// if (dialog != null
		// && dialog.isShowing()) {
		// dialog.dismiss();
		// }
		// }
		// String msg = getString(R.string.msg_like_result_fail);
		// if (success && result != null) {
		// msg = result.msg;
		// if (result.success) {
		// APingTaiAPP.getInstance().isSyncBroadcast=true;
		// finish();
		// }
		// }
		// UIUtils.showTast(
		// getApplicationContext(), msg);
		// }
		// });
		// } else {
		// Logger.d("commit canceled");
		// }
		// }
		// }).start();
		// }

	}

	/**
	 * 初始化图片选择的界面
	 * */
	private void doPictureViewInit() {
		picGrid = (GridView) findViewById(R.id.pictures);
		picGrid.setAdapter(picAdapter = new PictureAdapter());
	}

	/**
	 * 初始化发布对象界面
	 * */
	private void doTargetViewInit() {
		chooseTarget = (TextView) findViewById(R.id.broadcast_target);
		// int N = APingTaiAPP.getInstance().targets.size();
		// if (N > 0) {
		// chooseTarget
		// .setTag(APingTaiAPP.getInstance().targets.get(N - 1).Id);
		// chooseTarget
		// .setText(APingTaiAPP.getInstance().targets.get(N - 1).Name);
		// }
		findViewById(R.id.choose_target).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent mIntent = new Intent(getApplication(),
								ChooseTargetActivity.class);
						mIntent.putExtra("data", chooses);
						mIntent.putExtra("toCompany", isToCompany);
						mIntent.putExtra("toAll", isToAll);
						startActivityForResult(mIntent, 1);
					}
				});
	}

	private void doTagViewInit() {
		chooseTag = (TextView) findViewById(R.id.choose_tag);
		chooseTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(getContext(),
						ChooseSubjectActivity.class);
				mIntent.putExtra("data", subjects);
				startActivityForResult(mIntent, 5);
			}
		});
	}

	private void enablePopUpView() {
		View popupView = getLayoutInflater().inflate(
				R.layout.pop_emoticons_comment, null);
		final ViewPager pager = (ViewPager) popupView
				.findViewById(R.id.emoticons_pager);
		final CirclePageIndicator mIndicator = (CirclePageIndicator) popupView
				.findViewById(R.id.indicator);
		float density = this.getResources().getDisplayMetrics().density;
		mIndicator.setRadius(5 * density);
		mIndicator.setFillColor(0xFF000000);
		mIndicator.setStrokeWidth(1 * density);
		pager.setOffscreenPageLimit(3);
		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(this,
				AccumulationAPP.getInstance().faces, this);
		pager.setAdapter(adapter);
		mIndicator.setViewPager(pager);
		popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				emoticonsLayout.setVisibility(LinearLayout.GONE);
				emoticonsButton
						.setImageResource(R.drawable.btn_chat_emo_selector);
			}
		});
	}

	// 显示软键盘
	public void showSoftInputView() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(inoput, InputMethodManager.SHOW_FORCED);
	}

	class PictureAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
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
					if (isUploading) {
						toast("等待上传任务结束");
						return;
					}
					if (url.flagTime < 0) {
						startChoosePicActivity();
					} else {
						Intent intent = new Intent(getApplication(),
								BrowserSelectActivity.class);
						intent.putExtra("photos", paths);
						intent.putExtra("position", arg0);
						startActivityForResult(intent, 6);
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
		startActivityForResult(mIntent, 0);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 0) {
			if (arg1 == RESULT_OK) {
				ArrayList<ChooseFile> selects = (ArrayList<ChooseFile>) arg2
						.getSerializableExtra("result");
				paths.addAll(selects);
				picAdapter.notifyDataSetChanged();
			}
		} else if (arg0 == 6) {
			if (arg1 == RESULT_OK) {
				paths.clear();
				ArrayList<ChooseFile> selects = (ArrayList<ChooseFile>) arg2
						.getSerializableExtra("result");
				paths.addAll(selects);
				picAdapter.notifyDataSetChanged();
			}
		} else if (arg0 == 1) {
			if (arg1 == RESULT_OK) {
				isToAll = arg2.getBooleanExtra("toAll", false);
				isToCompany = arg2.getBooleanExtra("toCompany", false);
				chooses = (ArrayList<Target>) arg2
						.getSerializableExtra("chooseGroup");
				gid = arg2.getStringExtra("gid");
				String show = arg2.getStringExtra("show");
				if (show != null) {
					chooseTarget.setText(show);
				}
			}
		} else if (arg0 == 5) {
			if (arg1 == RESULT_OK) {
				subjects.clear();
				subjects.addAll((List) arg2.getSerializableExtra("data"));
				caculateBroadcastTag();
			}
		} else {
			if (arg1 == RESULT_OK) {
				String alt_user = arg2.getStringExtra("target_email");
				if (alt_user != null) {
					int start = inoput.getSelectionStart();
					CharSequence content = inoput.getText().insert(start,
							alt_user);
					inoput.setText(content);
					inoput.setCursorToEnd();
				}
			}
		}
	}

	private void caculateBroadcastTag() {
		if (subjects.size() == 0) {
			chooseTag.setText("标记");
			chooseTag.setTextColor(getResources().getColor(
					R.color.description_txt_color));
		} else {
			int N = subjects.size();
			String show = "";
			for (int i = 0; i < N; i++) {
				if (i == 0) {
					show += "" + subjects.get(i).Name;
				} else {
					show += "," + subjects.get(i).Name;
				}
			}
			chooseTag.setText(show);
			chooseTag.setTextColor(Color.BLACK);
		}
	}

	@Override
	public void keyClickedIndex(final FaceHolder index) {

		if (-1 == index.index) {
			KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0,
					0, 0, KeyEvent.KEYCODE_ENDCALL);
			inoput.dispatchKeyEvent(event);
			return;
		}

		String key = index.face.toString();
		try {
			if (inoput != null && !TextUtils.isEmpty(key)) {
				int start = inoput.getSelectionStart();
				CharSequence content = inoput.getText().insert(start, key);
				inoput.setText(content);
				// 定位光标位置
				CharSequence info = inoput.getText();
				if (info instanceof Spannable) {
					Spannable spanText = (Spannable) info;
					Selection.setSelection(spanText, start + key.length());
				}
			}
		} catch (Exception e) {

		}
	}

	private void allGolablLayoutListener(final View parentLayout) {
		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);
						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);
						keyboardHeight = heightDifference;
						emoticonsLayout.setPadding(0, 0, 0, heightDifference);
						emoticonsLayout
								.setVisibility(heightDifference > 100 ? View.VISIBLE
										: View.GONE);
					}
				});
	}

	private void doEmotionViewInit() {
		inoput = (EmoticonsEditText) findViewById(R.id.broadcast_content);
		emoticonsLayout = (LinearLayout) findViewById(R.id.emotion_container);
		emoticonsButton = (ImageView) findViewById(R.id.btn_chat_emo);
		emoticonsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				emotionInput = !emotionInput;
				caculateEmotionShow();
				if (emotionInput) {
					popupWindow.setHeight(keyboardHeight);
					popupWindow
							.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
				} else {
					popupWindow.dismiss();
				}
			}
		});
//		findViewById(R.id.btn_chat_alt).setOnClickListener(
//				new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Intent mIntent = new Intent(getApplication(),
//								UserSelectActivity.class);
//						startActivityForResult(mIntent, 3);
//					}
//				});
		enablePopUpView();
		allGolablLayoutListener(rootLayout);

	}

	private void setEmotionInput(boolean ffag) {
		emotionInput = ffag;
		caculateEmotionShow();
	}

	private void caculateEmotionShow() {
		emoticonsButton
				.setImageResource(emotionInput ? R.drawable.btn_chat_keyboard_selector
						: R.drawable.btn_chat_emo_selector);
	}

}
