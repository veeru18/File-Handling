package org.vwf.file_handling.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
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
import org.vwf.file_handling.security.CustomUserDetailService;
import org.vwf.file_handling.upload.constant.ErrorMessage;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public String loggedInUserId = null;
    public String clientIp = null;
    public static String url = null;
    public String rType = null;
    public int status = 0;
    public Date intime = null;
    public String jwtToken = null;

    private final TokenUtils tokenUtils;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        clientIp = request.getRemoteAddr();
        url = request.getRequestURL().toString();
        rType = request.getMethod();
        status = response.getStatus();
        intime = new Date(new java.util.Date().getTime());

        final String authReqTokenHeader = request.getHeader("Authorization");
        String data;
        if (authReqTokenHeader != null && authReqTokenHeader.startsWith("Bearer ")) {
            log.info("Inside JwtFilter's doFilter method..");
            jwtToken = authReqTokenHeader.substring(7);
            try {
                data = tokenUtils.getDataFromToken(jwtToken);
                if (StringUtils.isNotBlank(data) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    loggedInUserId = data;
                    UserDetails userDetails = customUserDetailService.loadUserByUsername(loggedInUserId);
                    // if token is valid configure Spring Security to manually set the context's authentication
                    if (tokenUtils.validateToken(jwtToken, userDetails)) {
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
            } catch (ExpiredJwtException e) {
                log.error("Expired Exception in jwtFilter", e);
                delegateFilterExceptions(request, ErrorMessage.EXPIRED_JWT.getMessage(), e);
            } catch (MalformedJwtException e) {
                log.error("Malformed Exception in jwtFilter", e);
                delegateFilterExceptions(request, ErrorMessage.MALFORMED_JWT.getMessage(), e);
            } catch (JwtException e) {
                log.error("Jwt Exception in jwtFilter", e);
                delegateFilterExceptions(request, ErrorMessage.JWT_FILTER_ERROR.getMessage(), e);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws RuntimeException {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String servletPath = request.getServletPath();
        return pathMatcher.match("/swagger-ui.html", servletPath) ||
                pathMatcher.match("/swagger-ui/**", servletPath) ||
                pathMatcher.match("/swagger-resources/**", servletPath) ||
                pathMatcher.match("/v2/api-docs", servletPath) ||
                pathMatcher.match("/webjars/**", servletPath) ||
                pathMatcher.match("/authenticate/login", servletPath) ||
                pathMatcher.match("/authenticate/register", servletPath);
    }

    protected void delegateFilterExceptions(HttpServletRequest request, String error,
                                            JwtException e) {
        request.setAttribute("jwt_exception", e);
        // delegating to jwtAuthenticationEntryPoint
        throw new org.springframework.security.core.AuthenticationException(error, e) {};
    }
}
