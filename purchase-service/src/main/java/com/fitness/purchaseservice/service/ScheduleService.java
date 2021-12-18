package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.repository.ScheduleRepository;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<Schedule> getAllSchedules() {
        return this.scheduleRepository.findAll();
    }

    public Schedule saveSchedule(Schedule schedule) {
        schedule.setTrainerUsername("sandesh");
        schedule.setClientUsername("sandesh");
        return this.scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Integer id) {
        if (!this.scheduleRepository.existsById(id))
            throw new NotFoundException("Schedule does not exist!");
        this.scheduleRepository.deleteById(id);
    }
}
