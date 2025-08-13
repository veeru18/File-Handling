package org.vwf.file_handling.upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vwf.file_handling.upload.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
