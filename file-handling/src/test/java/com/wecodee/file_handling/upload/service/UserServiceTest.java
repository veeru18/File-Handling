package com.wecodee.file_handling.upload.service;

import com.wecodee.file_handling.upload.constant.ApiResponse;
import com.wecodee.file_handling.upload.constant.ResponseMessage;
import com.wecodee.file_handling.upload.dto.UserDTO;
import com.wecodee.file_handling.upload.entity.User;
import com.wecodee.file_handling.upload.exceptions.UserDeleteException;
import com.wecodee.file_handling.upload.exceptions.UserNotFoundException;
import com.wecodee.file_handling.upload.exceptions.UserSaveException;
import com.wecodee.file_handling.upload.exceptions.UserUpdateException;
import com.wecodee.file_handling.upload.repository.UserRepository;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
// or @SpringBootTest to mark class as integration tester class
public class UserServiceTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //dependencies autowired inside the currently testing class is annotated with @Mock
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    //the currently testing class is annotated with @InjectMocks
    @InjectMocks
    private UserService userService;

    //method to be tested is having @Test
    @Test
    void getByUserId_returnsUser_whenExists() {
        log.info("Inside getByUserId_returnsUser_whenExists test");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Yo");
        mockUser.setPhoneNumber("9916548890");
        // when() is static method of Mockito to test based on certain cases
        // that stubs(pushes) its required responses from its Autowired dependencies like thenThrow, thenReturn
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        JSONObject data = new JSONObject(Map.of("user", mockUser));
        ApiResponse<JSONObject> apiResponse = ApiResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(), data);
        // based on when() conditions, tests are done using these Assertions class methods
        assertEquals(apiResponse, userService.getUser(1L));
    }

    @Test
    void saveUser_returnsUserSaved() {
        log.info("Inside saveUser_returnsUserSaved");
        UserDTO userDTO = new UserDTO("Yo", "9916548890");
        User mappedUser = new User();
        mappedUser.setUsername("Yo");
        mappedUser.setPhoneNumber("9916548890");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Yo");
        mockUser.setPhoneNumber("9916548890");
        // when() is static method of Mockito to test based on certain cases
        // that stubs(pushes) its required responses from its Autowired dependencies like thenThrow, thenReturn
        when(modelMapper.map(userDTO, User.class)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(mockUser);

        JSONObject data = new JSONObject(Map.of("user", mockUser));
        ApiResponse<JSONObject> apiResponse = ApiResponse.success(ResponseMessage.USER_SAVE_SUCCESS.getMessage(), data);
        // based on when() conditions, tests are done using these Assertions class methods
        assertEquals(apiResponse, userService.saveUser(userDTO));
    }

    @Test
    void saveUser_throwsUserSaveException() {
        log.info("Inside saveUser_throwsUserNotFoundException");
        UserDTO userDTO = null;
        assertThrows(UserSaveException.class, () -> userService.saveUser(userDTO));
    }

    @Test
    void getUser_throwsUserNotFoundException() {
        log.info("Inside getUser_throwsUserNotFoundException");
        assertThrows(UserNotFoundException.class, () -> userService.getUser(2L));
    }

    @Test
    void updateUser_throwsUserUpdateException() {
        UserDTO userDTO = new UserDTO("Yo","9916548890");

        assertThrows(UserUpdateException.class, ()-> userService.updateUser(1L,null));
        assertThrows(UserUpdateException.class, ()-> userService.updateUser(null, userDTO));
    }

    @Test
    void deleteUser_throwsUserDeleteException() {
        // adding the throw when any long value is passed

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Yo");
        mockUser.setPhoneNumber("9916548890");
        JSONObject data = new JSONObject(Map.of("deletedUser", mockUser));
        ApiResponse<JSONObject> apiResponse = ApiResponse.success(ResponseMessage.USER_DELETE_SUCCESS.getMessage(), data);

        List<Long> longs = List.of(1L, 3L, 5L, 7L);
        // when() is static method of Mockito to test based on certain cases
        // that stubs(pushes) its required responses from its Autowired dependencies like thenThrow, thenReturn
        when(userRepository.findById(argThat(id->id!=null&&longs.contains(id)))).thenThrow(UserDeleteException.class);
        when(userRepository.findById(argThat(id->(id!=null&&id%2==0)))).thenReturn(Optional.of(mockUser));
        // for null id passed
        // based on when() conditions, tests are done using these Assertions class methods
        assertThrows(UserDeleteException.class, ()->userService.deleteUser(null));
        assertThrows(UserDeleteException.class, ()->userService.deleteUser(1L));
        assertEquals(apiResponse, userService.deleteUser(2L));
    }
}
