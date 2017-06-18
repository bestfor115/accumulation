package com.accumulation.app.adapter;

import java.io.Serializable;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.dialog.DialogTips;
import com.accumulation.app.dialog.ShareDialog;
import com.accumulation.app.manager.BroadcastManager;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.app.manager.SpannedManager;
import com.accumulation.app.ui.broadcast.AddCommentActivity;
import com.accumulation.app.ui.broadcast.AddDisccussActivity;
import com.accumulation.app.ui.broadcast.BroadcastDetailActivity;
import com.accumulation.app.ui.user.UserCardActivity;
import com.accumulation.app.ui.user.UserHomeActivity;
import com.accumulation.app.util.ClipboardUtil;
import com.accumulation.app.util.UIUtils;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.Broadcast;
import com.accumulation.lib.tool.net.data.JsonData;
import com.accumulation.lib.ui.ViewHolder;

@SuppressLint("NewApi")
public class BroadcastAdapter extends BaseAdapter {
	private List<Broadcast> datas;
	private Context cxt;

	public BroadcastAdapter(Context cxt, List<Broadcast> datas) {
		this.cxt = cxt;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return AccumulationAPP.getInstance().commitingList.size()
				+ datas.size();
	}

	@Override
	public Broadcast getItem(int position) {
		// TODO Auto-generated method stub
		int commitingCount = AccumulationAPP.getInstance().commitingList.size();
		if (position < commitingCount) {
			return AccumulationAPP.getInstance().commitingList.get(position);
		}
		return datas.get(position - commitingCount);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(cxt).inflate(
					R.layout.item_broadcast, null);
		}
		final Broadcast entry = (Broadcast) getItem(position);
		final ImageView avatar = ViewHolder.get(convertView, R.id.avatar);
		TextView user_name = ViewHolder.get(convertView, R.id.user_name);
		TextView broadcast_content = ViewHolder.get(convertView,
				R.id.broadcast_content);
		LinearLayout image_container = ViewHolder.get(convertView,
				R.id.image_container);
		LinearLayout zip_container = ViewHolder.get(convertView,
				R.id.zip_container);
		LinearLayout related_container = ViewHolder.get(convertView,
				R.id.related_container);
		LinearLayout related_image_container = ViewHolder.get(convertView,
				R.id.related_image_container);
		View tag_line = ViewHolder.get(convertView, R.id.tag_line);

		TextView related_broadcast = ViewHolder.get(convertView,
				R.id.related_broadcast);
		LinearLayout comment_broadcast = ViewHolder.get(convertView,
				R.id.comment_broadcast);
		LinearLayout like_broadcast = ViewHolder.get(convertView,
				R.id.like_broadcast);
		LinearLayout retweet_broadcast = ViewHolder.get(convertView,
				R.id.retweet_broadcast);
		LinearLayout tag_part = ViewHolder.get(convertView, R.id.tag_part);
		LinearLayout tag_container = ViewHolder.get(convertView,
				R.id.tag_container);
		LinearLayout delete_container = ViewHolder.get(convertView,
				R.id.delete_container);

		TextView comment_count = ViewHolder
				.get(convertView, R.id.comment_count);
		TextView like_count = ViewHolder.get(convertView, R.id.like_count);
		final TextView like_animation = ViewHolder.get(convertView,
				R.id.like_animation);
		ImageView like_image = ViewHolder.get(convertView, R.id.like_image);

		TextView retweet_count = ViewHolder
				.get(convertView, R.id.retweet_count);
		TextView publish_time = ViewHolder.get(convertView, R.id.publish_time);
		final ImageView broadcast_menu = ViewHolder.get(convertView,
				R.id.broadcast_menu);
		TextView delete = ViewHolder.get(convertView, R.id.delete);
		View commiting = ViewHolder.get(convertView, R.id.commiting);
		commiting
				.setVisibility(entry.state == Broadcast.COMMITING ? View.VISIBLE
						: View.GONE);
		broadcast_menu
				.setVisibility(entry.state != Broadcast.COMMITING ? View.VISIBLE
						: View.GONE);

