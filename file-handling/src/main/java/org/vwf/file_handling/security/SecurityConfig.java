package org.vwf.file_handling.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.vwf.file_handling.filters.JwtFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomUserDetailService customUserDetailService;

    private static final String[] PUBLIC_GET_URLS = new String[]{
    };
    private static final String[] PUBLIC_POST_URLS = new String[]{
            "/authenticate/login",
            "/authenticate/register",
            "/swagger-ui.html"
    };

    // newer approach wouldn't need this as the webSecConfigAdapter wasn't extended
    // configured AuthenticationManager with UserDetailsService and password encoder to be injected in super.getauthManager bean, thus overriding
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
    }

    // and in newer approach the super call gets replaced
    // as authManager bean(AuthConfig authconfig) needs only authconfig.getAuthManager when it needs it
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // this will become SecurityFilterChain @Bean from springSecurity 5.7+ and boot 3.x with no need to extend WebSecConfigurerAdapter
    // using the overridden configure(httpSecurity) security filter chain
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests()
                .antMatchers(HttpMethod.POST, PUBLIC_POST_URLS).permitAll()
                .antMatchers(HttpMethod.POST, PUBLIC_GET_URLS).permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
