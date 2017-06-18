package com.accumulation.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.dialog.DialogTips;
import com.accumulation.app.manager.BroadcastManager;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.app.manager.SpannedManager;
import com.accumulation.app.ui.user.UserCardActivity;
import com.accumulation.app.util.ClipboardUtil;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.lib.sociability.data.MessageItem;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.tool.net.ServiceHelper.ResponseHandlerT;
import com.accumulation.lib.tool.net.http.RequestParams;
import com.accumulation.lib.tool.time.TimeFormateTool;
import com.accumulation.lib.ui.ViewHolder;

/**
 * 聊天适配器
 * 
 * @ClassName: MessageChatAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-5-28 下午5:34:07
 */
public class MessageChatAdapter extends BaseListAdapter<MessageItem> {
	private PopupWindow mCommentMenu;

	public MessageChatAdapter(Context context, List<MessageItem> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final MessageItem entry = getItem(position);
		if (convertView == null) {
			convertView = createViewByType(entry, position);
		}
		boolean self = entry.sentByMySelf;
		if (!convertView.getTag().equals(self)) {
			convertView = createViewByType(entry, position);
		}
		Logger.d(entry.content + "");
		ProgressBar commiting = ViewHolder.get(convertView, R.id.commiting);
		
		commiting.setVisibility(entry.state==MessageItem.COMMITING?View.VISIBLE:View.GONE);
		final ImageView avatar = ViewHolder.get(convertView, R.id.avatar);
		TextView broadcast_content = ViewHolder.get(convertView,
				R.id.broadcast_content);
		LinearLayout image_container = ViewHolder.get(convertView,
				R.id.image_container);
		LinearLayout zip_container = ViewHolder.get(convertView,
				R.id.zip_container);
		TextView publish_time = ViewHolder.get(convertView, R.id.publish_time);
		final View container = convertView;
		OnLongClickListener l = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (mCommentMenu != null && mCommentMenu.isShowing()) {
					mCommentMenu.dismiss();
					return true;
				} else {
					View pop = initCommentPopupWindowView(entry);
					pop.measure(MeasureSpec.UNSPECIFIED,
							MeasureSpec.UNSPECIFIED);
					int popupWidth = pop.getMeasuredWidth();
					int popupHeight = pop.getMeasuredHeight();
					int[] location = new int[2];
					container.getLocationOnScreen(location);
					int X = (location[0] + AccumulationAPP.getInstance().width / 2)
							- popupWidth / 2;
					int Y = (location[1] + v.getHeight() / 2) - popupHeight / 2
							+ PixelUtil.dp2px(30);
					Logger.d("X" + X);
					Logger.d("Y" + Y);
					mCommentMenu.showAtLocation(v, Gravity.NO_GRAVITY, X, Y);
					return true;
				}
			}
		};
		ViewHolder.get(convertView, R.id.message_container)
				.setOnLongClickListener(l);
		broadcast_content.setOnLongClickListener(l);

		AccumulationAPP.getInstance().loadImage(entry.headpic, avatar);
		if (TextUtils.isEmpty(entry.content)) {
			broadcast_content.setVisibility(View.GONE);
		} else {
			broadcast_content.setVisibility(View.VISIBLE);
			SpannedManager.changeRawString(broadcast_content, entry.content,
					null, false);
		}
		if (getCount() < 2 || position == 0) {
			publish_time.setText(TimeFormateTool
					.formatChatTIme(entry.publishTimeAccurate));
		} else {
			if (TimeFormateTool.isSameMinute(
					getItem(position).publishTimeAccurate,
					getItem(position - 1).publishTimeAccurate)) {
				publish_time.setText("");
			} else {
				publish_time.setText(TimeFormateTool
						.formatChatTIme(entry.publishTimeAccurate));
			}
		}
		publish_time.setVisibility(CommonUtils.isEmpty(publish_time.getText())?View.GONE:View.VISIBLE);

		BroadcastManager.showImageInContainer(entry.ImageList, image_container,
				context);		
		BroadcastManager.showZipInContainer(entry.content, zip_container,
				context);

		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, UserCardActivity.class);
				intent.putExtra("userid", entry.senderId);
				context.startActivity(intent);
			}
		});
		// convertView.setTag(entry);
		return convertView;
	}

	private View createViewByType(MessageItem message, int position) {
		boolean self = SaveManager.getInstance(context).isSelf(message.senderId);
		View view = null;
		if (self) {
			view = inflater.inflate(R.layout.item_right_chat_message, null);
		} else {
			view = inflater.inflate(R.layout.item_left_chat_message, null);
		}
		view.setTag(self);
		return view;
	}

	public View initCommentPopupWindowView(final MessageItem entry) {

		// // 获取自定义布局文件pop.xml的视图
		View customView = inflater.inflate(R.layout.pop_comment_menu, null,
				false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		mCommentMenu = new PopupWindow(customView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		mCommentMenu.setAnimationStyle(R.style.Animations_Pop);
		mCommentMenu.setBackgroundDrawable(new BitmapDrawable());
		mCommentMenu.setOutsideTouchable(true);
		/** 在这里可以实现自定义视图的功能 */
		Button delete = (Button) customView.findViewById(R.id.delete);
		Button replay = (Button) customView.findViewById(R.id.replay);
		Button copy = (Button) customView.findViewById(R.id.copy);
		replay.setVisibility(View.GONE);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogTips dialog = new DialogTips(context, "信息",
						"本条私信将被删除且不可还原，确定要删除吗？", "确定", true, true);
				// 设置成功事件
				dialog.setOnSuccessListener(new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface,
							int userId) {
						list.remove(entry);
						notifyDataSetChanged();
						RequestParams param = new RequestParams();
						param.put("id", entry.id);
						// APingTaiAPP.getInstance().requestNetData(
						// UriConfig.MESSAGE_REMOVE_URL, param,
						// JsonBean.class,
						// new ResponseHandlerT<JsonBean>() {
						// @Override
						// public void onResponse(boolean success,
						// JsonBean result) {
						// if (success && result != null
						// && result.success) {
						//
						// }
						// }
						// });
					}
				});
				// 显示确认对话框
				dialog.show();
				dialog = null;

				if (mCommentMenu != null && mCommentMenu.isShowing()) {
					mCommentMenu.dismiss();
				}
			}
		});

		copy.setOnClickListener(new OnClickListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClipboardUtil.copyToClipboard(context, entry.content);
				if (mCommentMenu != null && mCommentMenu.isShowing()) {
					mCommentMenu.dismiss();
				}
			}
		});
		return customView;
	}
}
