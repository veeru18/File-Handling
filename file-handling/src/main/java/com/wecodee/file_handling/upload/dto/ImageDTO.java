package com.wecodee.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor @Data
public class ImageDTO {
    private Long imageId;
    private String imageData; // storing it as byte[]/aka clob
    @NotNull(message = "ImageFilename should be available in payload")
    @NotEmpty(message = "ImageFilename should not be Empty")
    private String imageFileName;
    @NotNull(message = "ImageType should be available in payload")
    @NotEmpty(message = "ImageType should not be Empty")
    private String imageType;
//    private String extension;

}
