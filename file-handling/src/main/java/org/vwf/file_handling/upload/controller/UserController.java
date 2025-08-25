package org.vwf.file_handling.upload.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.dto.UserRequest;
import org.vwf.file_handling.upload.dto.UserResponse;
import org.vwf.file_handling.upload.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Api(tags = "Users Handler", description = "Used for handling users that needs their files maintained")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping("/all")
    public GenericResponse<List<UserResponse>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/page/{pageNo}/size/{size}")
    public GenericResponse<JSONObject> getAllUsersByPage(@PathVariable("pageNo") Integer pageNo,
                                                         @PathVariable("size") Integer pageSize) {
        return userService.getUsersByPage(pageNo,pageSize);
    }

    @GetMapping()
    public GenericResponse<UserResponse> getUser() {
        return userService.getUser();
    }

    @PutMapping()
    public GenericResponse<UserResponse> updateUser(@RequestBody UserRequest userRequest) {
        return userService.updateUser(userRequest);
    }

    // delete user operation removed since one user cant be allowed to handle other users without roles implemented to them
}
