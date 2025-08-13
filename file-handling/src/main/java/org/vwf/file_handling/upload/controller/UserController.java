package org.vwf.file_handling.upload.controller;

import org.vwf.file_handling.upload.constant.ApiResponse;
import org.vwf.file_handling.upload.dto.UserDetailsDTO;
import org.vwf.file_handling.upload.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = "Users Handler", description = "Used for handling users that needs their files maintained")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PostMapping("/user")
    public ApiResponse<JSONObject> saveUser(@RequestBody UserDetailsDTO userDetailsDTO) {
        return userService.saveUser(userDetailsDTO);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<JSONObject> getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/user/{userId}")
    public ApiResponse<JSONObject> updateUser(@PathVariable Long userId, @RequestBody UserDetailsDTO userDetailsDTO) {
        return userService.updateUser(userId, userDetailsDTO);
    }

    @DeleteMapping("/user/{userId}")
    public ApiResponse<JSONObject> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}
