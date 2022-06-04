package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.Questionnaire;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Integer> {
    List<Questionnaire> findAllByEnabled(boolean enabled, Sort sort);

    List<Questionnaire> findAllBySortOrder(Integer sortOrder);

    List<Questionnaire> findAllBySortOrderGreaterThanEqual(Integer sortOrder);

    @Query("select max(q.sortOrder) from Questionnaire q")
    Integer findMaxSortValue();
}
