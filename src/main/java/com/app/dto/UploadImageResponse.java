package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UploadImageResponse {
    /**
     * Image id in s3.
     */
    private String imageId;
}
