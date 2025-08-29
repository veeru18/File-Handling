package org.vwf.file_handling.upload.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.upload.constant.ErrorMessage;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.dto.UserResponse;
import org.vwf.file_handling.upload.utility.HelperService;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.dto.UserRequest;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.exceptions.UserUpdateException;
import org.vwf.file_handling.upload.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final HelperService helperService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public GenericResponse<List<UserResponse>> getAllUsers() {
        log.info("Inside getAllUsers method");
        long count = userRepository.count();
        if (count <= 0)
            throw new UserNotFoundException(ErrorMessage.NO_USERS_EXIST.getMessage());
        return GenericResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(),
                objectMapper.convertValue(userRepository.findAll(), new TypeReference<List<UserResponse>>() {
                }));
    }

    public GenericResponse<UserResponse> getUser() {
        log.info("Inside getUser method");
        User user = userRepository.findByEmail(JwtFilter.loggedInUserId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        return GenericResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(), userResponse);
    }

    public GenericResponse<UserResponse> updateUser(UserRequest userRequest) {
        log.info("Inside updateUser method");
        if (ObjectUtils.isEmpty(userRequest))
            throw new UserUpdateException(ErrorMessage.USER_NOT_FOUND_INREQUEST.getMessage());
        // finding by email since email is taken as username in
        User existingUser = userRepository.findByEmail(JwtFilter.loggedInUserId)
                .orElseThrow(() -> new UserUpdateException(ErrorMessage.USER_UPDATE_FAIL.getMessage()));
        modelMapper.map(userRequest, existingUser);
        UserResponse userResponse = modelMapper.map(
                userRepository.saveAndFlush(existingUser), UserResponse.class);
        return GenericResponse.success(ResponseMessage.USER_UPDATE_SUCCESS.getMessage(), userResponse);
    }

    public GenericResponse<JSONObject> getUsersByPage(Integer pageNumber, Integer pageSize) {
        log.info("Inside getUsersByPage method");
        JSONObject paginatedList = HelperService.getPaginatedList(userRepository.findAll(), pageNumber, pageSize);
        List<UserResponse> finalRespList = objectMapper.convertValue(
                paginatedList.get("items"), new TypeReference<List<UserResponse>>() {
                });

        paginatedList.put("items", finalRespList);
        return GenericResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(), paginatedList);
    }
}
