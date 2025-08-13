package com.wecodee.file_handling.upload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRegisterDTO {
    @NotEmpty(message = "EmailId is required")
    private String email;
    @NotEmpty(message = "Password is required")
    private String password;
    @Length(min = 8, max = 15, message = "Number length should be within range of 8 and 15")
    private String phoneNumber;
}
