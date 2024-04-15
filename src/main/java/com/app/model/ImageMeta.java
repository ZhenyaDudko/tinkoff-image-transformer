package com.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "image-meta")
public class ImageMeta {

    /**
     * Image id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Image original name.
     */
    private String name;

    /**
     * Image size.
     */
    private Long size;

    /**
     * Image id in storage.
     */
    private String imageId;

    /**
     * Image media type.
     */
    private String mediaType;

    /**
     * Image owner id.
     */
    private Long userId;
}
