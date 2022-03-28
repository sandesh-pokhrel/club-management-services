package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    Page<Client> findAllByClub(Club club, Pageable pageable);


    boolean existsByEmail(String email);
    boolean existsByEmailAndUsernameNot(String email, String username);

    boolean existsByCellPhone(String cellPhone);
    boolean existsByCellPhoneAndUsernameNot(String cellPhone, String username);

    @Query("select concat(concat(concat(concat(concat(c.firstName, ' '), c.lastName), '  ('), c.username), ')')  " +
            "from Client c where c.club = :club order by c.firstName, c.lastName")
    List<String> getAllClientUsernames(Club club);

    @Query("select concat(concat(concat(concat(concat(c.firstName, ' '), c.lastName), '  ('), c.username), ')')  " +
            "from Client c where c.username = ?1 order by c.firstName, c.lastName")
    String getClientUsernameConcatFullNameByUsername(String username);

    @Query("SELECT c FROM Client c " +
            "WHERE c.club = :club AND (lower(c.username) like %:searchText% OR lower(c.firstName) like %:searchText% OR " +
            "lower(c.lastName) like %:searchText% OR lower(c.email) like %:searchText%)")
    Page<Client> search(String searchText, Club club, Pageable pageable);
}
