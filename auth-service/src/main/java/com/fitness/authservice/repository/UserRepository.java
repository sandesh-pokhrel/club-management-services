package com.fitness.authservice.repository;

import com.fitness.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUsernameOrEmail(String username, String email);
}
