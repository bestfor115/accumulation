package com.accumulation.lib.configuration.base;

import com.accumulation.lib.configuration.core.View;

public interface IConfigView {
	public View getViewData();
	public void onAction(String type);
	public boolean showFocusFrame();
	public void setShowFocusFrame(boolean show);
}
