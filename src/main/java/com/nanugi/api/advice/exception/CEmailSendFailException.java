package com.nanugi.api.advice.exception;

public class CEmailSendFailException extends RuntimeException {
    public CEmailSendFailException(String msg, Throwable t) {
        super(msg, t);
    }

    public CEmailSendFailException(String msg) {
        super(msg);
    }

    public CEmailSendFailException() {
        super();
    }
}
