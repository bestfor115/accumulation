package com.zyl.push.app.model;

import java.io.Serializable;

public class JsonResult<T> implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6263600536733150111L;

    private String message;
    private int code;
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 1;
    }

    public String fetchMessage() {
        return message == null ? "操作失败" : message;
    }
}
