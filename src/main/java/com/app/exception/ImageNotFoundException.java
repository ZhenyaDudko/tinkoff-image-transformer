package com.app.exception;

public class ImageNotFoundException extends Exception {

    public ImageNotFoundException() {
        super("Image not found");
    }
}