		like_count.setText(entry.liked == 0 ? "鼓励" : (entry.liked + ""));
		comment_count.setText(entry.replayCount == 0 ? "评论"
				: (entry.replayCount + ""));
		tag_part.setVisibility(entry.subjectList != null
				&& entry.subjectList.length > 0 ? View.VISIBLE : View.GONE);
		if (entry.likeClicked) {
			like_image.setImageResource(R.drawable.icon_liked);
			like_count.setTextColor(Color.RED);
		} else {
			like_count.setTextColor(cxt.getResources().getColor(
					R.color.description_txt_color));
			like_image.setImageResource(R.drawable.icon_like);
		}
		delete_container.setVisibility(SaveManager.getInstance(cxt).isSelf(
				entry.senderId) ? View.VISIBLE : View.GONE);
		AccumulationAPP.getInstance().loadImage(entry.headpic, avatar);

		SpannedManager.changeRawString(broadcast_content, entry.content,
				entry.sentTo, true);
		user_name.setText(SpannedManager.changeUserName(entry.senderTag));
		publish_time.setText(entry.publishTime);
		if (entry.original != null) {
			BroadcastManager.showImageInContainer(entry.original.ImageList,
					related_image_container, cxt);
			String related_content = entry.original.content;
			related_content = "@" + entry.original.senderTag + ": "
					+ related_content;
			SpannedManager.changeRawString(related_broadcast, related_content,
					entry.original.sentTo, true);
			related_container.setVisibility(View.VISIBLE);
			related_container.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(cxt,
							BroadcastDetailActivity.class);
					mIntent.putExtra("broadcast_id", entry.orignalId);
					cxt.startActivity(mIntent);
				}
			});
			related_broadcast.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(cxt,
							BroadcastDetailActivity.class);
					mIntent.putExtra("broadcast_id", entry.orignalId);
					cxt.startActivity(mIntent);
				}
			});
		} else {
			related_container.setVisibility(View.GONE);
		}
		BroadcastManager.showImageInContainer(entry, image_container, cxt);
		BroadcastManager.showZipInContainer(entry, zip_container, cxt);
		BroadcastManager.showTagInContainer(entry.subjectList, tag_container,
				cxt);
		tag_line.setVisibility(entry.subjectList != null
				&& entry.subjectList.length > 0 ? View.VISIBLE : View.GONE);
		like_broadcast.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (entry.state == Broadcast.COMMITING) {
					return;
				}
				if (!entry.likeClicked) {
					like_animation.setAlpha(1.0f);
					like_animation.setTranslationY(10);
					like_animation.setScaleX(0.8f);
					like_animation.setScaleY(0.8f);
					like_animation.animate().setDuration(600).alpha(0.0f)
							.translationYBy(-40).scaleX(1.1f).scaleY(1.1f);
				}
				likeBroadcast(entry);
			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogTips dialog = new DialogTips(cxt, "提示", "确认要删除这条动态",
						"确定", true, true);
				dialog.setOnSuccessListener(new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface,
							int userId) {
						deleteBroadcast(entry);
					}
				});
				dialog.show();
				dialog = null;
			}
		});
		retweet_broadcast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (entry.state == Broadcast.COMMITING) {
					return;
				}
				Intent mIntent = new Intent(cxt, AddDisccussActivity.class);
				mIntent.putExtra("bid", entry.id);
				cxt.startActivity(mIntent);
			}
		});
		comment_broadcast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (entry.state == Broadcast.COMMITING) {
					return;
				}
				if (entry.replayCount > 0) {
					Intent intent = new Intent(cxt,
							BroadcastDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("data", (Serializable) entry);
					bundle.putBoolean("comment", true);
					bundle.putString("broadcast_id", entry.id);
					intent.putExtras(bundle);
					cxt.startActivity(intent);
				} else {
					Intent intent = new Intent(cxt, AddCommentActivity.class);
					intent.putExtra("broadcastId", entry.id);
					cxt.startActivity(intent);
				}
			}
		});
		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (SaveManager.getInstance(cxt).isSelf(entry.senderId)) {
					Intent intent = new Intent(cxt, UserCenterActivity.class);
					intent.putExtra("id", entry.senderId);
					cxt.startActivity(intent);
				} else {
					Intent intent = new Intent(cxt, UserHomeActivity.class);
					intent.putExtra("id", entry.senderId);
					cxt.startActivity(intent);
				}
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(cxt, BroadcastDetailActivity.class);
				mIntent.putExtra("broadcast_id", entry.id);
				cxt.startActivity(mIntent);
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (entry.state == Broadcast.COMMITING) {
					return true;
				}
				showBroadcastMenu(entry, broadcast_menu);
				return true;
			}
		});
		broadcast_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(cxt, BroadcastDetailActivity.class);
				mIntent.putExtra("broadcast_id", entry.id);
				cxt.startActivity(mIntent);
			}
		});
		broadcast_content.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (entry.state == Broadcast.COMMITING) {
					return true;
				}
				showBroadcastMenu(entry, broadcast_menu);
				return true;
			}
		});
		broadcast_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showBroadcastMenu(entry, broadcast_menu);
			}
		});
		convertView.setTag(entry);
		return convertView;
	}

	/**
	 * 删除动态
	 * */
	private void deleteBroadcast(final Broadcast entry) {
		if (entry.state == Broadcast.COMMITING) {
			AccumulationAPP.getInstance().commitingList.remove(entry);
			notifyDataSetChanged();
		} else {
			SociabilityClient.getClient().deleteBroadcast(entry.id,
					new BaseCallback<JsonData>() {

						@Override
						public void onResultCallback(int code, String message,
								JsonData result) {
							// TODO Auto-generated method stub
							UIUtils.showTast(cxt, message);
							if (code >= 0) {
								datas.remove(entry);
								notifyDataSetChanged();
							}
						}
					}, JsonData.class);
		}
	}

	private void likeBroadcast(final Broadcast entry) {
		SociabilityClient.getClient().likeBroadcast(entry.id,
				new BaseCallback<JsonData>() {

					@Override
					public void onResultCallback(int code, String message,
							JsonData result) {
						if (code >= 0) {
							entry.liked++;
							entry.likeClicked = true;
							notifyDataSetChanged();
						}
						UIUtils.showTast(cxt, message);
					}
				}, JsonData.class);
	}

	/**
	 * 收藏动态
	 * */
	private void favoriteBroadcast(final Broadcast entry) {
		SociabilityClient.getClient().favoriteBroadcast(entry.id,
				!entry.favoriteClicked, new BaseCallback<JsonData>() {

					@Override
					public void onResultCallback(int code, String message,
							JsonData result) {
						if (code >= 0) {
							entry.favoriteClicked = !entry.favoriteClicked;
						}
						UIUtils.showTast(cxt, message);
					}
				}, JsonData.class);
	}

	private void showBroadcastMenu(final Broadcast entry, final ImageView menu) {
		menu.setImageResource(R.drawable.group_up);
		View popup_view = LayoutInflater.from(cxt).inflate(
				R.layout.dialog_broadcast_menu, null, false);
		Button copy_broadcast = ViewHolder.get(popup_view, R.id.copy_broadcast);
		Button share_broadcast = ViewHolder.get(popup_view,
				R.id.share_broadcast);
		Button favorite_broadcast = ViewHolder.get(popup_view,
				R.id.favorite_broadcast);
		Button cancel = ViewHolder.get(popup_view, R.id.cancel);
		if (entry.favoriteClicked) {
			favorite_broadcast.setText("取消收藏");
		} else {
			favorite_broadcast.setText("收 藏");
		}
		DisplayMetrics metric = cxt.getResources().getDisplayMetrics();
		int mScreenWidth = metric.widthPixels;
		int mScreenHeight = metric.heightPixels;
		final PopupWindow popup = new PopupWindow(popup_view, mScreenWidth,
				mScreenHeight);
		popup.setAnimationStyle(R.style.Animations_Pop);
		popup_view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (popup != null && popup.isShowing()) {
					popup.dismiss();
				}
				return true;
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popup != null && popup.isShowing()) {
					popup.dismiss();
				}
			}
		});
		copy_broadcast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClipboardUtil.copyToClipboard(cxt, entry.content);
				if (popup != null && popup.isShowing()) {
					popup.dismiss();
				}
			}
		});
		favorite_broadcast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				favoriteBroadcast(entry);
				if (popup != null && popup.isShowing()) {
					popup.dismiss();
				}
			}
		});
		share_broadcast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ShareDialog.createDialog(entry, cxt).show();
				if (popup != null && popup.isShowing()) {
					popup.dismiss();
				}
			}
		});
		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				menu.setImageResource(R.drawable.group_down);
			}
		});
		popup.showAtLocation(menu, Gravity.CENTER, 0, 0);
	}

}
