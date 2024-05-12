package com.app.kafka;

import com.app.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageWipMessage implements Serializable {
    /**
     * ИД изображения с которым сейчас ведется работа по данному запросу.
     */
    private String imageId;
    /**
     * ИД пользовательского запроса.
     */
    private String requestId;
    /**
     * Формат изображения.
     */
    private String mediaType;
    /**
     * Фильтры, которые нужно применить.
     */
    private List<ImageService.Filter> filters;
}
