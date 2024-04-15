package com.app.exception;

public final class ImageIsTooBigException extends RuntimeException {

    /**
     * Constructor.
     * @param size received image size.
     * @param maxSize maximum allowed image size.
     */
    public ImageIsTooBigException(final long size, final long maxSize) {
        super("Provided image has size "
                + size
                + ", maximum accepted size is " + maxSize
        );
    }
}
