package com.nanugi.api.advice.exception;

public class CTooManyFavsException extends RuntimeException {
    public CTooManyFavsException(String msg, Throwable t) {
        super(msg, t);
    }

    public CTooManyFavsException(String msg) {
        super(msg);
    }

    public CTooManyFavsException() {
        super();
    }
}
