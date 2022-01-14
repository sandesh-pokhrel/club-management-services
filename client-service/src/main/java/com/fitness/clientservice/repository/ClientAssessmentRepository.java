package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.ClientAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientAssessmentRepository extends JpaRepository<ClientAssessment, Integer> {

    List<ClientAssessment> findAllByClient(Client client);
}
