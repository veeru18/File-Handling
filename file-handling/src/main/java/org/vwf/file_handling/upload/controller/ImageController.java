package org.vwf.file_handling.upload.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.dto.ImageDTO;
import org.vwf.file_handling.upload.service.ImageService;

@RestController
@RequestMapping("/uploads/images")
@RequiredArgsConstructor
@Api(tags = "Image Uploads Handler", description = "Used for uploading images into DB memory as CLOBs")
public class ImageController {
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    @PostMapping()
    public GenericResponse<ImageDTO> imageUpload(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        return imageService.writeImageToDb(multipartFile);
    }

    @GetMapping("/{imageId}/distType/{disType}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("imageId") Long imageId,
                                                       @PathVariable("disType") String disType) {
        return imageService.getFile(imageId, disType);
    }
}
