package com.nvapp.comic.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 显示项边框的GridView
 */
public class RectItemGridView extends GridView {
	public RectItemGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RectItemGridView(Context context) {
		super(context);
	}

	public RectItemGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
