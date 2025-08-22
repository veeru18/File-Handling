package org.vwf.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotEmpty(message = "EmailId is required")
    @Email(message = "Please pass a valid email ID")
    private String email;
    @NotEmpty(message = "Password is required")
    private String password;
}
