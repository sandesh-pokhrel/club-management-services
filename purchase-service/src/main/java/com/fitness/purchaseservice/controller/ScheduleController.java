package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.service.ScheduleService;
import com.fitness.purchaseservice.util.ScheduleRecurrenceUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleRecurrenceUtil scheduleRecurrenceUtil;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAllSchedules() {
        return this.scheduleService.getAllSchedules();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Schedule saveSchedule(@RequestBody Schedule schedule, @RequestParam String mode) {
//        if (Objects.isNull(schedule.getRecurrenceRule())) {
//            return Collections.singletonList(this.scheduleService.saveSchedule(schedule, mode));
//        } else {
//            List<Schedule> schedules = this.scheduleRecurrenceUtil.parseRecurrenceRule(schedule);
//            List<Schedule> finalSchedules = new ArrayList<>();
//            schedules.forEach(sch -> finalSchedules.add(this.scheduleService.saveSchedule(sch, mode)));
//            return finalSchedules;
//        }
//        if (!Objects.isNull(schedule.getRecurrenceRule()))
//            schedule.setRecurrenceRule(this.scheduleRecurrenceUtil.recurrenceRuleWithCount(schedule));
//        return this.scheduleService.saveSchedule(schedule, mode);
        if (Objects.nonNull(schedule.getRecurrenceRule())) {
            String recurrenceRuleWithCount = this.scheduleRecurrenceUtil.recurrenceRuleWithCount(schedule);
            schedule.setRecurrenceRule(recurrenceRuleWithCount);
        }
        return this.scheduleService.saveSchedule(schedule, mode, Objects.nonNull(schedule.getRecurrenceRule()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSchedule(@PathVariable Integer id) {
        this.scheduleService.deleteSchedule(id);
    }
}
