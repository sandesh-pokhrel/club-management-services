package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.model.ScheduleEditMode;
import com.fitness.purchaseservice.service.ScheduleService;
import com.fitness.purchaseservice.util.ScheduleRecurrenceUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleRecurrenceUtil scheduleRecurrenceUtil;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAllSchedules(@RequestParam("club-id") Integer clubId,
                                          @RequestParam("clients") List<String> clients,
                                          @RequestParam("trainers") List<String> trainers) {
        return this.scheduleService.getAllSchedulesByClientsAndTrainers(clubId, clients, trainers);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Schedule saveSchedule(@RequestBody Schedule schedule, @RequestParam String mode) {
        if (Objects.nonNull(schedule.getRecurrenceRule()) && Objects.isNull(schedule.getRecurrenceId())) {
            ScheduleEditMode scheduleEditMode;
            if (mode.equals("EDIT")) {
                Schedule scheduleFromDb = this.scheduleService.getScheduleById(schedule.getId());
                if (Objects.nonNull(scheduleFromDb.getRecurrenceRule()))
                    scheduleEditMode = ScheduleEditMode.SERIES_TO_SERIES;
                else
                    scheduleEditMode = ScheduleEditMode.SINGLE_TO_SERIES;
            } else scheduleEditMode = ScheduleEditMode.NORMAL;
            String recurrenceRuleWithCount = this.scheduleRecurrenceUtil.recurrenceRuleWithCount(schedule, scheduleEditMode);
            schedule.setRecurrenceRule(recurrenceRuleWithCount);
        }
        return this.scheduleService.saveSchedule(schedule, mode, Objects.nonNull(schedule.getRecurrenceRule()));
    }

    @GetMapping("/recurrence-delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void saveRecurrenceExceptionForDelete(@PathVariable Integer id,
                                                 @RequestParam("recurrence-exception") String recurrenceException) {
        this.scheduleService.saveRecurrenceExceptionForDelete(id, recurrenceException);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSchedule(@PathVariable Integer id) {
        this.scheduleService.deleteSchedule(id);
    }

    @GetMapping("/appointment-stat/{purchaseId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Long> getScheduleStatForPurchase(@PathVariable Integer purchaseId) {
        return this.scheduleService.getTotalScheduledAndCompleted(purchaseId);
    }

    @GetMapping("/agenda/purchase/{purchaseId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAgendaSchedulesByPurchase(@PathVariable Integer purchaseId) {
        return this.scheduleService.getAgendaSchedulesByPurchase(purchaseId);
    }

    @GetMapping("/agenda/client/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAgendaSchedulesByClient(@PathVariable String username) {
        return this.scheduleService.getAgendaSchedulesByClient(username);
    }

    @GetMapping("/agenda/trainer/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAgendaSchedulesByTrainer(@PathVariable String username) {
        return this.scheduleService.getAgendaSchedulesByTrainer(username);
    }

    @GetMapping("/agenda/today")
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAgendaSchedulesByToday() {
        return this.scheduleService.getAgendaSchedulesByToday();
    }
}
