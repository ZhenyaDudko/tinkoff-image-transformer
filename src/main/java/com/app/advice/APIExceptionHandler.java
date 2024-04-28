package com.app.advice;

import com.app.dto.UiSuccessContainer;
import com.app.exception.FilterRequestNotFoundException;
import com.app.exception.ImageIsTooBigException;
import com.app.exception.ImageNotAccessibleException;
import com.app.exception.ImageNotFoundException;
import com.app.exception.NotSupportedTypeOfImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public final class APIExceptionHandler {

    /**
     * Handler for invalid image exceptions.
     * @param ex caught exception.
     * @return response with error message.
     */
    @ExceptionHandler({
            ImageIsTooBigException.class,
            NotSupportedTypeOfImageException.class
    })
    public ResponseEntity<UiSuccessContainer> imageNotValid(
            final Exception ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new UiSuccessContainer(false, ex.getMessage()));
    }

    /**
     * Handler for not found or not accessible image.
     * @param ex caught exception.
     * @return response with error message.
     */
    @ExceptionHandler({
            ImageNotAccessibleException.class,
            ImageNotFoundException.class,
            FilterRequestNotFoundException.class,
    })
    public ResponseEntity<UiSuccessContainer> imageNotFount(
            final Exception ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new UiSuccessContainer(false, ex.getMessage()));
    }

    /**
     * Handler for unexpected exceptions.
     * @param ex caught exception.
     * @return response with error message.
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<UiSuccessContainer> unexpectedError(
            final Exception ex
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UiSuccessContainer(false, ex.getMessage()));
    }
}
