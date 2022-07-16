package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.feign.TrainerWorkingHourFeignClient;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.model.ScheduleEditMode;
import com.fitness.purchaseservice.model.TrainerWorkingHour;
import com.fitness.purchaseservice.service.ScheduleService;
import com.fitness.purchaseservice.util.ScheduleRecurrenceUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleRecurrenceUtil scheduleRecurrenceUtil;
    private final TrainerWorkingHourFeignClient trainerWorkingHourFeignClient;

    private static final List<String> WEEK_DAYS = Arrays.asList("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT");

    private void calculateBlockedSchedules(List<Schedule> schedules, String trainer) {
        List<TrainerWorkingHour> trainerWorkingHours = new ArrayList<>();
        if (Objects.nonNull(trainer) && !trainer.isEmpty())
            trainerWorkingHours = this.trainerWorkingHourFeignClient.getTrainerWorkingHour(trainer);
        AtomicReference<LocalTime> localTimeAtomicReference = new AtomicReference<>(null);
        List<TrainerWorkingHour> finalTrainerWorkingHours = trainerWorkingHours;
        WEEK_DAYS.forEach(weekDay -> {
            List<Schedule> blockUpSchedules = finalTrainerWorkingHours.stream()
                    .filter(t -> t.getDay().equalsIgnoreCase(weekDay))
                    .sorted(Comparator.comparing(f -> LocalTime.parse(f.getStartHour())))
                    .map(t -> {
                        if (Objects.isNull(localTimeAtomicReference.get())) {
                            localTimeAtomicReference.set(LocalTime.of(LocalTime.parse(t.getEndHour()).getHour(),
                                    LocalTime.parse(t.getEndHour()).getMinute()));
                            return Schedule.builder()
                                    .isBlock(true)
                                    .startTime(new Date(100, Calendar.JANUARY, 1, 2, 0))
                                    .endTime(new Date(100, Calendar.JANUARY, 1, Integer.parseInt(t.getStartHour().split(":")[0]),
                                            Integer.parseInt(t.getStartHour().split(":")[1])))
                                    .recurrenceRule("FREQ=WEEKLY;BYDAY=" + t.getDay().substring(0, 2) + ";INTERVAL=1")
                                    .build();
                        } else {
                            LocalTime localTime = localTimeAtomicReference.get();
                            Schedule schedule = Schedule.builder()
                                    .isBlock(true)
                                    .startTime(new Date(100, Calendar.JANUARY, 1, localTime.getHour(), localTime.getMinute()))
                                    .endTime(new Date(100, Calendar.JANUARY, 1, Integer.parseInt(t.getStartHour().split(":")[0]),
                                            Integer.parseInt(t.getStartHour().split(":")[1])))
                                    .recurrenceRule("FREQ=WEEKLY;BYDAY=" + t.getDay().substring(0, 2) + ";INTERVAL=1")
                                    .build();
                            localTimeAtomicReference.set(LocalTime.of(LocalTime.parse(t.getEndHour()).getHour(),
                                    LocalTime.parse(t.getEndHour()).getMinute()));
                            return schedule;
                        }
                    }).collect(Collectors.toList());

            if (Objects.nonNull(localTimeAtomicReference.get())) {
                LocalTime localTime = localTimeAtomicReference.get();
                schedules.add(Schedule.builder()
                        .isBlock(true)
                        .endTime(new Date(100, Calendar.JANUARY, 1, 22, 0))
                        .startTime(new Date(100, Calendar.JANUARY, 1, localTime.getHour(), localTime.getMinute()))
                        .recurrenceRule("FREQ=WEEKLY;BYDAY=" + weekDay.substring(0, 2) + ";INTERVAL=1")
                        .build());
            }
            schedules.addAll(blockUpSchedules);
            localTimeAtomicReference.set(null);
        });
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> getAllSchedules(@RequestParam("club-id") Integer clubId,
                                          @RequestParam("clients") List<String> clients,
                                          @RequestParam("trainers") List<String> trainers,
                                          @RequestParam("trainer") String trainer) {
        List<Schedule> schedules = this.scheduleService.getAllSchedulesByClientsAndTrainers(clubId, clients, trainers);
        calculateBlockedSchedules(schedules, trainer);
        return schedules;
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

    @PatchMapping("/new-status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public void updateSchedulesStatus(@RequestBody List<Schedule> schedules,
                                      @PathVariable String status) {
        this.scheduleService.updateScheduleStatus(schedules, status);
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
