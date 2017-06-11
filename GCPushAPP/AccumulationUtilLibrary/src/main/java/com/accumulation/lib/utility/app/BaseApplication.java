package com.accumulation.lib.utility.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import com.accumulation.lib.utility.base.NetUtils;
import com.accumulation.lib.utility.debug.Logger;
import com.accumulation.lib.utility.mail.MailUtil;

/**
 * 基础应用
 * */
public abstract class BaseApplication extends Application {

	protected static boolean DEVELOP_MODE = false;

	private static boolean GLOBAL_EXCEPTION = false;

	private static Application mInstance;

	private Map<String, String> mInfos = new HashMap<String, String>();

	@SuppressLint("SimpleDateFormat")
	private DateFormat mFormatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	public static Application getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance=this;
		if (GLOBAL_EXCEPTION) {
			new CrashHandler().init();
		}
	}

	public void cancelDevelopMode() {
		DEVELOP_MODE = false;
	}

	public void startlDevelopMode() {
		DEVELOP_MODE = true;
	}
	
	public boolean isDevelopMode(){
		return DEVELOP_MODE;
	}
	
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				onException();
				Looper.loop();
			}
		}.start();
		collectLoginInfo(mInfos);
		collectDeviceInfo(mInfos);
		String crashInfo=caculateCrashInfo(ex);
		onSaveCrashInfo2File(crashInfo);
		onSaveCrashInfo2Email(crashInfo);
		return true;
	}
	
	protected void collectLoginInfo(Map<String, String> infos) {
		
	}
	
	protected void collectDeviceInfo(Map<String, String> infos) {
		try {
			PackageManager pm = this.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(this.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Logger.e("an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Logger.d(field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Logger.e("an error occured when collect crash info", e);
			}
		}
	}
	
	private String caculateCrashInfo(Throwable ex) {
		
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : mInfos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		return sb.toString();
	}

	protected abstract String getLogSavePath();
	
	/**
	 * 异常发生时的提示
	 * */
	protected void onException(){
		Toast.makeText(this, "程序出问题啦1.", Toast.LENGTH_LONG)
		.show();
	}
	protected void onSaveCrashInfo2Email(String exception){
		if(NetUtils.isConnected(this)){
			MailUtil.sendMail(MailUtil.MAIL_TYPE_LOG, exception, this);
		}
	}
	protected void onSaveCrashInfo2File(String exception){
		try {
			long timestamp = System.currentTimeMillis();
			String time = mFormatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = getLogSavePath();
				if (path != null) {
					File dir = new File(path);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(path
							+ fileName);
					fos.write(exception.getBytes());
					fos.close();
				}
			}
		} catch (Exception e) {
			Logger.e("an error occured while writing file...", e);
		}
	}
	
	class CrashHandler implements UncaughtExceptionHandler {
		
		private UncaughtExceptionHandler mDefaultHandler;

		private CrashHandler() {
		}

		public void init() {
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(this);
		}

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			if (!handleException(ex) && mDefaultHandler != null) {
				mDefaultHandler.uncaughtException(thread, ex);
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					Logger.e("error : ", e);
				}
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
		}
	}
}
