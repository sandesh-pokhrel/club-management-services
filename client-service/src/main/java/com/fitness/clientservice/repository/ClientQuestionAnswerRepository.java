package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.ClientQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientQuestionAnswerRepository extends JpaRepository<ClientQuestionAnswer, Integer> {

    List<ClientQuestionAnswer> findAllByClientUsername(String username);
}
