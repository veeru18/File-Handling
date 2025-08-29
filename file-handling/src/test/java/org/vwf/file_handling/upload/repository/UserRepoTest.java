package org.vwf.file_handling.upload.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.vwf.file_handling.upload.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
// doesn't use(by internally self configured h2 db), if not added in pom.xml and in properties
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepoTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findAllReturnsNotNull() {
        List<User> users = userRepository.findAll();
        Assertions.assertNotNull(users);
    }

    @Test
    public void findByEmailReturnsEmpty() {
        Optional<User> byEmail = userRepository.findByEmail("hello");
        // assertJ's method
        assertThat(byEmail).isNotPresent();
    }

    @Test
    public void findByEmailReturnsMock() {
        User mockUser = new User();
        mockUser.setEmail("email");
        mockUser.setUsername("user");
        mockUser.setPhoneNumber("9876543210");

        userRepository.save(mockUser);
        //junit5's method
        assertEquals(Optional.of(mockUser),userRepository.findByEmail("email"));
    }
}
