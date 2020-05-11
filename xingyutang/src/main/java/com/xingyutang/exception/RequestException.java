package com.xingyutang.exception;

public class RequestException extends Exception {
    private int code;
    private String message;

    public RequestException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public RequestException(String message) {
        this.code = 1;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
