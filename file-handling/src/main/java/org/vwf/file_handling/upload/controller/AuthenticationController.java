package org.vwf.file_handling.upload.controller;

import org.vwf.file_handling.upload.constant.ApiResponse;
import org.vwf.file_handling.upload.dto.LoginRegisterDTO;
import org.vwf.file_handling.upload.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<JSONObject> login(@RequestBody @Valid LoginRegisterDTO loginDto) {
        return authenticationService.login(loginDto);
    }

    @PostMapping("/register")
    public ApiResponse<JSONObject> register(@RequestBody @Valid LoginRegisterDTO registerDto) {
        return authenticationService.register(registerDto);
    }
}
