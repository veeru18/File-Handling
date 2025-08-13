package org.vwf.file_handling.upload.service;

import org.vwf.file_handling.upload.constant.ApiResponse;
import org.vwf.file_handling.upload.constant.ErrorMessage;
import org.vwf.file_handling.upload.constant.HelperService;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.dto.UserDetailsDTO;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.UserDeleteException;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.exceptions.UserSaveException;
import org.vwf.file_handling.upload.exceptions.UserUpdateException;
import org.vwf.file_handling.upload.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final HelperService helperService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public ApiResponse<JSONObject> saveUser(UserDetailsDTO userDetailsDTO) {
        log.info("Inside saveUser method");
        JSONObject respObject = new JSONObject();
        if (ObjectUtils.isEmpty(userDetailsDTO))
            throw new UserSaveException(ErrorMessage.USER_NOT_FOUND_INREQUEST.getMessage());
        User mappedUser = modelMapper.map(userDetailsDTO, User.class);
        User savedUser = userRepository.save(mappedUser);
        respObject.put("user", savedUser);
        return ApiResponse.success(ResponseMessage.USER_SAVE_SUCCESS.getMessage(), respObject);
    }

    public ApiResponse<JSONObject> getUser(Long userId) {
        log.info("Inside getUser method: {}", userId);
        if (Objects.isNull(userId))
            throw new UserNotFoundException(ErrorMessage.USERID_NOT_FOUND_INREQUEST.getMessage());
        JSONObject respObject = new JSONObject();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        respObject.put("user", user);
        return ApiResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(), respObject);
    }

    public ApiResponse<JSONObject> updateUser(Long userId, UserDetailsDTO userDetailsDTO) {
        log.info("Inside updateUser method");
        JSONObject respObject = new JSONObject();
        if (ObjectUtils.isEmpty(userDetailsDTO) || Objects.isNull(userId))
            throw new UserUpdateException(ErrorMessage.USER_NOT_FOUND_INREQUEST.getMessage());
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserUpdateException(ErrorMessage.USER_UPDATE_FAIL.getMessage()));
        modelMapper.map(userDetailsDTO, existingUser);
        User updatedUser = userRepository.saveAndFlush(existingUser);
        respObject.put("user", updatedUser);
        return ApiResponse.success(ResponseMessage.USER_UPDATE_SUCCESS.getMessage(), respObject);
    }


    public ApiResponse<JSONObject> deleteUser(Long userId) {
        log.info("Inside deleteUser method: {}", userId);
        JSONObject respObject = new JSONObject();
        if (Objects.isNull(userId))
            throw new UserDeleteException(ErrorMessage.USERID_NOT_FOUND_INREQUEST.getMessage());
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserDeleteException(ErrorMessage.USER_DELETE_FAIL.getMessage()));
        userRepository.delete(existingUser);
        respObject.put("deletedUser", existingUser);
        return ApiResponse.success(ResponseMessage.USER_DELETE_SUCCESS.getMessage(), respObject);
    }
}
