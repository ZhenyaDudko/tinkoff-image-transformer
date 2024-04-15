package com.app.exception;

public final class ImageIsTooBigException extends RuntimeException {

    public ImageIsTooBigException(long size, long maxSize) {
        super("Provided image has size " + size + ", maximum accepted size is " + maxSize);
    }
}
