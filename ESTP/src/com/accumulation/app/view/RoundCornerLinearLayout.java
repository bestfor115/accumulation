package com.accumulation.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;

import com.accumulation.app.R;

public class RoundCornerLinearLayout extends LinearLayout {

	public RoundCornerLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				caculateChildBackground();
			}
		});
	}

	private void caculateChildBackground() {
		int N = getChildCount();
		for (int i = 0; i < N; i++) {
			View child = getChildAt(i);
			if(child.getVisibility()==View.VISIBLE){
				child.setBackgroundResource(caculateChildState(i,N,child));
			}
		}
	}
	
	private int caculateChildState(int index,int total,View child){
		boolean pre = false,post = false;
		for (int i = index+1; i < total; i++) {
			View view=getChildAt(i);
			if(view.getVisibility()==View.VISIBLE){
				post=true;
				break;
			}
		}
		
		for (int i = index-1; i >=0; i--) {
			View view=getChildAt(i);
			if(view.getVisibility()==View.VISIBLE){
				pre=true;
				break;
			}
		}
		
		if(pre){
			if(post){
				return R.drawable.background_view_rounded_middle;
			}else{
				return R.drawable.background_view_rounded_bottom;
			}
		}else{
			if(post){
				return R.drawable.background_view_rounded_top;
			}else{
				return R.drawable.background_view_rounded_single;
			}
		}
	}

}
