package com.accumulation.app.ui.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.accumulation.app.adapter.EmoticonsPagerAdapter;
import com.accumulation.app.adapter.MessageChatAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.config.Config;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.app.ui.broadcast.UserSelectActivity;
import com.accumulation.app.util.UIUtils;
import com.accumulation.app.view.EmoticonsEditText;
import com.accumulation.app.view.xlist.XListView;
import com.accumulation.app.view.xlist.XListView.IXListViewListener;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.FaceHolder;
import com.accumulation.lib.sociability.data.Image;
import com.accumulation.lib.sociability.data.JDMessage;
import com.accumulation.lib.sociability.data.JLMessage;
import com.accumulation.lib.sociability.data.MessageItem;
import com.accumulation.lib.sociability.im.IMManager;
import com.accumulation.lib.sociability.im.MessageReceiver;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.tool.time.TimeFormateTool;
import com.accumulation.lib.ui.indicator.CirclePageIndicator;
import com.google.gson.Gson;

/**
 * 锟斤拷锟芥：锟斤拷某锟剿碉拷私锟斤拷锟斤拷史
 * */
public class UserChatActivity extends BaseActivity implements
		IXListViewListener, OnClickListener, KeyClickListener, MessageReceiver {
	private XListView listView;
	private BaseAdapter adapter;
	private LinkedList<MessageItem> chats = new LinkedList<MessageItem>();
	private boolean first = true;
	private Button emoBtn, sendBtn, addBtn, keyboardBtn, speakBtn, voiceBtn;
	EmoticonsEditText input;
	private LinearLayout moreLayout, emoLayout, addLayout;
	private TextView pictureTxt, cameraTxt, locationTxt;
	// 锟斤拷锟斤拷锟叫癸拷
	RelativeLayout recrodLayout;
	TextView voiceTip;
	ImageView recordIcon;

	private Drawable[] animations;// 锟斤拷筒锟斤拷锟斤拷
	private ViewPager emoPager;

	private String talkName, talkId, talkHeader;
	Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// JMessageClient.registerEventReceiver(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IMManager.getManager().enterSingleConversaion(talkId);
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(0);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		IMManager.getManager().exitConversaion();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// JMessageClient.unRegisterEventReceiver(this);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_user_chat;
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		talkName = intent.getStringExtra("target_name");
		talkId = intent.getStringExtra("target_id");
		talkHeader = intent.getStringExtra("target_header");
	}

	@Override
	protected int getMenuRes() {
		// TODO Auto-generated method stub
		return R.drawable.icon_user;
	}

	@Override
	protected void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		Intent intent = new Intent(this, ChatSettingActivity.class);
		intent.putExtra("target_id", talkId);
		startActivity(intent);
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
	}

	public void initView() {
		listView = (XListView) findViewById(R.id.data_list);
		listView.setXListViewListener(this);
		listView.mShowExtra = false;
		listView.setPullLoadEnable(false);
		listView.setPullRefreshEnable(false);
		listView.setAdapter(adapter = new MessageChatAdapter(this, chats));
		listView.setSelector(new ColorDrawable(0));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		initBottomView();
		initVoiceView();
		loadData(true);
		allGolablLayoutListener(rootLayout);
	}

	private void scrollToButton() {
		rootLayout.requestLayout();
		if (listView.getFirstVisiblePosition() + listView.getChildCount() >= chats
				.size() - 1) {
			listView.setSelection(chats.size() - 1);
			listView.setSelection(listView.getBottom());
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
						rootLayout.setPadding(0, statusBarHeight, 0,
								heightDifference);
						// if (heightDifference > 0) {
						// scrollToButton();
						// }
						// findViewById(R.id.bar_layout).setPadding(0, 0, 0,
						// heightDifference);
						// findViewById(R.id.content_layout).setPadding(0, 0, 0,
						// heightDifference);
					}
				});
	}

	private void initAddView() {
		pictureTxt = (TextView) findViewById(R.id.tv_picture);
		cameraTxt = (TextView) findViewById(R.id.tv_camera);
		locationTxt = (TextView) findViewById(R.id.tv_location);
		pictureTxt.setOnClickListener(this);
		locationTxt.setOnClickListener(this);
		cameraTxt.setOnClickListener(this);
	}

	private void initBottomView() {
		// 锟斤拷锟斤拷锟�
		addBtn = (Button) findViewById(R.id.btn_chat_add);
		emoBtn = (Button) findViewById(R.id.btn_chat_emo);
		addBtn.setOnClickListener(this);
		emoBtn.setOnClickListener(this);
		// 锟斤拷锟揭憋拷
		keyboardBtn = (Button) findViewById(R.id.btn_chat_keyboard);
		voiceBtn = (Button) findViewById(R.id.btn_chat_voice);
		voiceBtn.setOnClickListener(this);
		keyboardBtn.setOnClickListener(this);
		sendBtn = (Button) findViewById(R.id.btn_chat_send);
		sendBtn.setOnClickListener(this);
		// 锟斤拷锟斤拷锟斤拷
		moreLayout = (LinearLayout) findViewById(R.id.layout_more);
		emoLayout = (LinearLayout) findViewById(R.id.layout_emo);
		addLayout = (LinearLayout) findViewById(R.id.layout_add);
		initAddView();
		initEmoView();
		// 锟斤拷锟叫硷拷
		// 锟斤拷锟斤拷锟斤拷
		speakBtn = (Button) findViewById(R.id.btn_speak);
		// 锟斤拷锟斤拷锟�
		input = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		input.setOnClickListener(this);
		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(s)) {
					sendBtn.setVisibility(View.VISIBLE);
					keyboardBtn.setVisibility(View.GONE);
					voiceBtn.setVisibility(View.GONE);
				} else {
					if (voiceBtn.getVisibility() != View.VISIBLE) {
						voiceBtn.setVisibility(View.VISIBLE);
						sendBtn.setVisibility(View.GONE);
						keyboardBtn.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * 锟斤拷始锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	 * 
	 * @Title: initVoiceView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initVoiceView() {
		recrodLayout = (RelativeLayout) findViewById(R.id.layout_record);
		voiceTip = (TextView) findViewById(R.id.tv_voice_tips);
		recordIcon = (ImageView) findViewById(R.id.iv_record);
		speakBtn.setOnTouchListener(new VoiceTouchListen());
		initVoiceAnimRes();
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

	private void initVoiceAnimRes() {
		animations = new Drawable[] {
				getResources().getDrawable(R.drawable.chat_icon_voice2),
				getResources().getDrawable(R.drawable.chat_icon_voice3),
				getResources().getDrawable(R.drawable.chat_icon_voice4),
				getResources().getDrawable(R.drawable.chat_icon_voice5),
				getResources().getDrawable(R.drawable.chat_icon_voice6) };
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return talkName;
	}

	@Override
	public void onRefresh() {
		loadData(true);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}

	private void loadData(final boolean head) {
		SociabilityClient.getClient().getMessage(talkId, 1, 100,
				new BaseCallback<JLMessage>() {
					@Override
					public void onResultCallback(int code, String message,
							JLMessage result) {
						// TODO Auto-generated method stub
						if (code >= 0) {
							chats.clear();
							chats.addAll(result.data.list);
							Collections.sort(chats, new MessageComparator());
						} else {
							toast(message);
						}
						listView.noticeFinish();
						adapter.notifyDataSetChanged();
						listView.setSelection(adapter.getCount() - 1);
					}
				}, JLMessage.class);
	}

	public class MessageComparator implements Comparator<MessageItem> {

		public int compare(MessageItem o1, MessageItem o2) {

			return o1.publishTimeAccurate.compareTo(o2.publishTimeAccurate);
		}
	}

	@Override
	public void keyClickedIndex(FaceHolder index) {
		// TODO Auto-generated method stub
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
				// 锟斤拷位锟斤拷锟轿伙拷锟�
				CharSequence info = input.getText();
				if (info instanceof Spannable) {
					Spannable spanText = (Spannable) info;
					Selection.setSelection(spanText, start + key.length());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit_user_comment:// 锟斤拷锟斤拷谋锟斤拷锟斤拷锟斤拷

			if (moreLayout.getVisibility() == View.VISIBLE) {
				addLayout.setVisibility(View.GONE);
				emoLayout.setVisibility(View.GONE);
				moreLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_chat_emo:// 锟斤拷锟叫︼拷锟酵硷拷锟�
			if (moreLayout.getVisibility() == View.GONE) {
				showEditState(true);
			} else {
				if (addLayout.getVisibility() == View.VISIBLE) {
					addLayout.setVisibility(View.GONE);
					emoLayout.setVisibility(View.VISIBLE);
				} else {
					moreLayout.setVisibility(View.GONE);
				}
			}

			break;
		case R.id.btn_chat_add:// 锟斤拷影锟脚�-锟斤拷示图片锟斤拷锟斤拷锟秸★拷位锟斤拷
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

			break;
		case R.id.btn_chat_voice:// 锟斤拷锟斤拷锟斤拷钮
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

			try {
				startActivityForResult(intent, Config.REQUESTCODE_TAKE_SPEECH);
			} catch (ActivityNotFoundException a) {
			}
			// edit_user_comment.setVisibility(View.GONE);
			// layout_more.setVisibility(View.GONE);
			// btn_chat_voice.setVisibility(View.GONE);
			// btn_chat_keyboard.setVisibility(View.VISIBLE);
			// btn_speak.setVisibility(View.VISIBLE);
			// hideSoftInputView();
			break;
		case R.id.btn_chat_keyboard:// 锟斤拷锟教帮拷钮锟斤拷锟斤拷锟斤拷偷锟斤拷锟斤拷锟斤拷滩锟斤拷锟斤拷氐锟斤拷锟斤拷锟斤拷锟脚�
			showEditState(false);
			break;
		case R.id.tv_camera:// 锟斤拷锟斤拷
			selectImageFromCamera();
			break;
		case R.id.tv_picture:// 图片
			selectImageFromLocal();
			break;
		case R.id.tv_location:// 位锟斤拷
			selectLocationFromMap();
			break;
		case R.id.btn_chat_send:// 锟斤拷锟斤拷锟侥憋拷
			final String msg = input.getText().toString();
			if (msg.equals("")) {
				UIUtils.showTast(getApplicationContext(), "锟斤拷锟斤拷锟诫发锟斤拷锟斤拷息!");
				return;
			}
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected) {
				UIUtils.showTast(getApplicationContext(), R.string.network_tips);
				return;
			}
			input.setText("");
			// showEditState(false);
			appendMessage(MESSAGE_TYPE_TEXT, msg);
			break;
		default:
			break;
		}
		scrollToButton();
		listView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scrollToButton();
			}
		}, 300);
	}

	/**
	 * 锟斤拷锟斤拷锟角凤拷锟斤拷笑锟斤拷锟斤拷锟斤拷示锟侥憋拷锟斤拷锟斤拷锟斤拷状态
	 * 
	 * @Title: showEditState
	 * @Description: TODO
	 * @param @param isEmo: 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟街和憋拷锟斤拷
	 * @return void
	 * @throws
	 */
	private void showEditState(boolean isEmo) {
		input.setVisibility(View.VISIBLE);
		keyboardBtn.setVisibility(View.GONE);
		voiceBtn.setVisibility(View.VISIBLE);
		speakBtn.setVisibility(View.GONE);
		input.requestFocus();
		if (isEmo) {
			moreLayout.setVisibility(View.VISIBLE);
			moreLayout.setVisibility(View.VISIBLE);
			emoLayout.setVisibility(View.VISIBLE);
			addLayout.setVisibility(View.GONE);
			hideSoftInputView();
		} else {
			moreLayout.setVisibility(View.GONE);
			showSoftInputView();
		}

	}

	// 锟斤拷示锟斤拷锟斤拷锟�
	public void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(input, 0);
		}
	}

	/**
	 * 锟斤拷锟斤拷说锟斤拷
	 * 
	 * @ClassName: VoiceTouchListen
	 * @Description: TODO
	 * @author smile
	 * @date 2014-7-1 锟斤拷锟斤拷6:10:16
	 */
	class VoiceTouchListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				return true;
			case MotionEvent.ACTION_MOVE: {
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recrodLayout.setVisibility(View.INVISIBLE);
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * 锟斤拷锟斤拷锟斤拷图
	 * 
	 * @Title: selectLocationFromMap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void selectLocationFromMap() {
		Intent mIntent = new Intent(getApplication(), UserSelectActivity.class);
		startActivityForResult(mIntent, Config.REQUESTCODE_TAKE_ALT);
	}

	private String localCameraPath = "";// 锟斤拷锟秸猴拷玫锟斤拷锟酵计拷锟街�

	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟� startCamera
	 * 
	 * @Title: startCamera
	 * @throws
	 */
	public void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(Config.FILE_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		localCameraPath = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, Config.REQUESTCODE_TAKE_CAMERA);
	}

	/**
	 * 选锟斤拷图片
	 * 
	 * @Title: selectImage
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, Config.REQUESTCODE_TAKE_LOCAL);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Config.REQUESTCODE_TAKE_CAMERA:// 锟斤拷取锟斤拷值锟斤拷时锟斤拷锟斤拷洗锟絧ath路锟斤拷锟铰碉拷图片锟斤拷锟斤拷锟斤拷锟斤拷
				appendMessage(MESSAGE_TYPE_IMAGE, localCameraPath);
				break;
			case Config.REQUESTCODE_TAKE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						if (localSelectPath == null
								|| localSelectPath.equals("null")) {
							UIUtils.showTast(getApplicationContext(),
									"锟揭诧拷锟斤拷锟斤拷锟斤拷要锟斤拷图片");
							return;
						}
						appendMessage(MESSAGE_TYPE_IMAGE, localSelectPath);
					}
				}
				break;
			case Config.REQUESTCODE_TAKE_LOCATION:// 锟斤拷锟斤拷位锟斤拷
				double latitude = data.getDoubleExtra("x", 0);// 维锟斤拷
				double longtitude = data.getDoubleExtra("y", 0);// 锟斤拷锟斤拷
				String address = data.getStringExtra("address");
				if (address != null && !address.equals("")) {
					// sendLocationMessage(address, latitude, longtitude);
				} else {
					UIUtils.showTast(getApplicationContext(),
							"锟睫凤拷锟斤拷取锟斤拷锟斤拷锟斤拷位锟斤拷锟斤拷息!");
				}
				break;
			case Config.REQUESTCODE_TAKE_ALT:
				if (resultCode == RESULT_OK) {
					String alt_user = data.getStringExtra("target_email");
					if (alt_user != null) {
						int start = input.getSelectionStart();
						CharSequence content = input.getText().insert(start,
								alt_user);
						input.setText(content);
						input.setCursorToEnd();
						showEditState(false);
						sendBtn.setVisibility(View.VISIBLE);
						keyboardBtn.setVisibility(View.GONE);
						voiceBtn.setVisibility(View.GONE);
					}
				}
				break;
			case Config.REQUESTCODE_TAKE_SPEECH:
				if (resultCode == RESULT_OK && null != data) {
					ArrayList<String> text = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					input.setText(text.get(0));
				}
				break;
			}
		}
	}

	public final static int MESSAGE_TYPE_TEXT = 0;
	public final static int MESSAGE_TYPE_IMAGE = 1;
	public final static int MESSAGE_TYPE_FILE = 2;

	public void appendMessage(int type, final Object message) {

		if (type == MESSAGE_TYPE_TEXT) {
			if ("".equals(message + "")) {
				toast("锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷");
				return;
			}
			Logger.e("1    =============" + talkId);
			final MessageItem item = new MessageItem();
			item.sender = SaveManager.getInstance(this).getLoginId();
			item.toUserId = talkId;
			item.senderId = SaveManager.getInstance(this).getLoginId();
			item.content = message + "";
			item.headpic=SaveManager.getInstance(this).getSaveHeader();
			item.publishTimeAccurate = TimeFormateTool.dateToString(new Date(),
					TimeFormateTool.FORMAT_DATE_TIME);
			item.state = MessageItem.COMMITING;
			chats.add(item);
			adapter.notifyDataSetChanged();
			SociabilityClient.getClient().sendMessage(item, null,
					new BaseCallback<JDMessage>() {

						@Override
						public void onResultCallback(int code, String message,
								JDMessage result) {
							// TODO Auto-generated method stub
							if (code >= 0) {
								int index = chats.indexOf(item);
								chats.add(index, result.data);
								chats.remove(item);
								IMManager.getManager().sendMessage(result.data,
										talkId);
							} else {
								item.state = MessageItem.ERROR;
							}
							adapter.notifyDataSetChanged();
							listView.setSelection(adapter.getCount() - 1);
						}
					}, JDMessage.class);
		} else if (type == MESSAGE_TYPE_IMAGE) {
			final MessageItem item = new MessageItem();
			item.sender = SaveManager.getInstance(this).getLoginId();
			item.toUserId = talkId;
			item.headpic=SaveManager.getInstance(this).getSaveHeader();
			item.senderId = SaveManager.getInstance(this).getLoginId();
			item.content = "";
			item.publishTimeAccurate = TimeFormateTool.dateToString(new Date(),
					TimeFormateTool.FORMAT_DATE_TIME);
			item.state = MessageItem.COMMITING;
			ArrayList<Image> images = new ArrayList<Image>();
			for (int i = 0; i < 1; i++) {
				int[] wh = AccumulationAPP.getInstance().getImageWH(
						message + "");
				Image image = new Image();
				image.ThumbImg = message + "";
				image.SourceImg = message + "";
				image.AccessURL = message + "";
				image.Width = wh[0];
				image.Height = wh[1];
				images.add(image);
			}
			item.ImageList = images;
			chats.add(item);
			adapter.notifyDataSetChanged();
			SociabilityClient.getClient().sendMessage(item, message + "",
					new BaseCallback<JDMessage>() {

						@Override
						public void onResultCallback(int code, String message,
								JDMessage result) {
							// TODO Auto-generated method stub
							if (code >= 0) {
								result.data.state = MessageItem.DONE;
								IMManager.getManager().sendMessage(result.data,
										talkId);
								int index = chats.indexOf(item);
								chats.add(index, result.data);
								chats.remove(item);
							} else {
								item.state = MessageItem.ERROR;
							}
							adapter.notifyDataSetChanged();
							listView.setSelection(adapter.getCount() - 1);
						}
					}, JDMessage.class);
		}

	}

	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onReceiverMessage(MessageItem message) {
		// TODO Auto-generated method stub
		chats.add(message);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();
				listView.setSelection(adapter.getCount());
			}
		});
	}

}
