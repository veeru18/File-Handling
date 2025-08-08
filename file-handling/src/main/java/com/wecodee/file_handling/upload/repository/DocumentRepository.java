package com.wecodee.file_handling.upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wecodee.file_handling.upload.entity.Document;
import org.springframework.data.jpa.repository.Query;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "select UF_DOC_SEQ.nextVal from dual", nativeQuery = true)
    Long getDocumentValue();
}
