package com.nanugi.api.advice.exception;

public class CEmailNotVerifiedException extends RuntimeException {
    public CEmailNotVerifiedException() {
    }

    public CEmailNotVerifiedException(String message) {
        super(message);
    }

    public CEmailNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
