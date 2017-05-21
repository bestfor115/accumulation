package com.accumulation.lib.tool.design.grid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class WeightGridLayout extends ViewGroup {
	private static final String TAG = "WeightGridAdapter";

	private class ItemInfo {
		int position;
		View view;

		public ItemInfo(int position, View v) {
			this.position = position;
			this.view = v;
		}
	}

	public static abstract class WeightGridAdapter extends BaseAdapter {

		public abstract int getChildXSize(int position);

		public abstract int getChildYSize(int position);

		public abstract int getXSize();

		public abstract int getYSize();

		public int getXSpace() {
			return 10;
		}

		public int getYSpace() {
			return 10;
		}
	}

	private WeightGridAdapter mAdapter;

	private List<ItemInfo> mItemInfos = new ArrayList<WeightGridLayout.ItemInfo>();

	public WeightGridLayout(Context context) {
		this(context, null);
	}

	public WeightGridLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeightGridLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setChildrenDrawingOrderEnabled(true);
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			populateItems();
			invalidate();
			requestLayout();
		}

	};
	
	public WeightGridAdapter getAdapter(){
		return mAdapter;
	}

	public void setAdapter(WeightGridAdapter adapter) {
		if (mAdapter != null)
			mAdapter.unregisterDataSetObserver(mDataObserver);
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		if (adapter != null) {
			populateItems();
		} else {
			removeAllViews();
		}
		mItemInfos.clear();
		requestLayout();
	}

	private void populateItems() {
		this.removeAllViewsInLayout();
		List<ItemInfo> prevInfos = mItemInfos;
		mItemInfos = new ArrayList<WeightGridLayout.ItemInfo>();
		int count = mAdapter.getCount();
		for (int i = 0; i < count; i++) {
			View convertView = null;
			if (prevInfos != null && prevInfos.size() > i)
				convertView = prevInfos.get(i).view;
			View v = mAdapter.getView(i, convertView, this);
			if (v != null) {
				LayoutParams lp = v.getLayoutParams();
				if (lp == null)
					lp = generateDefaultLayoutParams();
				addViewInLayout(v, -1, lp);
				mItemInfos.add(new ItemInfo(i, v));
			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "onMeasure");
		if (mAdapter == null || mAdapter.getXSize() <= 0 || mAdapter.getYSize() <= 0) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int dw = Math.max(getLayoutParams().width, 0);
		int dh = Math.max(getLayoutParams().height, 0);
		setMeasuredDimension(getDefaultSize(dw, widthMeasureSpec),
				getDefaultSize(dh, heightMeasureSpec));

		final int measuredWidth = getMeasuredWidth();
		final int measuredHeight = getMeasuredHeight();

		int xSize = mAdapter.getXSize();
		int xSpace = mAdapter.getXSpace();
		int ySize = mAdapter.getYSize();
		int ySpace = mAdapter.getYSpace();

		Log.d(TAG, String.format("Width %d, Height %d, xSzie %d, ySize %d", measuredWidth,
				measuredHeight, xSize, ySize));

		int cellWidth = (measuredWidth - getPaddingLeft() - getPaddingRight() - (xSize - 1)
				* xSpace)
				/ xSize;
		int cellHeight = (measuredHeight - getPaddingTop() - getPaddingBottom() - (ySize - 1)
				* ySpace)
				/ ySize;
		Log.d(TAG, String.format("cellWidth %d, cellHeight %d", cellWidth, cellHeight));

		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildByOriginPosition(i);
			int childX = mAdapter.getChildXSize(i);
			int childY = mAdapter.getChildYSize(i);

			child.measure(MeasureSpec.makeMeasureSpec(getSizeWithSpace(childX, cellWidth, xSpace),
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					getSizeWithSpace(childY, cellHeight, ySpace), MeasureSpec.EXACTLY));
		}
	}

	private int getSizeWithSpace(int sizeCount, int sizeUnit, int space) {
		int sz = sizeCount * sizeUnit;
		if (sizeCount > 1)
			sz += (sizeCount - 1) * space;
		return sz;
	}

	@Override
	public void bringChildToFront(View child) {
		// TODO Auto-generated method stub
		super.bringChildToFront(child);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "onLayout");
		if (mAdapter == null || mAdapter.getXSize() <= 0 || mAdapter.getYSize() <= 0) {
			return;
		}

		int width = r - l;
		int height = b - t;
		int xSize = mAdapter.getXSize();
		int xSpace = mAdapter.getXSpace();
		int ySize = mAdapter.getYSize();
		int ySpace = mAdapter.getYSpace();

		int count = getChildCount();

		int pLeft = getPaddingLeft();
		int pTop = getPaddingTop();

		int cellWidth = (width - pLeft - getPaddingRight() - (xSize - 1) * xSpace) / xSize;
		int cellHeight = (height - pTop - getPaddingBottom() - (ySize - 1) * ySpace) / ySize;

		// x * y matrix for greedy layout
		int[][] grid = new int[ySize][xSize];
		for (int i = 0; i < count; i++) {
			View child = getChildByOriginPosition(i);
			int childX = mAdapter.getChildXSize(i);
			int childY = mAdapter.getChildYSize(i);
			int childW = child.getMeasuredWidth();
			int childH = child.getMeasuredHeight();

			int[] xy = findSpot(grid, childX, childY);
			if (xy != null) {
				int left = pLeft + xy[0] * (cellWidth + xSpace);
				int right = left + childW;
				int top = pTop + xy[1] * (cellHeight + ySpace);
				int bottom = top + childH;
				child.layout(left, top, right, bottom);
				Log.d(TAG, String.format("child %d at %d, %d; l %d, r %d, t %d, b %d", i, xy[0],
						xy[1], left, right, top, bottom));
			} else {
				Log.d(TAG, "can't find empty spot for child " + i);
			}
		}
	}

	public View getChildByOriginPosition(int position) {
		for (ItemInfo info : mItemInfos) {
			if (info.position == position)
				return info.view;
		}
		View v = getChildAt(position);
		return v;
	}

	private int[] findSpot(int[][] grid, int xSize, int ySize) {
		for (int r = 0; r <= grid.length - ySize; r++) {
			for (int c = 0; c <= grid[r].length - xSize; c++) {
				if (spaceAvailable(grid, r, c, xSize, ySize)) {
					markSpace(grid, r, c, xSize, ySize);
					return new int[] { c, r };
				}
			}
		}

		return null;
	}

	private void markSpace(int[][] grid, int r, int c, int xSize, int ySize) {
		for (int i = r; i < r + ySize; i++) {
			for (int j = c; j < c + xSize; j++) {
				grid[i][j] = 1;
			}
		}
	}

	private boolean spaceAvailable(int[][] grid, int r, int c, int xSize, int ySize) {
		if (r + ySize > grid.length || c + xSize > grid[0].length) {
			return false;
		}
		int total = 0;
		for (int i = r; i < r + ySize; i++) {
			for (int j = c; j < c + xSize; j++) {
				total += grid[i][j];
			}
		}

		return total == 0;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				handled = arrowScroll(FOCUS_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				handled = arrowScroll(FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				handled = arrowScroll(FOCUS_UP);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				handled = arrowScroll(FOCUS_DOWN);
				break;
			}
		}
		if (handled)
			return true;
		return super.dispatchKeyEvent(event);
	}

	public boolean arrowScroll(int direction) {
		View currentFocused = findFocus();
		if (currentFocused == this)
			currentFocused = null;

		boolean handled = false;

		View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
		if (nextFocused != null && nextFocused != currentFocused) {
			handled = nextFocused.requestFocus();
		}
		if (handled) {
			playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
		}
		return handled;
	}

}
