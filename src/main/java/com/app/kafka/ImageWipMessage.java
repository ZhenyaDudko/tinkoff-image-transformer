package com.app.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ImageWipMessage {
    /**
     * ИД изображения с которым сейчас ведется работа по данному запросу.
     */
    private String imageId;
    /**
     * ИД пользовательского запроса.
     */
    private String requestId;
    /**
     * Фильтры, которые нужно применить.
     */
    private List<String> filters;
}
