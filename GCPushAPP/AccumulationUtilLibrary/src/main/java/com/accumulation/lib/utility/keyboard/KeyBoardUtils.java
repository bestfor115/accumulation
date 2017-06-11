package com.accumulation.lib.utility.keyboard;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 打开或关闭软键盘
 *
 * @author zhy
 *
 */
public class KeyBoardUtils
{
    /**
     * 打卡软键盘
     *
     * @param dditText
     *            输入框
     * @param context
     *            上下文
     */
    public static void openKeybord(EditText dditText, Context context)
    {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(dditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param dditText
     *            输入框
     * @param context
     *            上下文
     */
    public static void closeKeybord(EditText dditText, Context context)
    {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(dditText.getWindowToken(), 0);
    }

    // 强制显示或者关闭系统键盘
    public static void keyBoard(final EditText dditText, final boolean open) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) dditText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (open) {
                    m.showSoftInput(dditText,
                            InputMethodManager.SHOW_FORCED);
                } else {
                    m.hideSoftInputFromWindow(dditText.getWindowToken(), 0);
                }
            }
        }, 300);
    }

    // 通过定时器强制隐藏虚拟键盘
    public static void timerHideKeyboard(final View v) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),
                            0);
                }
            }
        }, 10);
    }

    // 输入法是否显示着
    public static boolean keyBoard(EditText edittext) {
        boolean bool = false;
        InputMethodManager imm = (InputMethodManager) edittext.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            bool = true;
        }
        return bool;
    }
}