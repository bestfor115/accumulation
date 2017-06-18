package com.accumulation.app.ui.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;
import com.accumulation.app.base.DialogCallback;
import com.accumulation.app.dialog.ShareDialog;
import com.accumulation.app.manager.BroadcastManager;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.app.manager.SpannedManager;
import com.accumulation.app.util.ClipboardUtil;
import com.accumulation.app.util.PixelUtil;
import com.accumulation.app.view.RefreshLayout;
import com.accumulation.app.view.RefreshLayout.DataCaculateListener;
import com.accumulation.app.view.RefreshLayout.OnLoadListener;
import com.accumulation.lib.sociability.BaseCallback;
import com.accumulation.lib.sociability.SociabilityClient;
import com.accumulation.lib.sociability.data.Broadcast;
import com.accumulation.lib.sociability.data.Comment;
import com.accumulation.lib.sociability.data.JAComment;
import com.accumulation.lib.sociability.data.JLBroadcast;
import com.accumulation.lib.tool.net.data.JsonData;
import com.accumulation.lib.ui.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 界面：动态详情
 * */
public class BroadcastDetailActivity extends BaseActivity {

	public static final int REQUEST_CODE_ADD_COMMENT = 0;
	private final List<Comment> comments = new ArrayList<Comment>();
	private RefreshLayout refreshListView;
	private Button commentBtn;
	private TextView commentCountTxt;
	private TextView favoriteStateTxt;
	private TextView likeStateTxt;
	private ListView listView;
	private View reward;
	private LinearLayout no_comment;
	private Broadcast broadcast;
	private int keyboardHeight;
	private String broadcastId;
	private CommentsAdapter adapter;

