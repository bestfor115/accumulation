package com.zyl.push.sdk.script;

public class Script implements Executable,Constant/*,Scriptable*/{

    private Scriptable mScriptImpl;
    public Script(){
    }
	final public void bindImpl(Scriptable impl) {
		mScriptImpl=impl;
	}
	public String exec(String... objects) {
		return null;
	}
	public int fetchAPIVersion() {
	    return API_VERSION_BASE;
	}
	public int fetchPlatform() {
	    return ANDROID_PLATFORM;
	}
	final public boolean isAndroidPlatform() {
	    return fetchPlatform()==fetchPlatform();
	}
    final public void api_sendKeyEvent(int keyCode) {
        mScriptImpl.api_sendKeyEvent(keyCode);
    }
    final public void api_toast(String msg){
        mScriptImpl.api_toast(msg);
    }
	final public void api_openApp(String app) {
		mScriptImpl.api_openApp(app);
	}
	final public void api_sleep(long delay) {
		mScriptImpl.api_sleep(delay);
	}
	final public int api_sendMessage(String number, String content) {
		return mScriptImpl.api_sendMessage(number, content);
	}
	final public int api_callPerson(String number) {
		return mScriptImpl.api_callPerson(number);
	}
	final public void api_inputString(String content) {
		mScriptImpl.api_inputString(content);
	}
	final public void api_sendBackKeyEvent() {
		mScriptImpl.api_sendBackKeyEvent();
	}
	final public void api_sendHomeKeyEvent() {
		mScriptImpl.api_sendHomeKeyEvent();		
	}
	final public void api_sendMenuKeyEvent() {
		mScriptImpl.api_sendMenuKeyEvent();		
	}
	final public void api_sendTapGesture(int x, int y) {
		mScriptImpl.api_sendTapGesture(x,y);		
	}
	final public void api_sendLongPressGesture(int x, int y) {
		mScriptImpl.api_sendLongPressGesture(x,y);		
	}
	final public void api_sendDownGesture(int x, int y) {
		mScriptImpl.api_sendDownGesture(x,y);		
	}
    public void api_sendFlingGesture(int fromX, int formY, int toX, int toY) {
        mScriptImpl.api_sendFlingGesture(fromX, formY, toX, toY);
    }
    public void api_sendLongPressGesture(int x, int y, long time) {
        mScriptImpl.api_sendLongPressGesture(x, y, time);
    }
    public void api_sendScrollGesture(int fromX, int formY, int toX, int toY) {
        mScriptImpl.api_sendScrollGesture(fromX, formY, toX, toY);
    }
    public void api_sendScrollGesture(int fromX, int formY, int toX, int toY, long time) {
        mScriptImpl.api_sendScrollGesture(fromX, formY, toX, toY,time);
    }
    public void api_sendFlingGesture(int direction) {
        mScriptImpl.api_sendFlingGesture(direction);
    }
    public void api_sendFlingGesture(int direction, float locationScale) {
        mScriptImpl.api_sendFlingGesture(direction, locationScale);
    }
    public void api_sendFlingGesture(int direction, int location) {
        mScriptImpl.api_sendFlingGesture(direction, location);
    }

}
