package com.wecodee.file_handling.upload.service;

import com.wecodee.file_handling.upload.constant.ApiResponse;
import com.wecodee.file_handling.upload.constant.HelperService;
import com.wecodee.file_handling.upload.constant.ResponseMessage;
import com.wecodee.file_handling.upload.dto.UserDTO;
import com.wecodee.file_handling.upload.entity.User;
import com.wecodee.file_handling.upload.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final HelperService helperService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

//    public UserService(HelperService helperService, ModelMapper modelMapper, UserRepository userRepository) {
//        this.helperService = helperService;
//        this.modelMapper = modelMapper;
//        this.userRepository = userRepository;
//    }

    public ApiResponse<JSONObject> saveUser(UserDTO userDTO) {
        log.info("Inside saveUser method");
        JSONObject respObject = new JSONObject();
        try {
            User mappedUser = modelMapper.map(userDTO, User.class);
            User savedUser = userRepository.save(mappedUser);
            respObject.put("user", savedUser);
            return ApiResponse.success(ResponseMessage.USER_SAVE_SUCCESS.getMessage(), respObject);
        } catch (Exception e) {
            log.error("Exception in saveUser method", e);
            respObject.put("exceptionMessage", e.getMessage());
            return ApiResponse.failure(ResponseMessage.USER_SAVE_FAILED.getMessage(), respObject);
        }
    }

    public ApiResponse<JSONObject> getUser(Long userId) {
        log.info("Inside getUser method: {}", userId);
        JSONObject respObject = new JSONObject();
        try {
            respObject.put("user", userRepository.findById(userId).orElse(null));
            return ApiResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(), respObject);
        } catch (Exception e) {
            log.error("Exception in getUser");
            respObject.put("exceptionMessage", e.getMessage());
            return ApiResponse.failure("User fetch failed", respObject);
        }
    }

    public ApiResponse<JSONObject> updateUser(Long userId, UserDTO userDTO) {
        log.info("Inside updateUser method");
        JSONObject respObject = new JSONObject();
        try {
            User existingUser = userRepository.findById(userId).orElse(null);
            if(ObjectUtils.isEmpty(existingUser))
                throw new IllegalArgumentException("Invalid userId for update");
            modelMapper.map(userDTO, existingUser);
            User savedUser = userRepository.save(existingUser);
            respObject.put("user", savedUser);
            return ApiResponse.success("User update success", respObject);
        } catch (Exception e) {
            log.error("Exception in updateUser method", e);
            respObject.put("exceptionMessage", e.getMessage());
            return ApiResponse.failure("User update failed", respObject);
        }
    }


    public ApiResponse<JSONObject> deleteUser(Long userId) {
        log.info("Inside deleteUser method: {}", userId);
        JSONObject respObject = new JSONObject();
        try {
            userRepository.deleteById(userId);
            respObject.put("deletedUser", userId);
            return ApiResponse.success("User delete success", respObject);
        } catch (Exception e) {
            log.error("Exception in deleteUser",e);
            respObject.put("exceptionMessage", e.getMessage());
            return ApiResponse.failure("User delete failed", respObject);
        }
    }
}
