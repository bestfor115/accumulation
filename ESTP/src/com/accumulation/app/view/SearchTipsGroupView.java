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
 * Description:�ܶ��������浯��������ʾ�� User: xjp Date: 2015/4/15 Time: 9:09
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
		setOrientation(VERTICAL);// ���÷���
	}

	public SearchTipsGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOrientation(VERTICAL);// ���÷���
	}

	@SuppressLint("NewApi")
	public SearchTipsGroupView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		setOrientation(VERTICAL);// ���÷���
	}

	public void setAdapter(TipGridAdapter adapter){
		this.adapter=adapter;
		this.removeAllViews();
		if(adapter!=null&&adapter.getCount()>0){
			int length = 0;// һ�м���item �Ŀ��
			LinearLayout layout = null;

			LayoutParams layoutLp = null;

			boolean isNewLine = true;// �Ƿ���

			int screenWidth = getScreenWidth();// ��Ļ�Ŀ��

			int size = adapter.getCount();
			int topSpacing=adapter.getYSpace();
			int leftSpacing=adapter.getXSpace();
			for (int i = 0; i < size; i++) {// ����items
				if (isNewLine) {// �Ƿ����µ�һ��
					layout = new LinearLayout(context);
					layout.setOrientation(HORIZONTAL);
					layoutLp = new LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutLp.topMargin = topSpacing;
				}

				View view = adapter.getView(i, null, this);
				// ����item�Ĳ���
				LayoutParams itemLp = new LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				itemLp.leftMargin =leftSpacing;
				// �õ���ǰ�еĳ���
				length += leftSpacing + getViewWidth(view);
				if (length > screenWidth) {// ��ǰ�еĳ��ȴ�����Ļ�������
					length = 0;
					addView(layout, layoutLp);
					isNewLine = true;
					i--;
				} else {// ������ӵ���ǰ��
					isNewLine = false;
					layout.addView(view, itemLp);
				}
			}
			addView(layout, layoutLp);
		}
	}

	/**
	 * �õ��ֻ���Ļ�Ŀ��
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
	 * �õ�view�ؼ��Ŀ��
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
