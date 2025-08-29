package org.vwf.file_handling.upload.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.filters.TokenUtils;
import org.vwf.file_handling.security.CustomUserDetailService;
import org.vwf.file_handling.security.JwtAuthenticationEntryPoint;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.service.UserService;

import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ModelMapper.class)
// for SB v2.7.1 securityFilterChain will be shown as 2 beans error
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private TokenUtils tokenUtils;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    @MockBean
//    private JwtFilter jwtFilter;

//    @MockBean
//    private SecurityConfig securityConfig;
    // for SB v2.7.1 securityFilterChain will be shown as having 2 beans error
//    @MockBean
//    private SecurityFilterChain springSecurityFilterChain;
    @MockBean
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    public void setter() {
        JwtFilter.loggedInUserId = "veeeresh.ta@wecodee.com";
    }
    @AfterEach
    public void reset() {
        JwtFilter.loggedInUserId = null;
    }

    @Test
    public void getUser_returnsValue() throws Exception {
//        String data = "{\"id\": 183548," +
//                "  \"username\": \"Veeru 17\"," +
//                "  \"phoneNumber\": \"9876543210\"," +
//                "  \"email\": \"veeresh.ta@wecodee.com\"" + "}";
        String content = "{\"success\":true,\"message\":\"User fetched successfully\",\"statusCode\":200}";
        String email = "veeresh.ta@wecodee.com";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Yo");
        mockUser.setPhoneNumber("9916548890");
        mockUser.setEmail(email);
//        UserResponse mapped = modelMapper.map(mockUser, UserResponse.class);
        when(userService.getUser()).thenReturn(
                GenericResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(),
                        null));
//        when(userService.getUser()).thenReturn(
//                GenericResponse.success(ResponseMessage.USER_FETCH_SUCCESS.getMessage(),
//                        mapped));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().json(content));
    }

    @Test
    public void getAllUser_returns() throws Exception {
        String data = "{" +
                "  \"username\": \"Veeru 17\"," +
                "  \"phoneNumber\": \"9876543210\"" + "}";
        String content = "{\"success\":true,\"message\":\"User update success\",\"statusCode\":200,\"data\":["+data+"]}";
        mockMvc.perform(MockMvcRequestBuilders.get("/users/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
//                .andExpect(MockMvcResultMatchers.content().string(content));
//                .andExpect(MockMvcResultMatchers.model().attribute("message",ResponseMessage.USER_FETCH_SUCCESS.getMessage()));
//                .andExpect(MockMvcResultMatchers.handler().handlerType(ApplicationExceptionHandler.class));
    }

    @Test
    public void updateUser_throws() throws Exception {
        String data = "{" +
                "  \"username\": \"Veeru 18\"," +
                "  \"phoneNumber\": \"9876543210\"," +
                "}";
//        String content = "{\"success\":true,\"message\":\"User update success\",\"statusCode\":200}";
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(data))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
//                .andExpect(MockMvcResultMatchers.content().json(content));
    }
}
