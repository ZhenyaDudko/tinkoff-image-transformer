package com.app.exception;

public class ImageNotAccessibleException extends Exception {

    /**
     * Constructor.
     */
    public ImageNotAccessibleException() {
        super("Image not found");
    }
}
