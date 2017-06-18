package com.accumulation.app.ui.broadcast;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.inputmethod.InputMedhodTools;

public class ChooseRewardActivity extends BaseActivity implements OnClickListener{

	private View lastSelect;
	private View other_tip;
	private EditText other_input;
	
	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_choose_reward;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "Ñ¡Ôñ½ð¶î";
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		InputMedhodTools.hideKeyboard(other_input);
	}
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		other_tip=findViewById(R.id.other_tip);
		other_input=(EditText) findViewById(R.id.other_input);
		findViewById(R.id.reward_5).setOnClickListener(this);
		findViewById(R.id.reward_10).setOnClickListener(this);
		findViewById(R.id.reward_20).setOnClickListener(this);
		findViewById(R.id.reward_30).setOnClickListener(this);
		findViewById(R.id.reward_50).setOnClickListener(this);
		findViewById(R.id.reward_100).setOnClickListener(this);
		findViewById(R.id.reward_200).setOnClickListener(this);
		findViewById(R.id.reward_other).setOnClickListener(this);

		findViewById(R.id.save).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int reward=0;
				if(lastSelect!=null){
					if(lastSelect.getId()==R.id.reward_other){
						reward=CommonUtils.isEmpty(other_input.getText())?0:Integer.parseInt(other_input.getText().toString());
					}else{
						reward=Integer.parseInt(lastSelect.getTag()+"");
					}
				}
				if(reward>0){
					Intent intent = new Intent();
					intent.putExtra("reward", reward);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		int old_reward=getIntent().getIntExtra("reward", 0);
		if(old_reward>0){
			View select=rootLayout.findViewWithTag(old_reward+"");
			if(select!=null){
				select.performClick();
			}else{
				findViewById(R.id.reward_other).performClick();
				other_input.setText(old_reward+"");
			}
		}else{
			findViewById(R.id.reward_5).performClick();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(lastSelect!=null){
			lastSelect.setSelected(false);
		}
		v.setSelected(true);
		if(v.getId()==R.id.reward_other){
			other_tip.setVisibility(View.GONE);
			other_input.setVisibility(View.VISIBLE);
			other_input.requestFocus();
			InputMedhodTools.showKeyboard(other_input);
		}else{
			other_tip.setVisibility(View.VISIBLE);
			other_input.setVisibility(View.GONE);
			InputMedhodTools.hideKeyboard(other_input);

		}
		lastSelect=v;
	}
	

}
