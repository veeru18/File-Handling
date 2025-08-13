package org.vwf.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageDTO {
    private Long imageId;
    private String imageData; // storing it as byte[]/aka clob
    @NotEmpty(message = "Image Filename is required")
    private String imageFileName;
    @NotEmpty(message = "Image Type is required")
    private String imageType;
//    private String extension;

}
