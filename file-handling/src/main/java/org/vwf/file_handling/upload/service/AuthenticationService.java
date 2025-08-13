package org.vwf.file_handling.upload.service;

import org.vwf.file_handling.upload.constant.ApiResponse;
import org.vwf.file_handling.upload.constant.ErrorMessage;
import org.vwf.file_handling.upload.constant.HelperService;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.filters.JwtTokenUtils;
import org.vwf.file_handling.security.CustomUserDetail;
import org.vwf.file_handling.upload.dto.LoginRegisterDTO;
import org.vwf.file_handling.upload.dto.LoginResponse;
import org.vwf.file_handling.upload.dto.RegisterResponse;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.*;
import org.vwf.file_handling.upload.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManager authenticationManager;
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
        /* 1st way of handling the validation of loginRequest creating auth object after using userService.loadbyusername */
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginDto.getEmail(),
//                        loginDto.getPassword()
//                )
//        );
        // fetching what will be set internally in auth obj using userDetailService
        // that sets principal as userDetail obj, creds as null and authorities as userDetail.getAuthorities
        // which is set similarly during filter's token check to set auth obj for each request
//        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
//        String tokenByAuthObj = jwtTokenUtils.generateToken(userDetail);

        /* 2nd way of handling the validation of loginRequest validating  */
        if(!bCryptPasswordEncoder.matches(loginDto.getPassword(),existUser.getPassword())) {
            throw new InvalidPasswordException(ErrorMessage.INVALID_PASSWORD_ENTERED.getMessage());
        }
        // token generated is being sent here
        String token = jwtTokenUtils.generateToken(new CustomUserDetail(existUser.getEmail(), existUser.getPassword()));
        LoginResponse loginResponse = modelMapper.map(existUser, LoginResponse.class);
        loginResponse.setToken(token);
        return ApiResponse.success(ResponseMessage.USER_LOGIN_SUCCESS.getMessage(),
                new JSONObject(Map.of("loginDetails", loginResponse)));
    }

}
