package com.app.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDoneMessage implements Serializable {
    /**
     * ИД итогового изображения.
     */
    private String imageId;
    /**
     * ИД пользовательского запроса.
     */
    private String requestId;
}
