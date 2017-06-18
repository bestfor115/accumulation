package com.accumulation.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

import com.accumulation.app.R;
import com.accumulation.app.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.accumulation.lib.sociability.data.FaceHolder;

public class EmoticonsPagerAdapter extends PagerAdapter {
	private static final int NO_OF_EMOTICONS_PER_PAGE = 21;
	List<FaceHolder> emoticons;
	FragmentActivity activity;
	KeyClickListener listener;

	public EmoticonsPagerAdapter(FragmentActivity activity,
			List<FaceHolder> emoticons, KeyClickListener listener) {
		this.emoticons = emoticons;
		this.activity = activity;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		int N=emoticons.size();
		int M=N/(NO_OF_EMOTICONS_PER_PAGE-1);
		return N%(NO_OF_EMOTICONS_PER_PAGE-1)==0?M:(M+1);
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		View layout = activity.getLayoutInflater().inflate(
				R.layout.item_pager_emoticons, null);

		int initialPosition = position * NO_OF_EMOTICONS_PER_PAGE-position;
		ArrayList<FaceHolder> emoticonsInAPage = new ArrayList<FaceHolder>();

		for (int i = initialPosition; i < initialPosition
				+ NO_OF_EMOTICONS_PER_PAGE-1
				&& i < emoticons.size(); i++) {
			emoticonsInAPage.add(emoticons.get(i));
		}
		FaceHolder del=new FaceHolder();
		del.index=-1;
		emoticonsInAPage.add(del);

		GridView grid = (GridView) layout.findViewById(R.id.emoticons_grid);
		EmoticonsGridAdapter adapter = new EmoticonsGridAdapter(
				activity.getApplicationContext(), emoticonsInAPage, position,
				listener);
		grid.setAdapter(adapter);
		((ViewPager) collection).addView(layout);
		return layout;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}