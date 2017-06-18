package com.accumulation.app.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.ui.ViewHolder;

/**
 * 界面：意见反馈
 * */
public class SuggestActivity extends BaseActivity {

	EditText input;
	TextView wordCount;
	public final int MAX_WORD_COUNT = 200;

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		input = ViewHolder.get(rootLayout, R.id.suggest_input);
		wordCount = ViewHolder.get(rootLayout, R.id.word_count);
		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				int c = Math.min(MAX_WORD_COUNT, s.length());
				wordCount.setText(c + "/" + MAX_WORD_COUNT);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		ViewHolder.get(rootLayout, R.id.suggest).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (input.getText() == null
								|| "".equals(input.getText().toString().trim())) {
							toast("内容不能为空");
							return;
						}
						String input_suggest = input.getText().toString();
						toast("感谢你的建议");
					}
				});
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "意见反馈";
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_suggest;
	}

}
