package com.accumulation.app.view;

import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.accumulation.app.manager.SpannedManager;

public class EmoticonsEditText extends EditText {
	public EmoticonsEditText(Context context) {
		super(context);
	}

	public EmoticonsEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EmoticonsEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		if (!TextUtils.isEmpty(text)) {
			super.setText(SpannedManager.formatRawString(this, text), type);
		} else {
			super.setText(text, type);
		}
	}

	public void setCursorToEnd() {
		CharSequence text = getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
	}

}
