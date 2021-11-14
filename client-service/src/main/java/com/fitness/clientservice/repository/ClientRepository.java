package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    boolean existsByEmail(String email);
    boolean existsByEmailAndUsernameNot(String email, String username);

    boolean existsByCellPhone(String cellPhone);
    boolean existsByCellPhoneAndUsernameNot(String cellPhone, String username);

    @Query("SELECT c FROM Client c " +
            "WHERE lower(c.username) like %:searchText% OR lower(c.firstName) like %:searchText% OR " +
            "lower(c.lastName) like %:searchText% OR lower(c.email) like %:searchText%")
    Page<Client> search(String searchText, Pageable pageable);
}
