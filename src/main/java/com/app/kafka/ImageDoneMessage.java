package com.app.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ImageDoneMessage {
    /**
     * ИД итогового изображения
     */
    private String imageId;
    /**
     * ИД пользовательского запроса.
     */
    private String requestId;
}
