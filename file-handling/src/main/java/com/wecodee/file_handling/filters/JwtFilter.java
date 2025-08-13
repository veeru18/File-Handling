package com.wecodee.file_handling.filters;

import com.wecodee.file_handling.upload.repository.UserRepository;
import com.wecodee.file_handling.security.CustomUserDetailService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static String loggedInUserId = null;
    public static String clientIp = null;
    public static String url = null;
    public static String rType = null;
    public static String requestBody = null;
    public static int status = 0;
    public static Date intime = null;
    public static String jwtToken = null;

    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Inside doFilter method..");
        clientIp = request.getRemoteAddr();
        url = request.getRequestURL().toString();
        rType = request.getMethod();
//         requestBody = getRequestBodyData(request.getReader());
        status = response.getStatus();
        intime = new Date(new java.util.Date().getTime());

        final String requestTokenHeader = request.getHeader("Authorization");
        String data;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                data = jwtTokenUtils.getDataFromToken(jwtToken);
                if (StringUtils.isNotBlank(data) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    loggedInUserId = data;
                    UserDetails userDetails = customUserDetailService.loadUserByUsername(loggedInUserId);
                    // if token is valid configure Spring Security to manually set the context's authentication
                    if (jwtTokenUtils.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        // After setting the Authentication in the context, we specify
                        // that the current user is authenticated. So it passes the
                        // Spring Security Configurations successfully.
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    } else
                        throw new JwtException("Invalid Jwt Token, please try Login again");
                }
            } catch (Exception e) {
                log.error("Exception in jwtFilter", e);
                throw new RuntimeException(e);
            }
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws RuntimeException {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        if (pathMatcher.match("/swagger-ui.html", request.getServletPath()) ) {
//                || pathMatcher.match("/authenticate/login", request.getServletPath())
//                || pathMatcher.match("/authenticate/register", request.getServletPath())) {
            return true;
        }
        return false;
    }

    public String getRequestBodyData(BufferedReader reader) {
        StringBuilder jb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) jb.append(line);
            reader.close();
        } catch (IOException e) {
            log.error("exception in getRequestBodyData", e);
        }
        log.info("body-> {}", jb);
        log.info(jb.toString());
        return jb.toString();
    }
}
