package com.zyl.push.sdk.script;

public interface Scriptable {

    void api_toast(String msg);
    void api_openApp(String app);
    void api_tapById(String id);
    void api_tapByText(String text);
    void api_sleep(long delay);
    int api_sendMessage(String number, String content);
    int api_callPerson(String number);
    void api_inputString(String content);
    void api_sendKeyEvent(int keyCode);
    void api_sendBackKeyEvent();
    void api_sendHomeKeyEvent();
    void api_sendMenuKeyEvent();

    void api_sendTapGesture(int x, int y);
    void api_sendLongPressGesture(int x, int y);
    void api_sendLongPressGesture(int x, int y, long time);
    void api_sendScrollGesture(int fromX, int formY, int toX, int toY);
    void api_sendScrollGesture(int fromX, int formY, int toX, int toY, long time);
    void api_sendFlingGesture(int direction);
    void api_sendFlingGesture(int direction, float locationScale);
    void api_sendFlingGesture(int direction, int location);
    void api_sendFlingGesture(int fromX, int formY, int toX, int toY);
    void api_sendDownGesture(int x, int y);
    void api_setInputMethodState(int state);

}
