package com.accumulation.app.ui.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.adapter.DirectFileListAdapter;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.config.Config;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.util.FileTraversal;
import com.accumulation.app.util.UIUtils;
import com.accumulation.app.util.Util;
import com.accumulation.lib.tool.net.imgcache.SharedImageFetcher;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 界面：选择图片
 * */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ChoosePictureActivity extends BaseActivity implements
		OnItemClickListener {
	private final ArrayList<ChooseFile> pics = new ArrayList<ChooseFile>();
	private final ArrayList<ChooseFile> chooses = new ArrayList<ChooseFile>();

	private GridView picGrid;
	private PictureAdapter adapter;
	private TextView directory, preview;
	private View chooseLayout;
	private ListView directoryListView;
	private Util util;
	private List<FileTraversal> locallist = new ArrayList<FileTraversal>();
	private long time;
	private int max;
	private DirectFileListAdapter listAdapter;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_choose_picture;
	}

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		time = intent.getLongExtra("time", 0);
		max = intent.getIntExtra("max", 0);
		util = new Util(this, time);
		startLoadImages();
	}

	public void startLoadImages() {
		final Dialog dialog = UIUtils.createLoadingDialog(this, true);
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				locallist = util.LocalImgFileList();
				Collections.sort(locallist, new DirectComparator());
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						List<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
						if (locallist != null) {
							for (int i = 0; i < locallist.size(); i++) {
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("filecount",
										locallist.get(i).filecontent.size()
												+ "张");
								map.put("imgpath",
										locallist.get(i).filecontent.size()==0||locallist.get(i).filecontent.get(0) == null ? null
												: (locallist.get(i).filecontent
														.get(0)).path);
								map.put("filename", locallist.get(i).filename);
								listdata.add(map);
							}
						}
						pics.clear();
						directory.setText(locallist.get(0).filename);
						pics.addAll(locallist.get(0).filecontent);
						adapter.notifyDataSetChanged();
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						directoryListView
								.setAdapter(listAdapter = new DirectFileListAdapter(
										getContext(), listdata, util));
						picGrid.setAdapter(adapter = new PictureAdapter());
					}
				});
			}
		}).start();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		directory = (TextView) findViewById(R.id.directory);
		preview = (TextView) findViewById(R.id.preview);
		chooseLayout = findViewById(R.id.choose_container);
		directoryListView = (ListView) findViewById(R.id.directory_list);
		directoryListView.setAdapter(listAdapter);
		directoryListView.setOnItemClickListener(this);
		chooseLayout.setAlpha(0);
		directoryListView.setVisibility(View.GONE);

		chooseLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return chooseLayout.getAlpha() != 0;
			}
		});
		directory.setOnClickListener(new OnClickListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (chooseLayout.getAlpha() != 0) {
					chooseLayout.animate().alpha(0);
					directoryListView.setVisibility(View.GONE);
				} else {
					chooseLayout.animate().alpha(1);
					directoryListView.setVisibility(View.VISIBLE);
				}
			}
		});
		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (chooses.size() > 0) {
					Intent intent = new Intent(getContext(),
							previewImagerActivity.class);
					intent.putExtra("seleccts", chooses);
					intent.putExtra("photos", chooses);
					intent.putExtra("position", 0);
					intent.putExtra("max", max);
					startActivityForResult(intent, 0);
				}
			}
		});
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.commit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// 把返回数据存入Intent
				intent.putExtra("result", chooses);
				// 设置返回数据
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		picGrid = (GridView) findViewById(R.id.pictures);
		// mPictures.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		picGrid.setAdapter(adapter = new PictureAdapter());
		SharedImageFetcher.getSharedFetcher(getApplicationContext())
				.clearTasks();
		picGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				ChooseFile path = (ChooseFile) arg1.getTag();
				if (!chooses.contains(path)) {
					if (max>=0&&chooses.size() >= max) {
						toast(R.string.msg_choose_pic_count);
						return;
					}
					chooses.add(path);
				} else {
					chooses.remove(path);
				}
				checkValid();
				adapter.notifyDataSetChanged();
			}
		});
		checkValid();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public class DirectComparator implements Comparator<FileTraversal> {

		public int compare(FileTraversal o1, FileTraversal o2) {
			String key1 = o1.filename;
			String key2 = o2.filename;

			if (key1.equals("所有图片")) {
				return -1;
			}
			if (key2.equals("所有图片")) {
				return 1;
			}

			if (key1.equals("Camera")) {
				return -1;
			}
			if (key2.equals("Camera")) {
				return 1;
			}

			if (key1.equals("Screenshots")) {
				return -1;
			}
			if (key2.equals("Screenshots")) {
				return 1;
			}
			return key1.compareTo(key2);
		}
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "选择图片";
	}

	private void checkValid() {
		if (chooses.size() > 0) {
			if(max>=0){
				preview.setText("预览 (" + chooses.size() + "/" + max + ")");
			}else{
				preview.setText("预览 (" + chooses.size() + ")");
			}
		} else {
			preview.setText("预览");
		}
		commitView.setEnabled(chooses.size() > 0);
	}

	int count = 0;

	class PictureAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pics.size() + 1;
		}

		@Override
		public ChooseFile getItem(int arg0) {
			// TODO Auto-generated method stub
			if (arg0 == 0) {
				return null;
			}
			return pics.get(arg0 - 1);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(ChoosePictureActivity.this)
						.inflate(R.layout.item_pic_choose, null);
			}
			final ChooseFile entry = getItem(position);
			ImageView mImgView = (ImageView) convertView
					.findViewById(R.id.pic_img);
			ImageView mSecletView = (ImageView) convertView
					.findViewById(R.id.select);
			final View mask = convertView.findViewById(R.id.mask);
			mSecletView.setVisibility(entry == null ? View.GONE : View.VISIBLE);
			if (entry == null) {
				ViewHelper.setAlpha(mask, 0.0f);
				ImageLoader.getInstance().displayImage("", mImgView);
				mImgView.setImageResource(R.drawable.slr_camera);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (chooses.size() >= max&&max>=0) {
							toast(R.string.msg_choose_pic_count);
							return;
						}
						selectImageFromCamera();
					}
				});
			} else {
				mImgView.setImageResource(R.drawable.message_placeholder_picture);
				boolean checked = chooses.contains(entry);
				ViewHelper.setAlpha(mask, checked ? 1.0f : 0.0f);
				mSecletView
						.setImageResource(checked ? R.drawable.mini_check_selected
								: R.drawable.mini_check_red);
				String url = "file://" + entry.path;
				ImageLoader.getInstance().displayImage(url, mImgView);
				mSecletView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!chooses.contains(entry)) {
							if (max>=0&&chooses.size() >= max) {
								toast(R.string.msg_choose_pic_count);
								return;
							}
							chooses.add(entry);
						} else {
							chooses.remove(entry);
						}
						checkValid();
						adapter.notifyDataSetChanged();
					}
				});
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getContext(),
								previewImagerActivity.class);
						intent.putExtra("seleccts", chooses);
						intent.putExtra("photos", pics);
						intent.putExtra("position", position - 1);
						intent.putExtra("max", max);
						startActivityForResult(intent, 0);
					}
				});
			}

			convertView.setTag(entry);
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		View choose = view.findViewById(R.id.current_choose);
		if (choose != null) {
			choose.setVisibility(View.VISIBLE);
		}
		chooseLayout.animate().alpha(0);
		directoryListView.setVisibility(View.GONE);
		pics.clear();
		listAdapter.current_path = locallist.get(position).filename;
		listAdapter.notifyDataSetChanged();
		directory.setText(listAdapter.current_path);
		pics.addAll(locallist.get(position).filecontent);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (directoryListView.isShown()) {
			chooseLayout.animate().alpha(0);
			directoryListView.setVisibility(View.GONE);
			return;
		}
		super.onBackPressed();

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 0 && arg1 == RESULT_OK) {
			chooses.clear();
			ArrayList<ChooseFile> selects = (ArrayList<ChooseFile>) arg2
					.getSerializableExtra("result");
			chooses.addAll(selects);
			adapter.notifyDataSetChanged();
			checkValid();
			boolean commit = arg2.getBooleanExtra("commit", false);
			if (commit) {
				Intent intent = new Intent();
				// 把返回数据存入Intent
				intent.putExtra("result", chooses);
				// 设置返回数据
				setResult(RESULT_OK, intent);
				finish();
			}
		}
		if (arg0 == Config.REQUESTCODE_TAKE_CAMERA && arg1 == RESULT_OK) {
			ChooseFile camera = new ChooseFile();
			camera.path = localCameraPath;
			camera.flagTime = time;
			chooses.add(camera);
			Intent intent = new Intent();
			// 把返回数据存入Intent
			intent.putExtra("result", chooses);
			// 设置返回数据
			setResult(RESULT_OK, intent);
			finish();
		}

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
}
