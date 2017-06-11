package com.zyl.push.sdk.script;

import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ScriptImpl implements Scriptable {
	private static final long FLING_TOUCH_DURATION = 1500;
	private static final long SCROLL_TOUCH_DURATION = 5000;
	public static final String TAG = "ScriptImpl";

	@Override
	public int api_callPerson(String arg0) {
		return 0;
	}

	@Override
	public void api_inputString(String arg0) {
		int length = arg0 == null ? 0 : arg0.length();
		if (length == 0) {
			return;
		}
		for (int i = 0; i < length; i++) {
			api_sleep(1000);
			String command = "input text " + arg0.substring(i, i + 1);
			ShellUtils.execCommand(command, true);
		}
	}

	@Override
	public void api_openApp(String arg0) {
		ComponentName cmp = parseAPPURL(arg0);
		if (cmp == null) {
			Log.d(TAG, String.format("can't find a compomment for url :%s", arg0));
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(cmp);
		try {
			ScriptManager.getManager().getContext().startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, String.format("can't find a activity for :%s", cmp.toString()));
		}
	}

	private ComponentName parseAPPURL(String url) {
		ComponentName cmp = null;
		if (Constant.APP_KEY_WEIXIN.equals(url)) {
			cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
		} else if (Constant.APP_KEY_QQ.equals(url)) {
			cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
		} else if (Constant.APP_KEY_QQ_ZONE.equals(url)) {
			cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
		} else if (Constant.APP_KEY_WEIBO.equals(url)) {
			cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
		} else {
			String keys[] = url.split("\\|");
			if (keys.length >= 2) {
				cmp = new ComponentName(keys[0], keys[1]);
			}
		}
		return cmp;
	}

	@Override
	public void api_sendBackKeyEvent() {
		api_sendKeyEvent(Constant.KEYCODE_BACK);
	}

	@Override
	public void api_sendHomeKeyEvent() {
		api_sendKeyEvent(Constant.KEYCODE_HOME);
	}

	@Override
	public void api_sendKeyEvent(int arg0) {
		String command = "input keyevent " + rebuldKeyCode(arg0);
		Log.d(TAG, "api_sendKeyEvent:" + command);
		ShellUtils.execCommand(command, true);
	}

	private int rebuldKeyCode(int code) {
		return code;
	}

	@Override
	public void api_sendMenuKeyEvent() {
		api_sendKeyEvent(Constant.KEYCODE_MENU);
	}

	@Override
	public int api_sendMessage(String arg0, String arg1) {
		return 0;
	}

	@Override
	public void api_sendTapGesture(int arg0, int arg1) {
		String command = "input tap " + rebuldLocation(arg0) + " " + rebuldLocation(arg1);
		ShellUtils.execCommand(command, true);
	}

	private int rebuldLocation(int location) {
		return location;
	}

	@Override
	public void api_sleep(long arg0) {
		try {
			Thread.sleep(arg0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void api_toast(final String arg0) {
		ScriptManager.getManager().getMainHandler().post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ScriptManager.getManager().getContext(), arg0, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void api_sendFlingGesture(int X1, int Y1, int X2, int Y2) {
		List<String> commands = calculateTouchEvent(X1, Y1, X2, Y2, FLING_TOUCH_DURATION);
		Log.d(TAG, "api_sendFlingGesture" + commands);
		ShellUtils.execCommand(commands, true);
	}

	@Override
	public void api_sendDownGesture(int arg0, int arg1) {

	}

	@Override
	public void api_sendLongPressGesture(int arg0, int arg1) {

	}

	@Override
	public void api_sendFlingGesture(int arg0) {
		Log.d(TAG, "api_sendFlingGesture:" + arg0);
		int X1 = 0, Y1 = 0, X2 = 0, Y2 = 0;
		Context context = ScriptManager.getManager().getContext();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		switch (arg0) {
		case Constant.TOUCH_DIRECTION_LR:
			X1 = (int) (width * 0.3f + Math.random() * 30);
			Y1 = (int) (height * 0.65f + Math.random() * 30);
			X2 = (int) (width * 0.55f + Math.random() * 30);
			Y2 = (int) (height * 0.6f + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_RL:
			X2 = (int) (width * 0.3f + Math.random() * 30);
			Y2 = (int) (height * 0.65f + Math.random() * 30);
			X1 = (int) (width * 0.55f + Math.random() * 30);
			Y1 = (int) (height * 0.6f + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_TB:
			X1 = (int) (width * 0.7f + Math.random() * 30);
			Y1 = (int) (height * 0.45f + Math.random() * 30);
			X2 = (int) (width * 0.65f + Math.random() * 30);
			Y2 = (int) (height * 0.75f + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_BT:
			X2 = (int) (width * 0.7f + Math.random() * 30);
			Y2 = (int) (height * 0.45f + Math.random() * 30);
			X1 = (int) (width * 0.65f + Math.random() * 30);
			Y1 = (int) (height * 0.75f + Math.random() * 30);
			break;
		default:
			return;
		}
		List<String> commands = calculateTouchEvent(X1, Y1, X2, Y2, FLING_TOUCH_DURATION);
		ShellUtils.execCommand(commands, true);
	}

	@Override
	public void api_sendFlingGesture(int direction, float locationScale) {
		Log.d(TAG, "api_sendFlingGesture:" + direction + "l:" + locationScale);
		int X1 = 0, Y1 = 0, X2 = 0, Y2 = 0;
		Context context = ScriptManager.getManager().getContext();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		switch (direction) {
		case Constant.TOUCH_DIRECTION_LR:
			X1 = (int) (width * 0.3f + Math.random() * 30);
			Y1 = (int) (height * locationScale + Math.random() * 30);
			X2 = (int) (width * 0.55f + Math.random() * 30);
			Y2 = (int) (height * (locationScale + 0.5) + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_RL:
			X2 = (int) (width * 0.3f + Math.random() * 30);
			Y2 = (int) (height * locationScale + Math.random() * 30);
			X1 = (int) (width * 0.55f + Math.random() * 30);
			Y1 = (int) (height * (locationScale + 0.5) + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_TB:
			X1 = (int) (width * locationScale + Math.random() * 30);
			Y1 = (int) (height * 0.45f + Math.random() * 30);
			X2 = (int) (width * (locationScale + 0.2f) + Math.random() * 30);
			Y2 = (int) (height * 0.75f + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_BT:
			X2 = (int) (width * locationScale + Math.random() * 30);
			Y2 = (int) (height * 0.45f + Math.random() * 30);
			X1 = (int) (width * (locationScale + 0.2f) + Math.random() * 30);
			Y1 = (int) (height * 0.75f + Math.random() * 30);
			break;
		default:
			return;
		}
		List<String> commands = calculateTouchEvent(X1, Y1, X2, Y2, FLING_TOUCH_DURATION);
		ShellUtils.execCommand(commands, true);
	}

	@Override
	public void api_sendFlingGesture(int direction, int location) {
		Log.d(TAG, "api_sendFlingGesture direction:" + direction + "l:" + location);
		int X1 = 0, Y1 = 0, X2 = 0, Y2 = 0;
		Context context = ScriptManager.getManager().getContext();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		switch (direction) {
		case Constant.TOUCH_DIRECTION_LR:
			X1 = (int) (width * 0.3f + Math.random() * 30);
			Y1 = (int) (location + Math.random() * 30);
			X2 = (int) (width * 0.55f + Math.random() * 30);
			Y2 = (int) (location + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_RL:
			X2 = (int) (width * 0.3f + Math.random() * 30);
			Y2 = (int) (location + Math.random() * 30);
			X1 = (int) (width * 0.55f + Math.random() * 30);
			Y1 = (int) (location + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_TB:
			X1 = (int) (location + Math.random() * 30);
			Y1 = (int) (height * 0.45f + Math.random() * 30);
			X2 = (int) (width * 0.65f + Math.random() * 30);
			Y2 = (int) (height * 0.75f + Math.random() * 30);
			break;
		case Constant.TOUCH_DIRECTION_BT:
			X2 = (int) (location + Math.random() * 30);
			Y2 = (int) (height * 0.45f + Math.random() * 30);
			X1 = (int) (width * 0.65f + Math.random() * 30);
			Y1 = (int) (height * 0.75f + Math.random() * 30);
			break;
		default:
			return;
		}
		List<String> commands = calculateTouchEvent(X1, Y1, X2, Y2, FLING_TOUCH_DURATION);
		ShellUtils.execCommand(commands, true);
	}

	@Override
	public void api_sendLongPressGesture(int arg0, int arg1, long arg2) {
		// TODO Auto-generated method stub
		Log.d(TAG, "api_sendFlingGesture 2");

	}

	@Override
	public void api_sendScrollGesture(int arg0, int arg1, int arg2, int arg3) {
		Log.d(TAG, "api_sendFlingGesture 3");
		List<String> commands = calculateTouchEvent(arg0, arg1, arg2, arg3, SCROLL_TOUCH_DURATION);
		ShellUtils.execCommand(commands, true);
		
	}

	@Override
	public void api_sendScrollGesture(int arg0, int arg1, int arg2, int arg3, long arg4) {
		Log.d(TAG, "api_sendFlingGesture 4");
		List<String> commands = calculateTouchEvent(arg0, arg1, arg2, arg3, arg4);
		ShellUtils.execCommand(commands, true);
	}

	private List<String> calculateTouchEvent(int X1, int Y1, int X2, int Y2, long time) {
		Path path = new Path();
		path.moveTo(X1, Y1);
		float k1 = (Y1 - Y2) / (X1 - X2 + 0f);
		float k2 = -(X1 - X2) / (Y1 - Y2 + 0f);

		float total = (float) Math.sqrt((Y1 - Y2) * (Y1 - Y2) + (X1 - X2) * (X1 - X2));
		float dots[] = { 1 / 4f, 1 / 3f, 1 / 2f, 3 / 5f, 3 / 4f, 5 / 6f, 1f };
		float point[] = new float[2];
		float[] point2 = new float[2];
		float[] point3 = new float[2];
		k1 = Math.abs(k1);
		double angle = Math.atan(k1);
		double angle2 = Math.atan(k2);
		float flagX = X1 < X2 ? 1 : -1;
		float flagY = Y1 < Y2 ? 1 : -1;
		int N = dots.length;
		float lastDistance = 0;
		for (int i = 0; i < N; i++) {
			float distance = dots[i] * total;
			point[0] = (float) (X1 + distance * Math.cos(angle) * flagX);
			point[1] = (float) (Y1 + distance * Math.sin(angle) * flagY);
			float distance2 = (float) (lastDistance + (distance - lastDistance) * Math.random());
			lastDistance = distance;
			point2[0] = (float) (X1 + distance2 * Math.cos(angle) * flagX);
			point2[1] = (float) (Y1 + distance2 * Math.sin(angle) * flagY);
			int seed = (int) ((System.currentTimeMillis() % 100 * Math.random()));
			if (seed % 3 == 0) {
				distance = (float) (30 + 10 * Math.random());
			} else {
				distance = 0;
			}
			point3[0] = (float) (point2[0] + distance * Math.cos(angle2));
			point3[1] = (float) (point2[1] + distance * Math.sin(angle2));
			path.quadTo(point3[0], point3[1], point[0], point[1]);
			if (i > 2 && i < N - 2) {
				seed = (int) ((System.currentTimeMillis() % 100 * Math.random()));
				if (seed % 3 != 0) {
					i++;
					lastDistance = dots[i] * total;
				}
			}
		}
		return TouchEventCreater.touchOnPath(path, time);
	}
}
