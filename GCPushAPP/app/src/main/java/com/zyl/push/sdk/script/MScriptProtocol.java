package com.zyl.push.sdk.script;

import android.content.Intent;
import android.util.Log;

public class MScriptProtocol /*implements Scriptable*/{

	private static final String TAG="lua-script";
    private static Scriptable mScriptImpl;

	static {
		System.loadLibrary("script-lib");
	}
	final public void bindImpl(Scriptable impl) {
		mScriptImpl=impl;
	}
	public void exec(String url){
		native_exec(url);
	}
	private native void native_exec(String url);
		
	private static final void api_openApp(String arg0) {
		Log.d(TAG,"api_openApp :"+arg0);
		mScriptImpl.api_openApp(arg0);
	}
	private static final void api_toast(String msg) {
		mScriptImpl.api_toast(msg);
	}
	private static final void api_sleep(long delay) {
		Log.d(TAG,"api_sleep :"+delay);
		//Log.d(TAG,String.format("NOTE: sleep value '%d' failed\n", delay));

		mScriptImpl.api_sleep(delay);
	}
	private static final int api_sendMessage(String number, String content) {
		return mScriptImpl.api_sendMessage(number,content);
	}
	private static final int api_callPerson(String number) {
		return mScriptImpl.api_callPerson(number);
	}
	private static final void api_inputString(String content) {
		mScriptImpl.api_inputString(content);
	}
	private static final void api_sendKeyEvent(int keyCode) {
		mScriptImpl.api_sendKeyEvent(keyCode);
	}
	private static final void api_sendBackKeyEvent() {
		mScriptImpl.api_sendBackKeyEvent();
	}
	private static final void api_sendHomeKeyEvent() {
		mScriptImpl.api_sendHomeKeyEvent();
	}
	private static final void api_sendMenuKeyEvent() {
		mScriptImpl.api_sendMenuKeyEvent();
	}
	private static final void api_sendTapGesture(int x, int y) {
		Log.d(TAG,"api_sendTapGesture :"+x+"  "+y);
		mScriptImpl.api_sendTapGesture(x,y);
	}
	private static final void api_sendLongPressGesture(int x, int y) {
		mScriptImpl.api_sendLongPressGesture(x,y);
	}
	private static final void api_sendLongPressGesture(int x, int y, long time) {
		mScriptImpl.api_sendLongPressGesture(x,y,time);
	}
	private static final void api_sendScrollGesture(int fromX, int formY, int toX, int toY) {
		mScriptImpl.api_sendScrollGesture(fromX,formY,toX,toY);
	}
	private static final void api_sendScrollGesture(int fromX, int formY, int toX, int toY, long time) {
		mScriptImpl.api_sendScrollGesture(fromX,formY,toX,toY,time);
	}
	private static final void api_sendFlingGesture(int direction) {
		mScriptImpl.api_sendFlingGesture(direction);
	}
	private static final void api_sendFlingGesture(int direction, float locationScale) {
		mScriptImpl.api_sendFlingGesture(direction,locationScale);
	}
	private static final void api_sendFlingGesture(int direction, int location) {
		mScriptImpl.api_sendFlingGesture(direction,location);
	}
	private static final void api_sendFlingGesture(int fromX, int formY, int toX, int toY) {
		mScriptImpl.api_sendFlingGesture(fromX,formY,toX,toY);
	}
	private static final void api_sendDownGesture(int x, int y) {
		mScriptImpl.api_sendDownGesture(x,y);
	}
	private static void api_tapById(String id) {
		mScriptImpl.api_tapById(id);
	}
	private static void api_tapByText(String text) {
		mScriptImpl.api_tapByText(text);
	}
	private static void api_setInputMethodState(int state){
		mScriptImpl.api_setInputMethodState(state);
	}
	private static void api_tapByImage(String path) {
		mScriptImpl.api_tapByImage(path);
	}

}
