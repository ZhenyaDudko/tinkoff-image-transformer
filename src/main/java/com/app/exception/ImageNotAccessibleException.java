package com.app.exception;

public class ImageNotAccessibleException extends Exception {

    public ImageNotAccessibleException() {
        super("Image not found");
    }
}
