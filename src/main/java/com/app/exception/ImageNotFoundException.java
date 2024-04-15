package com.app.exception;

public class ImageNotFoundException extends Exception {

    /**
     * Constructor.
     */
    public ImageNotFoundException() {
        super("Image not found");
    }
}
