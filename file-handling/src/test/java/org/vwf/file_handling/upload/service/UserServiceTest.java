package org.vwf.file_handling.upload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.dto.UserRequest;
import org.vwf.file_handling.upload.dto.UserResponse;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.exceptions.UserUpdateException;
import org.vwf.file_handling.upload.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    public void setVarsOfDependencies() {
        JwtFilter.loggedInUserId = "someValue@gmail.com";
    }

    @AfterEach
    public void reset() {
        JwtFilter.loggedInUserId = null;
    }

    //method to be tested is having @Test
    @Test
    void getByUserId_returnsUser_whenExists() {
        log.info("Inside getByUserId_returnsUser_whenExists test");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Yo");
        mockUser.setPhoneNumber("9916548890");
        mockUser.setEmail("veeresh.ta@wecodee.com");
        // when() is static method of Mockito to test based on certain cases
        // that stubs(pushes) its required responses from its Autowired dependencies like thenThrow, thenReturn
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));

        UserResponse userResponse = modelMapper.map(mockUser, UserResponse.class);
        GenericResponse<UserResponse> genericResponse = GenericResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(), userResponse);
        // based on when() conditions, tests are done using these Assertions class methods
        // how to inject loggedInUserId static variable of JwtFilter aka email into this getUSer method
        assertEquals(genericResponse, userService.getUser());
    }


    @Test
    void getUser_throwsUserNotFoundException() {
        log.info("Inside getUser_throwsUserNotFoundException");
        assertThrows(UserNotFoundException.class, () -> userService.getUser());
    }

    @Test
    void updateUser_throwsUserUpdateException() {
        log.info("Inside updateUser_throwsUserUpdateException");
        UserRequest userRequest = new UserRequest("Yo", "9916548890");

        assertThrows(UserUpdateException.class, () -> userService.updateUser(null));
        assertThrows(UserUpdateException.class, () -> userService.updateUser(userRequest));
    }

}
