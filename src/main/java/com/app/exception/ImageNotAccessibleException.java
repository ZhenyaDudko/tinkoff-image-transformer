package com.app.exception;

public class ImageNotAccessableException extends Exception {

    public ImageNotAccessableException() {
        super("Image not found");
    }
}
