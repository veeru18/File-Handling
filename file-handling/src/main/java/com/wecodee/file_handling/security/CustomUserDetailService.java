package com.wecodee.file_handling.security;

import com.wecodee.file_handling.constant.ErrorMessage;
import com.wecodee.file_handling.upload.exceptions.UserNotFoundException;
import com.wecodee.file_handling.upload.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Getter @Setter
@RequiredArgsConstructor
@Service
@Qualifier("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user-> new CustomUserDetail(user.getEmail(), user.getPassword()))
                .orElseThrow(()->new UserNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
    }
}
