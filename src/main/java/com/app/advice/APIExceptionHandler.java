package com.app.advice;

import com.app.dto.UiSuccessContainer;
import com.app.exception.ImageIsTooBigException;
import com.app.exception.ImageNotFoundException;
import com.app.exception.NotSupportedTypeOfImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class APIExceptionHandler {

    @ExceptionHandler({ImageIsTooBigException.class, NotSupportedTypeOfImageException.class})
    public ResponseEntity<UiSuccessContainer> imageNotValid(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new UiSuccessContainer(false, ex.getMessage()));
    }

    @ExceptionHandler({ImageIsTooBigException.class, ImageNotFoundException.class})
    public ResponseEntity<UiSuccessContainer> imageNotFount(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new UiSuccessContainer(false, ex.getMessage()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<UiSuccessContainer> unexpectedError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UiSuccessContainer(false, ex.getMessage()));
    }
}
