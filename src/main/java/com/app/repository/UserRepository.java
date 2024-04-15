package com.app.repository;

import com.app.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by username.
     * @param username username.
     * @return optional User entity.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if user with given username exists.
     * @param username username.
     * @return true if given user exists or false otherwise.
     */
    boolean existsByUsername(String username);
}
