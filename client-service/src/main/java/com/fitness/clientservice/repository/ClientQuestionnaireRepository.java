package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.ClientQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientQuestionnaireRepository extends JpaRepository<ClientQuestionnaire, Integer> {

    List<ClientQuestionnaire> findAllByClientUsername(String username);
}
