package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageResponse {
    private String imageId;
    private String filename;
    private long size;
}
