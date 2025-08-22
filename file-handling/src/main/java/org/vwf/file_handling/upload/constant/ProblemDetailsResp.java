package org.vwf.file_handling.upload.constant;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

// error response structure according to "RFC7807 standard"
@Data
@NoArgsConstructor
//@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemDetailsResp {
    private String type; // to point error as URI
    private String title; // to display error occurred as readable text
    private Integer status; // httpCode
    // optional
    private String detail; // description of where that error occurred
    private String instance; // URI path where the exception has occurred

}
