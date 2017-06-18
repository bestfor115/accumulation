package com.accumulation.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

import com.accumulation.app.R;
import com.accumulation.app.util.PixelUtil;

/**
 * Description:很多搜索界面弹出来的提示语 User: xjp Date: 2015/4/15 Time: 9:09
 */

public class SearchTipsGroupView extends LinearLayout {
	
	public static abstract class TipGridAdapter extends BaseAdapter {

		public int getXSpace() {
			return PixelUtil.dp2px(15);
		}

		public int getYSpace() {
			return PixelUtil.dp2px(10);
		}
		
		public int getFoldMax(){
			return -1;
		}
	}

	private Context context;
	
	private TipGridAdapter adapter;

	public SearchTipsGroupView(Context context) {
		super(context);
		this.context = context;
		setOrientation(VERTICAL);// 设置方向
	}

	public SearchTipsGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOrientation(VERTICAL);// 设置方向
	}

	@SuppressLint("NewApi")
	public SearchTipsGroupView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		setOrientation(VERTICAL);// 设置方向
	}

	public void setAdapter(TipGridAdapter adapter){
		this.adapter=adapter;
		this.removeAllViews();
		if(adapter!=null&&adapter.getCount()>0){
			int length = 0;// 一行加载item 的宽度
			LinearLayout layout = null;

			LayoutParams layoutLp = null;

			boolean isNewLine = true;// 是否换行

			int screenWidth = getScreenWidth();// 屏幕的宽度

			int size = adapter.getCount();
			int topSpacing=adapter.getYSpace();
			int leftSpacing=adapter.getXSpace();
			for (int i = 0; i < size; i++) {// 便利items
				if (isNewLine) {// 是否开启新的一行
					layout = new LinearLayout(context);
					layout.setOrientation(HORIZONTAL);
					layoutLp = new LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutLp.topMargin = topSpacing;
				}

				View view = adapter.getView(i, null, this);
				// 设置item的参数
				LayoutParams itemLp = new LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				itemLp.leftMargin =leftSpacing;
				// 得到当前行的长度
				length += leftSpacing + getViewWidth(view);
				if (length > screenWidth) {// 当前行的长度大于屏幕宽度则换行
					length = 0;
					addView(layout, layoutLp);
					isNewLine = true;
					i--;
				} else {// 否则添加到当前行
					isNewLine = false;
					layout.addView(view, itemLp);
				}
			}
			addView(layout, layoutLp);
		}
	}

	/**
	 * 得到手机屏幕的宽度
	 * 
	 * @return
	 */
	private int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 得到view控件的宽度
	 * 
	 * @param view
	 * @return
	 */
	private int getViewWidth(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return view.getMeasuredWidth();
	}
}
