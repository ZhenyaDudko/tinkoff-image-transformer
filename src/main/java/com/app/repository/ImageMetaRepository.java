package com.app.repository;

import com.app.model.ImageMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageMetaRepository extends JpaRepository<ImageMeta, Integer> {

    /**
     * Find image meta by image id.
     * @param imageId image id.
     * @return optional ImageMeta entity.
     */
    Optional<ImageMeta> findImageMetaByImageId(String imageId);

    /**
     * Find image meta for all images uploaded by user.
     * @param userId user id.
     * @return Image meta for found images.
     */
    List<ImageMeta> findAllByUserId(Long userId);
}
