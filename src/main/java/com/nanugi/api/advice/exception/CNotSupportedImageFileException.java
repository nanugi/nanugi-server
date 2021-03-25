package com.nanugi.api.advice.exception;

public class CNotSupportedImageFileException extends RuntimeException {
    public CNotSupportedImageFileException(String msg, Throwable t) {
        super(msg, t);
    }

    public CNotSupportedImageFileException(String msg) {
        super(msg);
    }

    public CNotSupportedImageFileException() {
        super();
    }
}
