package com.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "image-meta")
public class ImageMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String name;

    private Long size;

    @Column(length = 300)
    private String imageId;

    private String mediaType;

    private Long userId;
}
