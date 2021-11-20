package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.ClientExtraInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ClientExtraInfoRepository extends JpaRepository<ClientExtraInfo, String> {

    boolean existsByClientUsernameAndQuestionnaireInitiated(String username, boolean check);
    boolean existsByQuestionnaireSerial(String serial);

    Optional<ClientExtraInfo> findByQuestionnaireSerial(String serial);

    @Modifying
    @Transactional
    @Query("update ClientExtraInfo c set c.questionnaireSerial = null where c.clientUsername = ?1")
    void nullifySerialForUsername(String username);
}
