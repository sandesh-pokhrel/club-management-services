package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.ClientQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ClientQuestionRepository extends JpaRepository<ClientQuestion, String> {

    boolean existsByClientUsernameAndQuestionnaireInitiated(String username, boolean check);
    boolean existsByQuestionnaireSerial(String serial);

    Optional<ClientQuestion> findByQuestionnaireSerial(String serial);

    @Modifying
    @Transactional
//    @Query("update ClientExtraInfo c set c.questionnaireSerial = null where c.clientUsername = ?1")
    @Query("update ClientQuestion c set c.questionnaireInitiated = false where c.clientUsername = ?1")
    void nullifySerialForUsername(String username);
}
