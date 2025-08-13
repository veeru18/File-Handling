package com.wecodee.file_handling.upload.service;

import com.wecodee.file_handling.constant.ApiResponse;
import com.wecodee.file_handling.constant.ErrorMessage;
import com.wecodee.file_handling.constant.HelperService;
import com.wecodee.file_handling.constant.ResponseMessage;
import com.wecodee.file_handling.filters.JwtTokenUtils;
import com.wecodee.file_handling.upload.dto.LoginRegisterDTO;
import com.wecodee.file_handling.upload.dto.LoginResponse;
import com.wecodee.file_handling.upload.dto.RegisterResponse;
import com.wecodee.file_handling.upload.entity.User;
import com.wecodee.file_handling.upload.exceptions.*;
import com.wecodee.file_handling.upload.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private final HelperService helperService;


    public ApiResponse<JSONObject> register(LoginRegisterDTO registerDto) throws RuntimeException {
        log.info("Inside register method: {}", registerDto.getEmail());
        if(ObjectUtils.isEmpty(registerDto))
            throw new RuntimeException(ErrorMessage.REQUEST_DATA_EMPTY.getMessage());
        Optional<User> byEmail = userRepository.findByEmail(registerDto.getEmail());
        if(byEmail.isPresent())
            throw new UserAlreadyExistsException(ErrorMessage.USER_EXISTS_ERROR.getMessage());
        User mappedUser = modelMapper.map(registerDto, User.class);
        Map<Boolean, String> validated = helperService.validatePassword(registerDto.getPassword());
        if(ObjectUtils.isNotEmpty(validated))
            throw new PasswordValidationFailException(validated.get(false));
        String encoded = bCryptPasswordEncoder.encode(registerDto.getPassword());
        mappedUser.setPassword(encoded);
        User savedUser = userRepository.save(mappedUser);
        RegisterResponse registerResponse = modelMapper.map(savedUser, RegisterResponse.class);
        return ApiResponse.success(ResponseMessage.USER_REGISTER_SUCCESS.getMessage(),
                new JSONObject(Map.of("user", registerResponse)));
    }

    public ApiResponse<JSONObject> login(LoginRegisterDTO loginDto) throws RuntimeException {
        log.info("Inside login method: {}", loginDto.getEmail());
        if(ObjectUtils.isEmpty(loginDto))
            throw new InvalidRequestDataException(ErrorMessage.REQUEST_DATA_EMPTY.getMessage());
        User existUser = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(()-> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage()));
        if(!bCryptPasswordEncoder.matches(loginDto.getPassword(),existUser.getPassword())) {
            throw new InvalidPasswordException(ErrorMessage.INVALID_PASSWORD_ENTERED.getMessage());
        }

        LoginResponse loginResponse = modelMapper.map(existUser, LoginResponse.class);
        return ApiResponse.success(ResponseMessage.USER_LOGIN_SUCCESS.getMessage(),
                new JSONObject(Map.of("loginDetails", loginResponse)));
    }

}
