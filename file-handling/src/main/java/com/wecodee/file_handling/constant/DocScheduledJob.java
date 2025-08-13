package com.wecodee.file_handling.constant;

import com.wecodee.file_handling.upload.entity.Document;
import com.wecodee.file_handling.upload.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class DocScheduledJob {
    private static final Logger log = LoggerFactory.getLogger(DocScheduledJob.class);

    private final DocumentRepository documentRepository;

    // everyday at 5am (or)  "0 0 5 * * 6,7" every sat and sun @5am
    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    public void removeDocEntitiesDaily() {
        log.info("Inside removeDocEntitiesDaily method");
        // if a file doesn't exist for a given saved record's path, then deleting it by ID
        AtomicInteger count = new AtomicInteger(0);
        try (Stream<Document> docStream = documentRepository.streamAllDocuments()) {
            docStream.forEach(doc -> {
                try {
                    String location = doc.getFileLocation();
                    boolean missingFile = location == null || location.isBlank()
                            || !Files.isRegularFile(Path.of(location));

                    if (missingFile)
                        documentRepository.deleteById(doc.getDocumentId());

                    if (count.incrementAndGet() % 50 == 0)
                        documentRepository.flush();
                } catch (Exception e) {
                    // log and delete the record if path is invalid
                    log.error("Exception during delete of a record with id {} : {}", doc.getDocumentId(), e.getMessage());
                    documentRepository.deleteById(doc.getDocumentId());
                }
            });

        } catch (Exception e) {
            log.error("Exception in removeDocEntitiesDaily", e);
        }
    }
}
