package com.nanugi.api.advice.exception;

public class CNicknameAlreadyExistException extends RuntimeException {
    public CNicknameAlreadyExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public CNicknameAlreadyExistException(String msg) {
        super(msg);
    }

    public CNicknameAlreadyExistException() {
        super();
    }
}
