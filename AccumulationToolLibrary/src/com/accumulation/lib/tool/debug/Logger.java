package com.accumulation.lib.tool.debug;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class Logger {
	private static boolean DEBUG = true;

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
}
