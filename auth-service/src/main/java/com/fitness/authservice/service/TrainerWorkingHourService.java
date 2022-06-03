package com.fitness.authservice.service;

import com.fitness.authservice.model.TrainerWorkingHour;
import com.fitness.authservice.model.User;
import com.fitness.authservice.repository.TrainerWorkingHourRepository;
import com.fitness.sharedapp.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerWorkingHourService {

    private final TrainerWorkingHourRepository trainerWorkingHourRepository;

    public TrainerWorkingHour getById(Integer id) {
        return this.trainerWorkingHourRepository.findById(id).orElseThrow(() -> new BadRequestException("Invalid id!"));
    }

    public TrainerWorkingHour save(TrainerWorkingHour trainerWorkingHour) {
        return this.trainerWorkingHourRepository.save(trainerWorkingHour);
    }

    public List<TrainerWorkingHour> getByUser(User user) {
        return this.trainerWorkingHourRepository.findAllByUserAndDeletedIsFalse(user);
    }

    public void deleteById(Integer id) {
        this.trainerWorkingHourRepository.deleteById(id);
    }
}
