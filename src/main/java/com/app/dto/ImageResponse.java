package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ImageResponse {
    /**
     * Image id in s3.
     */
    private String imageId;

    /**
     * Original file name.
     */
    private String filename;

    /**
     * Image size.
     */
    private long size;
}
