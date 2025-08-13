package org.vwf.file_handling.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.vwf.file_handling.upload.constant.ErrorMessage;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.repository.UserRepository;

@Getter
@Setter
@RequiredArgsConstructor
@Service
@Qualifier("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> new CustomUserDetail(user.getEmail(), user.getPassword()))
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
    }
}
