package com.accumulation.lib.utility.base;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhangyl on 2016/7/30.
 */
public class Result {

    /**
     * 失败
     * */
    public static final int FAIL=0;

    /**
     * 成功
     * */
    public static final int SUCCESS=1;

    /** @hide */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true,
            value = {
                    FAIL,
                    SUCCESS
            })
    public @interface ResultCode {}

    private @ResultCode int code;
    private String msg;
    private Object extra;

    public boolean isSuccess(){
        return code==SUCCESS;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
