package com.wecodee.file_handling.upload.controller;

import com.wecodee.file_handling.upload.constant.ApiResponse;
import com.wecodee.file_handling.upload.dto.UserDTO;
import com.wecodee.file_handling.upload.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;

@RestController
@RequiredArgsConstructor
@Api(tags = "Users Handler", description = "Used for handling users that needs their files maintained")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PostMapping("/user")
    public ApiResponse<JSONObject> saveUser(@RequestBody UserDTO userDTO) {
        HttpHeaders headers = new HttpHeaders();
        Cookie cookie = new Cookie("niqq","someNigga");
        cookie.setMaxAge(60*60);

        ResponseEntity<String> hello = ResponseEntity
                .status(HttpStatus.OK)
                .header(cookie.getName(),cookie.getValue())
                .body("hello");
        return userService.saveUser(userDTO);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<JSONObject> getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/user/{userId}")
    public ApiResponse<JSONObject> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        return userService.updateUser(userId,userDTO);
    }

    @DeleteMapping("/user/{userId}")
    public ApiResponse<JSONObject> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}
