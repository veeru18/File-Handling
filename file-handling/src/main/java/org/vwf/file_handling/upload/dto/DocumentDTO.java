package org.vwf.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentDTO {
    private Long documentId;
    private String originalFileName;
    private String storedFileName;
    @NotEmpty(message = "FileType is required")
    private String fileType;
    @NotEmpty(message = "FileLocation is required")
    private String fileLocation; //storing it as file in a disk drive memory location
//    private String extension;
}
