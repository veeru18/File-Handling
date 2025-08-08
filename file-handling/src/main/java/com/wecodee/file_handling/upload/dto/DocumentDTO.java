package com.wecodee.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor @Data
public class DocumentDTO {
    private Long documentId;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private String fileLocation; //storing it as file in a disk drive memory location
//    private String extension;
}
