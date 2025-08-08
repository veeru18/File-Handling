package com.wecodee.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor @Data
public class ImageDTO {
    private Long imageId;
    private String imageData; // storing it as byte[]/aka clob
    private String imageFileName;
    private String imageType;
//    private String extension;

}
