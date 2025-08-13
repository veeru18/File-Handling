package org.vwf.file_handling.upload.repository;

import org.vwf.file_handling.upload.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageFileName(String imageFileName);

}
