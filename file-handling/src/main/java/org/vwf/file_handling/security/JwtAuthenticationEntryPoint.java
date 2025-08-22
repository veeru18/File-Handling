package org.vwf.file_handling.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized, Please try to login with valid credentials");
        Throwable jwtException = (Throwable) request.getAttribute("jwt_exception");
        String message = authException.getMessage();
        if(jwtException instanceof MalformedJwtException) {
            handleException(response, jwtException.getMessage(), message, HttpServletResponse.SC_FORBIDDEN);
        } else if (jwtException instanceof JwtException) {
            handleException(response, jwtException.getMessage(), message, HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleException(HttpServletResponse response, String error, String details, int status) throws IOException {
//        response.setStatus(status);
        response.setContentType("application/json");
        response.sendError(status,
                String.format("{\"error\":\"%s\",\"details\":\"%s\"}", error, details)
        );
    }
}
