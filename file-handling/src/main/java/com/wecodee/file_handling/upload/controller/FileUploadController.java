package com.wecodee.file_handling.upload.controller;

import com.wecodee.file_handling.upload.constant.ApiResponse;
import com.wecodee.file_handling.upload.service.FileUploadService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file-uploader")
@Api(tags = "File Uploads Handler", description = "Used for uploading files into in-server memory")
public class FileUploadController {
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final FileUploadService fileUploadService;

    @PostMapping("/upload/{userId}")
    public ApiResponse<JSONObject> uploadFile(@PathVariable Long userId, @RequestParam(name = "file") MultipartFile multipartFile) throws IOException {
        return fileUploadService.uploadFile(userId, multipartFile);
    }

    @GetMapping("/get")
    public ResponseEntity<InputStreamResource> getFile(@RequestParam Long userId,
                                                       @RequestParam Long documentId,
                                                       @RequestParam String disType) {
        return fileUploadService.getFile(userId, documentId, disType);
    }
}