	@Override
	protected void initDataFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.initDataFromIntent(intent);
		broadcastId = intent.getStringExtra("broadcast_id");
	}

	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		loadData();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		doCommentViewInit();
		doBroadcastOperateinInit();
		reward = findViewById(R.id.reward);
		reward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (broadcast != null) {
					Intent intent = new Intent(getContext(),
							RewardActivity.class);
					intent.putExtra("user", broadcast.senderId);
					startActivity(intent);
				}
			}
		});
		titleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listView.smoothScrollToPosition(0);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onMenu() {
		// TODO Auto-generated method stub
		super.onMenu();
		ShareDialog.createDialog(broadcast, getContext()).show();
	}

	@Override
	protected int getMenuRes() {
		// TODO Auto-generated method stub
		return R.drawable.btn_common_share;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "动态详情";
	}

	private void doBroadcastOperateinInit() {
		favoriteStateTxt = ViewHolder.get(rootLayout, R.id.favorite_state);
		likeStateTxt = ViewHolder.get(rootLayout, R.id.like_state);
		ViewHolder.get(rootLayout, R.id.comment_broadcast).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (broadcast.state == Broadcast.COMMITING) {
							return;
						}
						Intent intent = new Intent(getContext(),
								AddCommentActivity.class);
						intent.putExtra("broadcastId", broadcast.id);
						startActivityForResult(intent, REQUEST_CODE_ADD_COMMENT);
					}
				});
		ViewHolder.get(rootLayout, R.id.retweet_broadcast).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (broadcast.state == Broadcast.COMMITING) {
							return;
						}
						Intent intent = new Intent(getContext(),
								AddDisccussActivity.class);
						intent.putExtra("bid", broadcast.id);
						startActivity(intent);
					}
				});
		ViewHolder.get(rootLayout, R.id.like_broadcast).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (broadcast.state == Broadcast.COMMITING) {
							return;
						}
						likeBroadcast(broadcast);
					}
				});
		ViewHolder.get(rootLayout, R.id.favorite_broadcast).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (broadcast.state == Broadcast.COMMITING) {
							return;
						}
						favoriteBroadcast(broadcast);
					}
				});
		ViewHolder.get(rootLayout, R.id.copy_broadcast).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						ClipboardUtil.copyToClipboard(getContext(),
								broadcast.content);
					}
				});
	}

	private void loadData() {
		SociabilityClient.getClient().getBroadcastById(broadcastId,
				new DialogCallback<JLBroadcast>(this) {

					@Override
					public void onResultCallback(int code, String message,
							JLBroadcast result) {
						if (code < 0) {
							toast("未能找到该动态");
							finish();
						} else {
							broadcast = result.getDataAtIndex(0);
							refreshViewData();
						}
					}
				}, JLBroadcast.class);
	}

	private void refreshViewData() {
		doBroadcastViewInit();
		loadReplayData();
		refreshOperationShow();
	}

	private void refreshOperationShow() {
		if (broadcast != null) {
			if (broadcast.likeClicked) {
				likeStateTxt.setText("已赞");
			} else {
				likeStateTxt.setText("点赞");
			}
			if (broadcast.favoriteClicked) {
				favoriteStateTxt.setText("已收藏");
			} else {
				favoriteStateTxt.setText("收藏");
			}
		}
	}

	@SuppressLint("NewApi")
	private void doCommentViewInit() {
		listView = (ListView) findViewById(R.id.listview);
		listView.addHeaderView(LayoutInflater.from(this).inflate(
				R.layout.include_broadcast_comment_head, listView, false));
		no_comment = (LinearLayout) findViewById(R.id.no_comment);
		commentCountTxt = (TextView) findViewById(R.id.comment_count);
		commentBtn = (Button) findViewById(R.id.add_comment);
		commentBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(),
						AddCommentActivity.class);
				intent.putExtra("broadcastId", broadcast.id);
				startActivityForResult(intent, REQUEST_CODE_ADD_COMMENT);
			}
		});
		commentCountTxt.setText("共 " + comments.size() + " 条");
		listView.setAdapter(adapter = new CommentsAdapter(this, comments));

		// 获取RefreshLayout实例
		refreshListView = (RefreshLayout) findViewById(R.id.swipe_layout);
		// 设置下拉刷新时的颜色值,颜色值需要定义在xml中
		refreshListView.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		// 设置下拉刷新监听器
		refreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadReplayData();
			}
		});

		// 加载监听器
		refreshListView.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
				loadReplayData();
			}
		});
		refreshListView.setDataCaculateListener(new DataCaculateListener() {

			@Override
			public boolean hasMoreData() {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	private void caculateNoCommentVisible() {
		no_comment
				.setVisibility(comments.size() > 0 ? View.GONE : View.VISIBLE);
	}

	private void doBroadcastViewInit() {

		ImageView mPortrait = (ImageView) this
				.findViewById(R.id.broadcastItemImageAvatar);
		TextView mUserName = (TextView) this
				.findViewById(R.id.broadcastItemTextUserName);
		TextView mPublishTIme = (TextView) this
				.findViewById(R.id.broadcastItemTextPublishTime);
		TextView mRelatedContent = (TextView) this
				.findViewById(R.id.broadcastItemTextRelatedContent);
		TextView mContent = (TextView) this
				.findViewById(R.id.broadcastItemTextContent);
		View mRelatedContainer = this
				.findViewById(R.id.broadcastItemLayoutRelatedContentContainer);
		ImageLoader.getInstance().displayImage(broadcast.headpic, mPortrait);
		LinearLayout related_image_container = (LinearLayout) findViewById(R.id.related_image_container);
		LinearLayout layoutBroadcastItemImagesContainer = (LinearLayout) findViewById(R.id.broadcastItemLayoutImagesContainer);
		int max = AccumulationAPP.getInstance().width - PixelUtil.dp2px(40);
		BroadcastManager.showImageInContainer(broadcast.ImageList,
				layoutBroadcastItemImagesContainer, this, max);
		BroadcastManager.showZipInContainer(broadcast,
				(LinearLayout) findViewById(R.id.zip_container), this);

		SpannedManager.changeRawString(mContent, broadcast.content,
				broadcast.sentTo, false);
		mUserName.setText(SpannedManager.changeUserName(broadcast.senderTag));
		mPublishTIme.setText(broadcast.publishTime);
		if (broadcast.original != null) {
			BroadcastManager.showImageInContainer(broadcast.original.ImageList,
					related_image_container, this);
			String related_content = broadcast.original.content;
			related_content = "@" + broadcast.original.senderTag + ": "
					+ related_content;
			SpannedManager.changeRawString(mRelatedContent, related_content,
					broadcast.original.sentTo, true);
			SpannedManager
					.changeRawString(mRelatedContent,
							broadcast.original.content,
							broadcast.original.sentTo, true);
			mRelatedContainer.setVisibility(View.VISIBLE);
			mRelatedContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(getContext(),
							BroadcastDetailActivity.class);
					mIntent.putExtra("broadcast_id", broadcast.orignalId);
					startActivity(mIntent);
				}
			});
			mRelatedContainer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(getContext(),
							BroadcastDetailActivity.class);
					mIntent.putExtra("broadcast_id", broadcast.orignalId);
					startActivity(mIntent);
				}
			});
		} else {
			mRelatedContainer.setVisibility(View.GONE);
		}
		LinearLayout tag_container = (LinearLayout) findViewById(R.id.tag_container);
		BroadcastManager.showTagInContainer(broadcast.subjectList,
				tag_container, this);
	}

	private void loadReplayData() {
		if (broadcast == null) {
			return;
		}
		SociabilityClient.getClient().getBroadcastReply(broadcast.id,
				new BaseCallback<JAComment>() {
					@Override
					public void onResultCallback(int code, String message,
							JAComment result) {
						// TODO Auto-generated method stub
						if (code < 0) {
							toast(message);
						} else {
							comments.clear();
							comments.addAll(result.getCollectionData());
						}
						adapter.notifyDataSetChanged();
						refreshListView.setRefreshing(false);
						no_comment.setVisibility(comments.size() > 0 ? View.GONE
								: View.VISIBLE);
						commentCountTxt.setText("共 " + comments.size() + " 条");
					}
				}, JAComment.class);
	}

	@SuppressLint("NewApi")
	public class CommentsAdapter extends BaseAdapter {
		private List<Comment> datas;
		private Context cxt;

		public CommentsAdapter(Context cxt, List<Comment> datas) {
			this.cxt = cxt;
			this.datas = datas;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return datas.get(position);
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
						R.layout.item_broadcast_comment, null);
			}
			ImageView imageAvatar = (ImageView) convertView
					.findViewById(R.id.broadcast_comment_item_ImageAvatar);
			TextView textUserName = (TextView) convertView
					.findViewById(R.id.broadcast_comment_item_TextUserName);
			TextView textPublishTime = (TextView) convertView
					.findViewById(R.id.broadcast_comment_item_TextPublishTime);
			TextView textContent = (TextView) convertView
					.findViewById(R.id.broadcast_comment_item_TextContent);
			View line = convertView.findViewById(R.id.line);
			line.setVisibility(position == getCount() - 1 ? View.INVISIBLE
					: View.VISIBLE);
			final Comment entry = (Comment) getItem(position);
			LinearLayout image_container = ViewHolder.get(convertView,
					R.id.image_container);
			LinearLayout zip_container = ViewHolder.get(convertView,
					R.id.zip_container);
			BroadcastManager.showImageInContainer(entry.ImageList,
					image_container, cxt);
			BroadcastManager.showZipInContainer(entry.content, zip_container,
					cxt);
			imageAvatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				}
			});
			ImageLoader.getInstance().displayImage(entry.headPic, imageAvatar);
			textUserName.setText(SpannedManager.changeUserName(entry.sendBy));
			textPublishTime.setText(entry.sendTime);
			SpannedManager.changeRawString(textContent, entry.content, null,
					true);
			textContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showCommentMenu(entry);
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showCommentMenu(entry);
				}
			});
			convertView.setTag(entry);
			return convertView;
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
							refreshOperationShow();
						}
						toast(message);
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
							refreshOperationShow();
						}
						toast(message);
					}
				}, JsonData.class);
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_broadcast_detail;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == REQUEST_CODE_ADD_COMMENT) {
			if (arg1 == RESULT_OK) {
				loadReplayData();
			}
		}
	}

	private void showCommentMenu(final Comment entry) {
		View popup_view = LayoutInflater.from(this).inflate(
				R.layout.dialog_comment_menu, null, false);

		DisplayMetrics metric = getResources().getDisplayMetrics();
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

		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
			}
		});
		popup_view.findViewById(R.id.reply_comment).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popup.dismiss();
						Intent intent = new Intent(getContext(),
								AddCommentActivity.class);
						intent.putExtra("broadcastId", broadcast.id);
						intent.putExtra("commentId", entry.id);
						intent.putExtra("hint", "回复" + entry.sendBy + ":");
						startActivityForResult(intent, REQUEST_CODE_ADD_COMMENT);
					}
				});
		popup_view.findViewById(R.id.copy_comment).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popup.dismiss();
						ClipboardUtil.copyToClipboard(getContext(),
								entry.content);
					}
				});
		popup_view.findViewById(R.id.delete_comment).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popup.dismiss();
						SociabilityClient.getClient().deleteBroadcastReply(
								entry.id, new BaseCallback<JsonData>() {

									@Override
									public void onResultCallback(int code,
											String message, JsonData result) {
										if (code < 0) {
											toast(message);
										} else {
											comments.remove(entry);
											caculateNoCommentVisible();
											commentCountTxt.setText("共 "
													+ comments.size() + " 条");
											adapter.notifyDataSetChanged();
										}
									}
								}, JsonData.class);
					}
				});
		if (SaveManager.getInstance(getContext()).isSelf(entry.sendByUserId)) {
			popup_view.findViewById(R.id.delete_comment).setVisibility(
					View.VISIBLE);
			popup_view.findViewById(R.id.flag_comment).setVisibility(
					View.VISIBLE);
		} else {
			popup_view.findViewById(R.id.delete_comment).setVisibility(
					View.GONE);
			popup_view.findViewById(R.id.flag_comment).setVisibility(View.GONE);
		}
		popup.showAtLocation(rootLayout, Gravity.CENTER, 0, 0);
	}
}
