package com.accumulation.lib.utility.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class Logger {
	private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1
	private static boolean DEBUG = true;

	private Logger()
	{
        /* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static String TAG = "Accumulation";

	public static void d(String msg) {
		if (DEBUG)
			Log.d(TAG, msg);
	}
	public static void d(String tag,String msg) {
		if (DEBUG)
			Log.d(TAG+"->"+tag, msg);
	}
	public static void d(String msg, Throwable t) {
		if (DEBUG)
			Log.d(TAG, msg, t);
	}

	public static void e(String msg) {
		if (DEBUG)
			Log.e(TAG, msg);
	}
	public static void e(String tag,String msg) {
		if (DEBUG)
			Log.e(TAG+"->"+tag, msg);
	}
	public static void e(String msg, Throwable t) {
		if (DEBUG)
			Log.e(TAG, msg, t);
	}

	public static void printMemory(String msg) {
		d(msg);
		d("maxMemory: " + Runtime.getRuntime().maxMemory() / 1024 + "KB");
		d("totalMemory: " + Runtime.getRuntime().totalMemory() / 1024 + "KB");
		d("freeMemory: " + Runtime.getRuntime().freeMemory() / 1024 + "KB");
		d("nativeHeapSize: " + android.os.Debug.getNativeHeapSize() / 1024
				+ "KB");
		d("nativeHeapAllocatedSize: "
				+ android.os.Debug.getNativeHeapAllocatedSize() / 1024 + "KB");
		d("nativeHeapFreeSize: " + android.os.Debug.getNativeHeapFreeSize()
				/ 1024 + "KB");
	}

	public static ArrayList<String> splitString(String text, int sliceSize) {
		ArrayList<String> textList = new ArrayList<String>();
		String aux;
		int left = -1, right = 0;
		int charsLeft = text.length();
		while (charsLeft != 0) {
			left = right;
			if (charsLeft >= sliceSize) {
				right += sliceSize;
				charsLeft -= sliceSize;
			} else {
				right = text.length();
				aux = text.substring(left, right);
				charsLeft = 0;
			}
			aux = text.substring(left, right);
			textList.add(aux);
		}
		return textList;
	}

	public static ArrayList<String> splitString(String text) {
		return splitString(text, 2000);
	}

	public static void splitAndLog(String tag, String text) {
		ArrayList<String> messageList = splitString(text);
		for (String message : messageList) {
			Log.d(tag, message);
		}
	}
	public static void printViewState(String desc, View v) {
		Rect r = new Rect();
		v.getGlobalVisibleRect(r);
		d(desc + "globalVisibleRect: left=" + r.left + ", right=" + r.right + ", top="
				+ r.top + ", bottom=" + r.bottom + ", visibility = " + v.getVisibility()
				+ ", focusable=" + v.isFocusable() + ", isFocused=" + v.isFocused());
	}

	public static void printAllChildren(String tag, ViewGroup vg, boolean recursive) {
		int count = vg.getChildCount();
		for (int i = 0; i < count; i++) {
			View v = vg.getChildAt(i);
			printViewState(tag + " " + i + " - 0x" + Integer.toHexString(v.getId()) + " ", v);
			if (v instanceof ViewGroup && recursive) {
				printAllChildren(tag, (ViewGroup) v, recursive);
			}
		}
	}

	/**
	 * To stack trace string string.
	 * <p/>
	 * 此方法参见：https://github.com/Ereza/CustomActivityOnCrash
	 *
	 * @param throwable the throwable
	 * @return the string
	 */
	public static String toStackTraceString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		String stackTraceString = sw.toString();
		//Reduce data to 128KB so we don't get a TransactionTooLargeException when sending the intent.
		//The limit is 1MB on Android but some devices seem to have it lower.
		//See: http://developer.android.com/reference/android/os/TransactionTooLargeException.html
		//And: http://stackoverflow.com/questions/11451393/what-to-do-on-transactiontoolargeexception#comment46697371_12809171
		if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
			String disclaimer = " [stack trace too large]";
			stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
		}
		return stackTraceString;
	}
}
