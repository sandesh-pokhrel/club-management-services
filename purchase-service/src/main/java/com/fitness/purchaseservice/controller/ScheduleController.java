package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAllSchedules() {
        return this.scheduleService.getAllSchedules();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Schedule saveSchedule(@RequestBody Schedule schedule, @RequestParam String mode) {
        return this.scheduleService.saveSchedule(schedule, mode);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSchedule(@PathVariable Integer id) {
        this.scheduleService.deleteSchedule(id);
    }
}
