package com.accumulation.lib.configuration.widget;

import org.json.JSONObject;

import com.accumulation.lib.configuration.base.ActionUtils;
import com.accumulation.lib.configuration.base.IConfigViewGroup;
import com.accumulation.lib.configuration.base.PropertyUtils;
import com.accumulation.lib.configuration.core.Bind;
import com.accumulation.lib.configuration.core.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class AbsLayout extends AbsoluteLayout implements IConfigViewGroup {
	private View mData;
	
	public AbsLayout(Context context){
		super(context);
	}
	
	public AbsLayout(Context context, View data) {
		super(context);
		this.mData = data;
		PropertyUtils.setCommonProperties(this,data);
	}
	
	public AbsLayout(Context context, AttributeSet attrs){
		super(context);
	}
	
	public AbsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context);
	}

	@Override
	public android.view.ViewGroup.LayoutParams genConfLayoutParams(View data) {
		Bind bd = data.getBindByName(PROPERTY_LAYOUT_PARAMS);
		if (bd != null) {
			try {
				JSONObject jobj = bd.getValue().getJsonValue();
				return new AbsoluteLayout.LayoutParams(PropertyUtils.getScaledSize(jobj.optInt("width",
						LayoutParams.WRAP_CONTENT)), PropertyUtils.getScaledSize(jobj.optInt("height",
						LayoutParams.WRAP_CONTENT)), PropertyUtils.getScaledSize(jobj.optInt("x")),
						PropertyUtils.getScaledSize(jobj.optInt("y")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return generateDefaultLayoutParams();
	}

	@Override
	public View getViewData() {
		return mData;
	}

	@Override
	public void onAction(String type) {
		ActionUtils.handleAction(this, mData, type);
		
	}

	private boolean mShowFocusFrame = false;

	@Override
	public boolean showFocusFrame() {
		return mShowFocusFrame;
	}

	@Override
	public void setShowFocusFrame(boolean show) {
		mShowFocusFrame = show;
	}
}
