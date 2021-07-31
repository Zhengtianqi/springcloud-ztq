package com.ztq.common;


/**
 * @author zhengtianqi
 */
public class Result<T> {

    private String code;

    private String message;

    private T data;

    public Result(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public Result(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(String code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data.getClass() +
                '}';
    }
}
