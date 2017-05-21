package com.accumulation.lib.ui.grid.drag;

public interface ScrollingStrategy {

	boolean performScrolling(final int x, final int y, final DragGridView view);

}
