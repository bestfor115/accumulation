package com.zyl.push.sdk.script;

public interface Scriptable {

    public void api_toast(String msg);
    public void api_openApp(String app);
    public void api_sleep(long delay);
    public int api_sendMessage(String number, String content);
    public int api_callPerson(String number);
    public void api_inputString(String content);
    public void api_sendKeyEvent(int keyCode);
    public void api_sendBackKeyEvent();
    public void api_sendHomeKeyEvent();
    public void api_sendMenuKeyEvent();

    public void api_sendTapGesture(int x, int y);
    public void api_sendLongPressGesture(int x, int y);
    public void api_sendLongPressGesture(int x, int y, long time);
    public void api_sendScrollGesture(int fromX, int formY, int toX, int toY);
    public void api_sendScrollGesture(int fromX, int formY, int toX, int toY, long time);
    public void api_sendFlingGesture(int direction);
    public void api_sendFlingGesture(int direction, float locationScale);
    public void api_sendFlingGesture(int direction, int location);
    public void api_sendFlingGesture(int fromX, int formY, int toX, int toY);
    public void api_sendDownGesture(int x, int y);
}
