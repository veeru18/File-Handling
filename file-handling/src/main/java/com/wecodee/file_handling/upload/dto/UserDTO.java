package com.wecodee.file_handling.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull(message = "Username should be available in payload")
    @NotEmpty(message = "Username should not be Empty")
    private String username;
    @NotNull(message = "Number should be available in payload")
    @NotEmpty(message = "Number should not be Empty")
    @Length(min = 8, max = 15, message = "PhoneNumber should be in range of 8 and 15 chars")
    private String phoneNumber;
}
