package com.wecodee.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor @Data
public class DocumentDTO {
    private Long documentId;
    private String originalFileName;
    private String storedFileName;
    @NotNull(message = "FileType should be available in payload")
    @NotEmpty(message = "FileType should not be Empty")
    private String fileType;
    @NotNull(message = "FileLocation should be available in payload")
    @NotEmpty(message = "FileLocation should not be Empty")
    private String fileLocation; //storing it as file in a disk drive memory location
//    private String extension;
}
