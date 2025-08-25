package org.vwf.file_handling.upload.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vwf.file_handling.filters.TokenUtils;
import org.vwf.file_handling.upload.constant.AppConstants;
import org.vwf.file_handling.upload.constant.ErrorMessage;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.dto.LoginRequest;
import org.vwf.file_handling.upload.dto.LoginResponse;
import org.vwf.file_handling.upload.dto.RegisterRequest;
import org.vwf.file_handling.upload.dto.RegisterResponse;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.InvalidRequestDataException;
import org.vwf.file_handling.upload.exceptions.PasswordValidationFailException;
import org.vwf.file_handling.upload.exceptions.UserAlreadyExistsException;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.repository.UserRepository;
import org.vwf.file_handling.upload.utility.HelperService;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final HelperService helperService;


    public ResponseEntity<GenericResponse<RegisterResponse>> register(RegisterRequest registerRequest) throws RuntimeException {
        log.info("Inside register method: {}", registerRequest.getEmail());
        if (ObjectUtils.isEmpty(registerRequest))
            throw new RuntimeException(ErrorMessage.REQUEST_DATA_EMPTY.getMessage());
        Optional<User> byEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (byEmail.isPresent())
            throw new UserAlreadyExistsException(ErrorMessage.USER_EXISTS_ERROR.getMessage());
        User mappedUser = modelMapper.map(registerRequest, User.class);
        Map<Boolean, String> validated = helperService.validatePassword(registerRequest.getPassword());
        if (ObjectUtils.isNotEmpty(validated))
            throw new PasswordValidationFailException(validated.get(false));
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        mappedUser.setPassword(encodedPassword);
        User savedUser = userRepository.save(mappedUser);
        RegisterResponse registerResponse = modelMapper.map(savedUser, RegisterResponse.class);
        GenericResponse<RegisterResponse> response = GenericResponse.success(ResponseMessage.USER_REGISTER_SUCCESS.getMessage(),
                registerResponse);
        HttpHeaders headers = new HttpHeaders();
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);
    }

    public ResponseEntity<GenericResponse<LoginResponse>> login(LoginRequest loginRequest) throws RuntimeException {
        log.info("Inside login method: {}", loginRequest.getEmail());
        if (ObjectUtils.isEmpty(loginRequest))
            throw new InvalidRequestDataException(ErrorMessage.REQUEST_DATA_EMPTY.getMessage());
        User existUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        // fetching what will be set internally in auth obj using userDetailService
        // that sets principal as userDetail obj, creds as null and authorities as userDetail.getAuthorities
        // which is set similarly during filter's token check to set auth obj for each request
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        // token generated is being sent here
        String token = tokenUtils.generateToken(userDetail);
        LoginResponse loginResponse = modelMapper.map(existUser, LoginResponse.class);
//        loginResponse.setToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
//        headers.set(AppConstants.TOKEN, token);
        GenericResponse<LoginResponse> response = GenericResponse.success(ResponseMessage.USER_LOGIN_SUCCESS.getMessage(),
                loginResponse);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);
    }

}
