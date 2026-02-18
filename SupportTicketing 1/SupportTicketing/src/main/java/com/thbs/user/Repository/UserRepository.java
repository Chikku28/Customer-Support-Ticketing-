package com.thbs.user.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
}
