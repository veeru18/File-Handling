package org.vwf.file_handling.upload.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.dto.LoginRequest;
import org.vwf.file_handling.upload.dto.LoginResponse;
import org.vwf.file_handling.upload.dto.RegisterRequest;
import org.vwf.file_handling.upload.dto.RegisterResponse;
import org.vwf.file_handling.upload.service.AuthenticationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
@Api(tags = "Authentication Handler", description = "Authentication APIs like login/register")
public class AuthenticationController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authenticationService.register(registerRequest);
    }
}
