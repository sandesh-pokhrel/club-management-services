package com.fitness.authservice.service;

import com.fitness.authservice.model.TrainerWorkingHour;
import com.fitness.authservice.model.User;
import com.fitness.authservice.repository.TrainerWorkingHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerWorkingHourService {

    private final TrainerWorkingHourRepository trainerWorkingHourRepository;

    public TrainerWorkingHour save(TrainerWorkingHour trainerWorkingHour) {
        return this.trainerWorkingHourRepository.save(trainerWorkingHour);
    }

    public List<TrainerWorkingHour> getByUser(User user) {
        return this.trainerWorkingHourRepository.findAllByUser(user);
    }
}
