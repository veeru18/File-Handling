package org.vwf.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageResponse {
    private Long imageId;
    @NotEmpty(message = "Image Filename is required")
    private String imageFileName;
    @NotEmpty(message = "Image Type is required")
    private String imageType;

    // newer fields outside of entity
    private double fileWriteTime;
    private double fileSize;
}
