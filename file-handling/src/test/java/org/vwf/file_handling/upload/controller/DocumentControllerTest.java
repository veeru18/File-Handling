package org.vwf.file_handling.upload.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.filters.TokenUtils;
import org.vwf.file_handling.security.CustomUserDetailService;
import org.vwf.file_handling.security.JwtAuthenticationEntryPoint;
import org.vwf.file_handling.upload.service.DocumentService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
// for SB v2.7.1 securityFilterChain will be shown as 2 beans error
public class DocumentControllerTest {
    @MockBean
    private DocumentService documentService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TokenUtils tokenUtils;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
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

}
