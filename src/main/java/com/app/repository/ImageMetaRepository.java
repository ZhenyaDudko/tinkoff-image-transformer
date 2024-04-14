package com.app.repository;

import com.app.model.ImageMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageMetaRepository extends JpaRepository<ImageMeta, Integer> {

    Optional<ImageMeta> findImageMetaByImageId(String imageId);
    boolean existsByImageId(String imageId);
}
