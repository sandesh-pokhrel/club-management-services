package com.fitness.authservice.repository;

import com.fitness.authservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUsernameOrEmail(String username, String email);

    @Query("select u.username from User u where u.clubId = ?1")
    List<String> getOnlyUsernames(Integer clubId);

    Page<User> findAllByClubId(Integer clubId, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE u.clubId = :clubId AND (lower(u.username) like %:searchText% OR lower(u.firstName) like %:searchText% OR " +
            "lower(u.lastName) like %:searchText% OR lower(u.email) like %:searchText%)")
    Page<User> search(String searchText, Integer clubId, Pageable pageable);
}
