package com.wecodee.file_handling.upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wecodee.file_handling.upload.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
