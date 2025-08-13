package org.vwf.file_handling.upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.vwf.file_handling.upload.entity.Document;

import java.util.stream.Stream;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "select UF_DOC_SEQ.nextVal from dual", nativeQuery = true)
    Long getDocumentValue();

    @Query(value = "select d FROM Document d")
    Stream<Document> streamAllDocuments();
}
