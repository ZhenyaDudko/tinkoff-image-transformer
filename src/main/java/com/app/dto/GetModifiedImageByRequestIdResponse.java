package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GetModifiedImageByRequestIdResponse {
    /**
     * ИД модифицированного или оригинального файла в случае отсутствия первого.
     */
    private String imageId;

    /**
     * Статус обработки файла
     */
    private String status;
}
