package com.fitness.authservice.repository;

import com.fitness.authservice.model.TrainerWorkingHour;
import com.fitness.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerWorkingHourRepository extends JpaRepository<TrainerWorkingHour, Integer> {

    List<TrainerWorkingHour> findAllByUser(User user);
}
