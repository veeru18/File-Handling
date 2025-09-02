package org.vwf.file_handling.upload.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.filters.TokenUtils;
import org.vwf.file_handling.security.CustomUserDetailService;
import org.vwf.file_handling.security.JwtAuthenticationEntryPoint;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.dto.LoginRequest;
import org.vwf.file_handling.upload.dto.LoginResponse;
import org.vwf.file_handling.upload.dto.RegisterRequest;
import org.vwf.file_handling.upload.dto.RegisterResponse;
import org.vwf.file_handling.upload.service.AuthenticationService;

import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TokenUtils tokenUtils;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private CustomUserDetailService customUserDetailService;
    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setter() {
        JwtFilter.loggedInUserId = "veeeresh.ta@wecodee.com";
    }
    @AfterEach
    public void reset() {
        JwtFilter.loggedInUserId = null;
    }

    @Test
    public void login_returns() throws Exception {
        String jsonData = "{" +
                "  \"email\": \"veeresh.ta@wecodee.com\"," +
                "  \"password\": \"Veeee@1234\"" +
                "}";
        LoginRequest request = new LoginRequest("veeresh.ta@wecodee.com","Veeee@1234");
        LoginResponse response = new LoginResponse(1L,"veeresh.ta@wecodee.com","9876543210");
        GenericResponse<LoginResponse> success = GenericResponse.success(ResponseMessage.USER_LOGIN_SUCCESS.getMessage(), response);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("<someToken>");
        given(authenticationService.login(request)).willReturn(ResponseEntity.ok().headers(headers).body(success));
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonData))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").isNumber());
    }

    @Test
    public void register_Returns() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("someEmail@wecodee.com");
        request.setPassword("Veeee@1234");
        String jsonData = "{" +
                "  \"email\": \"someEmail@wecodee.com\"," +
                "  \"password\": \"Veeee@1234\"" +
                "}";
        RegisterResponse response = new RegisterResponse(1L,"someEmail@wecodee.com",null);
        GenericResponse<RegisterResponse> success = GenericResponse.success(ResponseMessage.USER_LOGIN_SUCCESS.getMessage(), response);
        given(authenticationService.register(request)).willReturn(ResponseEntity.ok().body(success));
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/register")
                    .content(jsonData)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString());
    }
}
