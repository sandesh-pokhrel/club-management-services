package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Integer> {
    List<Questionnaire> findAllByEnabled(boolean enabled);
}
