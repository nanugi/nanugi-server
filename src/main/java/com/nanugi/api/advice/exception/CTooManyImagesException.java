package com.nanugi.api.advice.exception;

public class CTooManyImagesException extends RuntimeException {
    public CTooManyImagesException(String msg, Throwable t) {
        super(msg, t);
    }

    public CTooManyImagesException(String msg) {
        super(msg);
    }

    public CTooManyImagesException() {
        super();
    }
}
