package com.accumulation.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.dialog.DialogTips;
import com.accumulation.app.manager.SpannedManager;
import com.accumulation.app.util.ClipboardUtil;
import com.accumulation.app.util.UIUtils;
import com.accumulation.lib.sociability.data.Message;
import com.accumulation.lib.sociability.data.MessageItem;
import com.accumulation.lib.tool.net.ServiceHelper.ResponseHandlerT;
import com.accumulation.lib.tool.net.http.RequestParams;
import com.accumulation.lib.tool.time.TimeFormateTool;
import com.accumulation.lib.ui.ViewHolder;

public class MessageAdapter extends BaseAdapter {

	private List<Message> groupList;
	private Context context;

	public MessageAdapter(Context context, List<Message> groupList) {
		this.context = context;
		this.groupList = groupList;
	}

	@Override
	public int getCount() {
		return groupList.size();
	}

	@Override
	public Object getItem(int position) {
		return groupList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_message_list, null);
		}
		final Message entry = (Message) getItem(position);

		ImageView imageAvatar = ViewHolder.get(convertView, R.id.avatar);
		TextView textName = ViewHolder.get(convertView, R.id.message_user);
		TextView textCreatedTime = ViewHolder.get(convertView,
				R.id.message_time);
		TextView textIntro = ViewHolder.get(convertView, R.id.message_info);
		AccumulationAPP.getInstance()
				.loadImage(entry.targetAvatar, imageAvatar);
		textName.setText(entry.target);
		textCreatedTime.setText(TimeFormateTool
				.formatShowTIme(entry.publishTimeAccurate));
		if (TextUtils.isEmpty(entry.content)) {
			SpannedManager.changeRawString2(textIntro, "[ͼƬ]", null, true);
		} else {
			SpannedManager.changeRawString2(textIntro, entry.content, null,
					true);
		}
		convertView.setTag(entry);
//		if (entry.hasRead) {
//			convertView.setBackgroundResource(AccumulationAPP.getInstance()
//					.getThemeResource(R.attr.auxiliaryHueThree));
//		} else {
//			convertView.setBackgroundColor(context.getResources().getColor(
//					R.color.unread_message_bg));
//		}
		return convertView;
	}

}