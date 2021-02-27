package com.nanugi.api.advice.exception;

public class BBoardNotFoundException extends RuntimeException {
    public BBoardNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public BBoardNotFoundException(String msg) {
        super(msg);
    }

    public BBoardNotFoundException() {
        super();
    }
}
