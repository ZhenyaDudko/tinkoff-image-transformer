package com.app.exception;

public class NotSupportedTypeOfImageException extends RuntimeException {

    /**
     * Constructor.
     */
    public NotSupportedTypeOfImageException() {
        super("Provided image has unsupported type");
    }
}
