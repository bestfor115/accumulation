package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.config.Config;
import com.accumulation.app.data.ChooseFile;
import com.accumulation.app.ui.broadcast.AddBroadcastActivity.PictureAdapter;
import com.accumulation.app.ui.file.BrowserSelectActivity;
import com.accumulation.app.ui.file.ChoosePictureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddTopicActivity extends BaseActivity {
	private final ArrayList<ChooseFile> paths = new ArrayList<ChooseFile>();

	private GridView picGrid;

	private PictureAdapter picAdapter;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_topic;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "Ω®ª∞Ã‚";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		picGrid = (GridView) findViewById(R.id.pictures);
		picGrid.setAdapter(picAdapter = new PictureAdapter());
	}

	class PictureAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Math
					.min(Config.MAX_TOPIC_PIC_CHOOSE_COUNT, paths.size() + 1);
		}

		@Override
		public ChooseFile getItem(int arg0) {
			// TODO Auto-generated method stub
			if (paths.size() < Config.MAX_TOPIC_PIC_CHOOSE_COUNT) {
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
						startActivityForResult(intent, 6);
					}
				}
			});
			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					paths.remove(url);
					notifyDataSetChanged();
				}
			});
			image.setBackgroundColor(Color.TRANSPARENT);
			if (url.flagTime < 0) {
				ImageLoader.getInstance().displayImage("file://" + url.path,
						image);
				image.setImageResource(R.drawable.choose_image);
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
		mIntent.putExtra("max",
				Config.MAX_TOPIC_PIC_CHOOSE_COUNT - paths.size());
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
		}
	}
}
