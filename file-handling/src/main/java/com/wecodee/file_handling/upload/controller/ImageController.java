package com.wecodee.file_handling.upload.controller;


import com.wecodee.file_handling.constant.ApiResponse;
import com.wecodee.file_handling.upload.service.ImageService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image-uploader")
@RequiredArgsConstructor
@Api(tags = "Image Uploads Handler", description = "Used for uploading images into DB memory as CLOBs")
public class ImageController {
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    @PostMapping("/upload/{userId}")
    public ApiResponse<JSONObject> imageUpload(@PathVariable Long userId,
                                               @RequestParam(name = "file") MultipartFile multipartFile) throws Exception {
        return imageService.writeImageToDb(userId, multipartFile);
    }

    @GetMapping("/get")
    public ResponseEntity<InputStreamResource> getFile(@RequestParam("userId") Long userId,
                                                       @RequestParam(value = "imageId") Long imageId,
                                                       @RequestParam("disType") String disType) {
        return imageService.getFile(userId, imageId, disType);
    }
}
