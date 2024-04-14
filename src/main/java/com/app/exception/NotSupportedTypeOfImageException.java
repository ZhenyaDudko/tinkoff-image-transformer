package com.app.exception;

public class NotSupportedTypeOfImageException extends RuntimeException {

    public NotSupportedTypeOfImageException() {
        super("Provided image has unsupported type");
    }
}
