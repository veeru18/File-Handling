package org.vwf.file_handling.upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.vwf.file_handling.upload.entity.Image;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageFileName(String imageFileName);

    @Query(value = "select UF_DOC_SEQ.nextVal from dual", nativeQuery = true)
    Long getImageValue();
}
