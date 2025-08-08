package com.wecodee.file_handling.upload.repository;

import com.wecodee.file_handling.upload.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageFileName(String imageFileName);

}
