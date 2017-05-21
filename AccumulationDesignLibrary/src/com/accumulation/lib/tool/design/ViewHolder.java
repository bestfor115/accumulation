package com.accumulation.lib.tool.design;

import android.util.SparseArray;
import android.view.View;

/**
 * Viewholder
 * 
 * @ClassName: ViewHolder
 * @Description: TODO
 * @author smile
 */
@SuppressWarnings("unchecked")
public class ViewHolder {
	
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view
				.getTag(R.id.view_holder);
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(R.id.view_holder, viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
