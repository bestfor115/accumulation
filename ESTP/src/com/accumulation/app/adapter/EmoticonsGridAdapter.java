package com.accumulation.app.adapter;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.accumulation.app.R;
import com.accumulation.lib.sociability.data.FaceHolder;

public class EmoticonsGridAdapter extends BaseAdapter {

	private ArrayList<FaceHolder> paths;
	private int pageNumber;
	private Context context;
	private KeyClickListener listener;

	public EmoticonsGridAdapter(Context context, ArrayList<FaceHolder> paths,
			int pageNumber, KeyClickListener listener) {
		this.context = context;
		this.paths = paths;
		this.pageNumber = pageNumber;
		this.listener = listener;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_emoticons_grid, null);
		}

		final FaceHolder face = getItem(position);
		ImageView image = (ImageView) v.findViewById(R.id.item);
		image.setImageDrawable(getGIFDraable(face));
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.keyClickedIndex(face);
			}
		});
		return v;
	}

	private Drawable getGIFDraable(FaceHolder holder) {
		try {
			if (holder.index >= 0) {
				AssetManager assetManager = context.getAssets();
				return new GifDrawable(assetManager, holder.face.path);
			}else{
				return context.getResources().getDrawable(R.drawable.th);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getCount() {
		return paths.size();
	}

	@Override
	public FaceHolder getItem(int position) {
		return paths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public interface KeyClickListener {

		public void keyClickedIndex(FaceHolder index);
	}
}
