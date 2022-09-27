package com.tejacodes.springsecurityclient.repository;

import com.tejacodes.springsecurityclient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
}
