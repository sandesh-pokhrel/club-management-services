package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.ClientGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientGoalRepository extends JpaRepository<ClientGoal, Integer> {
}
