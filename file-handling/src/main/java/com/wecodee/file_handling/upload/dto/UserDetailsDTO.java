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
public class UserDetailsDTO {
    private Long id;
    private String username;
    private String phoneNumber;
//    private String email;

    public UserDetailsDTO(String username, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.username = username;
    }
}
