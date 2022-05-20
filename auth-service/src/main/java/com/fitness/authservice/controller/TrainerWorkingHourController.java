package com.fitness.authservice.controller;

import com.fitness.authservice.model.TrainerWorkingHour;
import com.fitness.authservice.model.User;
import com.fitness.authservice.service.TrainerWorkingHourService;
import com.fitness.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer-working-hours")
@RequiredArgsConstructor
public class TrainerWorkingHourController {

    private final TrainerWorkingHourService trainerWorkingHourService;
    private final UserService userService;

    @PostMapping("/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public TrainerWorkingHour saveTrainerWorkingHour(@RequestBody TrainerWorkingHour trainerWorkingHour,
                                                     @PathVariable String username) {
        User user = this.userService.getByUsername(username);
        trainerWorkingHour.setUser(user);
        return this.trainerWorkingHourService.save(trainerWorkingHour);
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerWorkingHour> getTrainerWorkingHourByUser(@PathVariable String username) {
        User user = this.userService.getByUsername(username);
        return this.trainerWorkingHourService.getByUser(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrainingWorkingHour(@PathVariable Integer id) {
        this.trainerWorkingHourService.deleteById(id);
    }
}
