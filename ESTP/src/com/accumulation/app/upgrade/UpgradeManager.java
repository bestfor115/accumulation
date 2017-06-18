package com.accumulation.app.upgrade;

import java.io.InputStream;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.R;
import com.accumulation.app.dialog.DialogTips;
import com.accumulation.lib.tool.base.CommonUtils;
import com.accumulation.lib.tool.base.HttpUtils;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.tool.time.TimeFormateTool;

public class UpgradeManager {
	private static final String UPGRADE_CONFIG_PATH = "http://www.apingtai.cn/app/version.xml";
	public static final int UPGRADE_STATE_INVALID_NETWORK = -1;
	public static final int UPGRADE_STATE_INVALID_CONFIG = -2;
	public static final int UPGRADE_STATE_FAIL_DOWNLOAD = -3;
	public static final int UPGRADE_STATE_NO_NEED = -4;
	public static final int UPGRADE_STATE_DONE = 0;
	public static final int UPGRADE_STATE_CHECKING = 1;
	public static final int UPGRADE_STATE_CHOOSING = 2;
	public static final int UPGRADE_STATE_DOWNLOADING = 3;
	private boolean silent = true;
	private Context context=AccumulationAPP.getInstance();
	private long lastUpgradeTime = -1;
	private UpgradeConfig config;
	private int state = UPGRADE_STATE_DONE;
	private static UpgradeManager instance;
	private Handler handler = new Handler() ;

	public synchronized static UpgradeManager getInstance() {
		if (instance == null) {
			instance = new UpgradeManager();
		}
		return instance;
	}

	private UpgradeManager() {
	}


	public void checkUpgrade(final boolean silent,Context cxt) {
		if (state > 0) {
			return;
		}
		if (silent) {
			if (lastUpgradeTime < System.currentTimeMillis()) {
				lastUpgradeTime = System.currentTimeMillis()
						+ TimeFormateTool.DAY * 1000l;
			} else {
				return;
			}
		}
		this.context=cxt;
		this.silent = silent;
		final int versionCode = loadVersionCode(cxt);
		Thread thrad = new Thread(new Runnable() {
			@Override
			public void run() {
				if (!CommonUtils.isNetworkAvailable(context)) {
					recordUpgradeState(UPGRADE_STATE_INVALID_NETWORK);
				} else {
					recordUpgradeState(UPGRADE_STATE_CHECKING);
					InputStream inStream = HttpUtils
							.getInputStreamFromURL(UPGRADE_CONFIG_PATH);
					config = new ParseXmlService().parseUpgradeConfig(inStream);
					if (config == null) {
						recordUpgradeState(UPGRADE_STATE_INVALID_CONFIG);
					} else {
						int serviceCode = config.version;
						if (serviceCode > versionCode) {
							recordUpgradeState(UPGRADE_STATE_CHOOSING);
						} else {
							recordUpgradeState(UPGRADE_STATE_NO_NEED);
						}
					}
				}
			}
		});
		thrad.start();
	}

	/**
	 * 获取最新版本
	 * */
	private int loadVersionCode(Context cxt) {
		try {
			PackageManager pm = cxt.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(cxt.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				return pi.versionCode;
			}
		} catch (NameNotFoundException e) {
		}
		return -1;
	}

	private void showNoticeDialog() {
		DialogTips dialog = new DialogTips(context,
				context.getString(R.string.soft_update_info),
				context.getString(R.string.soft_update_title),
				context.getString(R.string.soft_update_later),
				context.getString(R.string.soft_update_updatebtn), false);
		// 设置成功事件
		dialog.setOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				recordUpgradeState(UPGRADE_STATE_DOWNLOADING);
				startDownloadThread();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				lastUpgradeTime = System.currentTimeMillis()
						+ TimeFormateTool.HOUR * 1000l;
				recordUpgradeState(UPGRADE_STATE_DONE);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	private void startDownloadThread() {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra("url", config.url);
		context.startService(intent);
		Toast.makeText(context, "后台更新下载中", Toast.LENGTH_LONG).show();
	}

	public void recordUpgradeState(int state) {
		Logger.d("record current upgrade state  :  " + state);
		this.state = state;
		switch (state) {
		case UPGRADE_STATE_CHOOSING:
			handler.post(new Runnable() {

				@Override
				public void run() {
					showNoticeDialog();
				}
			});
			break;
		case UPGRADE_STATE_INVALID_NETWORK:
			if (!silent) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(context, "网络未链接", Toast.LENGTH_LONG)
								.show();
					}
				});
			}
			break;
		case UPGRADE_STATE_INVALID_CONFIG:
			if (!silent) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(context, "信息解析出错", Toast.LENGTH_LONG)
								.show();
					}
				});
			}
			break;
		case UPGRADE_STATE_NO_NEED:
			if (!silent) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "已是最新版本", Toast.LENGTH_LONG)
								.show();
					}
				});
			}
			break;
		case UPGRADE_STATE_FAIL_DOWNLOAD:
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, "文件下载失败", Toast.LENGTH_LONG)
							.show();
				}
			});
			break;
		}
	}
}
