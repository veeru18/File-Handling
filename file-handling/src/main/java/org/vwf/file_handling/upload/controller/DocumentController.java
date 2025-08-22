package org.vwf.file_handling.upload.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.dto.DocumentResponse;
import org.vwf.file_handling.upload.service.DocumentService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uploads/document")
@Api(tags = "Documents Uploads Handler", description = "Used for uploading files into in-server memory")
public class DocumentController {
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    @PostMapping()
    public GenericResponse<DocumentResponse> uploadFile(@RequestParam(name = "file") MultipartFile multipartFile) throws IOException {
        return documentService.uploadFile(multipartFile);
    }

    @GetMapping()
    public ResponseEntity<InputStreamResource> getFile(@RequestParam Long documentId,
                                                       @RequestParam String disType) {
        return documentService.getFile(documentId, disType);
    }
}
