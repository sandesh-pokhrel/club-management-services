package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.model.ScheduleEditMode;
import com.fitness.purchaseservice.repository.ClientPurchaseRepository;
import com.fitness.purchaseservice.repository.ScheduleRepository;
import com.fitness.purchaseservice.util.ScheduleRecurrenceUtil;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClientPurchaseRepository clientPurchaseRepository;
    private final ScheduleRecurrenceUtil scheduleRecurrenceUtil;

    private List<Schedule> checkForReadOnlyWhileEntireSeriesModification(Integer scheduleId) {
        List<Schedule> editedSchedules = this.scheduleRepository.findAllByRecurrenceId(scheduleId);
        if (!editedSchedules.isEmpty()) {
            for (Schedule editedSchedule : editedSchedules) {
                if (Objects.nonNull(editedSchedule.getIsReadOnly()) && editedSchedule.getIsReadOnly())
                    throw new BadRequestException("Cannot edit the event series. Some of the appointments are already completed!");
            }
        }
        return editedSchedules;
    }

    private void setReadOnlyBehaviorOnEdit(Schedule schedule, ClientPurchase clientPurchase) {
        if (StringUtils.equalsAnyIgnoreCase(schedule.getStatus(), Constants.READ_ONLY_ALIKE_SCHEDULE_STATUS)) {
            schedule.setIsReadOnly(true);
        }
        if (StringUtils.equalsAnyIgnoreCase(schedule.getStatus(), Constants.COMPLETED_ALIKE_SCHEDULE_STATUS)) {
            Long totalCompletedSchedule = this.scheduleRepository.
                    countByClientUsernameAndPurchaseSubCategoryAndStatusIgnoreCase(schedule.getClientUsername(), schedule.getPurchaseSubCategory(), "COMPLETED");
            if (++totalCompletedSchedule == clientPurchase.getAppts().longValue())
                this.clientPurchaseRepository.updateApptScheduledToCompleted(schedule.getClientUsername(), schedule.getPurchaseSubCategory());
        }
    }

    public Schedule getScheduleById(Integer scheduleId) {
        return this.scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BadRequestException("Schedule not found!"));
    }

    public List<Schedule> getAllSchedules(Integer clubId) {
        return this.scheduleRepository.customFindAllByClubId(clubId);
    }

    public List<Schedule> getAllSchedulesByClientsAndTrainers(Integer clubId, List<String> clients, List<String> trainers) {
        return this.scheduleRepository.customFindAllByClubIdAndClientsAndTrainers(clubId, clients, trainers);
    }

    public Schedule saveSchedule(Schedule schedule, String mode, boolean isRecurrent) {
        ClientPurchase clientPurchase = this.clientPurchaseRepository
                .findByClientUsernameAndPurchaseSubCategoryAndApptScheduledNot(schedule.getClientUsername(), schedule.getPurchaseSubCategory(), -1);
        if (mode.equalsIgnoreCase("CREATE")) {
            if (!isRecurrent) {
                if (Objects.isNull(clientPurchase)) throw new NotFoundException("No active purchase found!");
                Long appointmentsScheduledTimes = this.scheduleRecurrenceUtil.getScheduledAppointments(schedule, ScheduleEditMode.NORMAL);
                if (appointmentsScheduledTimes >= clientPurchase.getAppts())
                    throw new BadRequestException("Max (" + clientPurchase.getAppts() + ") number of appointment exceeded!");
            }
        } else {
            // passed series is the recurrent series
            if (Objects.nonNull(schedule.getRecurrenceRule())) {
                Schedule scheduleFromDb = getScheduleById(Objects.nonNull(schedule.getId()) ? schedule.getId() : schedule.getRecurrenceId());
                // Passed schedule is not an edited instance of a series
                if (Objects.isNull(schedule.getRecurrenceId())) {
                    // Does not allow completing whole series at once
                    if (StringUtils.equalsAnyIgnoreCase(schedule.getStatus(), Constants.READ_ONLY_ALIKE_SCHEDULE_STATUS))
                        throw new BadRequestException("Cannot mark whole appointment series as completed");
                    // Schedule to edit is already a recurrence schedule
                    if (Objects.nonNull(scheduleFromDb.getRecurrenceRule())) {
                        List<Schedule> editedSchedules = checkForReadOnlyWhileEntireSeriesModification(schedule.getId());
                        // if no read only schedules is found in the recurrent series list then delete all
                        if (editedSchedules.size() > 0)
                            this.scheduleRepository.deleteAllInBatch(editedSchedules);

                        // reset all the fields of the recurrent series
                        schedule.setIsReadOnly(false);
                        schedule.setRecurrenceException(null);
                        schedule.setDeletedCount(0);
                        schedule.setRecurrenceId(null);
                    }
                } else {
                    // passed schedule is an edited instance
                    String recurrenceException = schedule.getRecurrenceException();
                    String recurrenceExceptionDb = scheduleFromDb.getRecurrenceException();
                    // checking if the recurrentException already exists in the parent schedule exceptions list
                    if (Objects.nonNull(recurrenceExceptionDb) && !recurrenceExceptionDb.isEmpty()) {
                        AtomicBoolean recurrenceExists = new AtomicBoolean(false);
                        Stream.of(recurrenceExceptionDb.split(",")).forEach(rException -> {
                            if (rException.trim().equals(recurrenceException))
                                recurrenceExists.set(true);
                        });
                        // if recurEx not in parent schedule then append at the end
                        if (!recurrenceExists.get())
                            scheduleFromDb.setRecurrenceException(recurrenceExceptionDb + "," + recurrenceException);
                    } else scheduleFromDb.setRecurrenceException(recurrenceException);
                    Schedule editedScheduleInDb = this.scheduleRepository
                            .findByRecurrenceExceptionAndRecurrenceId(recurrenceException, schedule.getRecurrenceId()).orElse(null);
                    setReadOnlyBehaviorOnEdit(schedule, clientPurchase);
                    // if edited instance is not already stored in the database then set the id to null
                    if (Objects.isNull(editedScheduleInDb)) schedule.setId(null);
                    else schedule.setId(editedScheduleInDb.getId());
                    // for deleted alike schedule status increment deleted count column value
                    if (StringUtils.equalsAnyIgnoreCase(schedule.getStatus(), Constants.DELETED_ALIKE_SCHEDULE_STATUS)) {
                        Integer deletedCount = scheduleFromDb.getDeletedCount();
                        if (Objects.isNull(deletedCount) || deletedCount == 0)
                            scheduleFromDb.setDeletedCount(1);
                        else scheduleFromDb.setDeletedCount(scheduleFromDb.getDeletedCount() + 1);
                    }
                    // save the existing parent schedule
                    this.scheduleRepository.saveAndFlush(scheduleFromDb);
                }
            } else {
                setReadOnlyBehaviorOnEdit(schedule, clientPurchase);
            }
        }
        schedule.setPurchaseId(clientPurchase.getId());
        // save the schedule finally
        return this.scheduleRepository.saveAndFlush(schedule);
    }

    public void deleteSchedule(Integer id) {
        if (!this.scheduleRepository.existsById(id))
            throw new NotFoundException("Schedule does not exist!");
        List<Schedule> editedSchedules = checkForReadOnlyWhileEntireSeriesModification(id);
        if (editedSchedules.size() > 0)
            this.scheduleRepository.deleteAllInBatch(editedSchedules);
        this.scheduleRepository.deleteById(id);
    }

    public void saveRecurrenceExceptionForDelete(Integer id, String recurrenceException) {
        Schedule schedule = this.scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Schedule not found!"));
        if (Objects.isNull(recurrenceException) || recurrenceException.isEmpty())
            throw new BadRequestException("Invalid recurrence exception string.");
        String recurrenceExceptionDb = schedule.getRecurrenceException();
        if (Objects.nonNull(recurrenceExceptionDb) && !recurrenceExceptionDb.isEmpty()) {
            // if recurrence exceptions exists then append deleted schedule recurrence exception at the end
            schedule.setRecurrenceException(recurrenceExceptionDb + "," + recurrenceException);
            Integer deletedCount = schedule.getDeletedCount();
            if (Objects.isNull(deletedCount))
                deletedCount = 0;
            schedule.setDeletedCount(deletedCount + 1);
        } else {
            // if no recurrence exception was found then simply set the exception
            schedule.setRecurrenceException(recurrenceException);
            schedule.setDeletedCount(1);
        }
        this.scheduleRepository.save(schedule);
    }

    public void updateScheduleStatus(List<Schedule> schedules, String status) {
        // for non-recurring schedule
        schedules.stream().filter(schedule -> Objects.isNull(schedule.getRecurrenceRule()))
                .peek(schedule -> schedule.setStatus(status))
                .forEach(schedule -> saveSchedule(schedule, "EDIT", Objects.nonNull(schedule.getRecurrenceRule())));
        // for recurring schedule with recurrence exception
        schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) && Objects.nonNull(schedule.getRecurrenceId()))
                .map(schedule -> this.scheduleRepository.getById(schedule.getId()))
                .peek(schedule -> schedule.setStatus(status))
                .forEach(schedule -> saveSchedule(schedule, "EDIT", Objects.nonNull(schedule.getRecurrenceRule())));
        // for recurring schedule without recurrence exception
        schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) && Objects.isNull(schedule.getRecurrenceId()))
                .map(Schedule::new)
                .peek(schedule -> schedule.setRecurrenceException(this.scheduleRecurrenceUtil.getRecurrenceException(schedule.getStartTime())))
                .peek(schedule -> schedule.setStatus(status))
                .peek(schedule -> schedule.setId(null))
                // parent schedule id was stored in series identifier field, fetching that
                .peek(schedule -> schedule.setRecurrenceId(Integer.parseInt(schedule.getSeriesIdentifier())))
                .peek(schedule -> schedule.setSeriesIdentifier(null))
                .forEach(schedule -> saveSchedule(schedule, "EDIT", Objects.nonNull(schedule.getRecurrenceRule())));
    }

    public Map<String, Long> getTotalScheduledAndCompleted(Integer id) {
        Map<String, Long> apptStatsMap = new HashMap<>();
        List<Schedule> schedules = this.scheduleRepository.findAllByPurchaseId(id);
        Long totalScheduled = this.scheduleRecurrenceUtil.getScheduledAppointmentsForSchedules(schedules);
        Long totalCompleted = this.scheduleRecurrenceUtil.getCompletedAppointmentsForSchedules(schedules);
        Long totalNoCharged = this.scheduleRecurrenceUtil.getNoChargeAppointmentsForSchedules(schedules);
        Long totalCharged = this.scheduleRecurrenceUtil.getChargeAppointmentsForSchedules(schedules);
        Long totalCancelled = this.scheduleRecurrenceUtil.getCancelledAppointmentsForSchedules(schedules);
        apptStatsMap.put("scheduled", totalScheduled);
        apptStatsMap.put("completed", totalCompleted);
        apptStatsMap.put("cancelled", totalCancelled);
        apptStatsMap.put("charged", totalCharged);
        apptStatsMap.put("no_charged", totalNoCharged);
        return apptStatsMap;
    }

    public Map<String, Long> getTotalScheduled(Integer id) {
        Map<String, Long> apptStatsMap = new HashMap<>();
        List<Schedule> schedules = this.scheduleRepository.findAllByPurchaseId(id);
        Long totalScheduled = this.scheduleRecurrenceUtil.getScheduledAppointmentsForSchedules(schedules);
        apptStatsMap.put("scheduled", totalScheduled);
        return apptStatsMap;
    }

    public List<Schedule> getAgendaSchedulesByPurchase(Integer purchaseId) {
        List<Schedule> schedules = this.scheduleRepository.findAllByPurchaseId(purchaseId);
        return this.scheduleRecurrenceUtil.generateAgendaForSchedules(schedules);
    }

    public List<Schedule> getAgendaSchedulesByClient(String username) {
        List<Schedule> schedules = this.scheduleRepository.findAllByClientUsername(username);
        return this.scheduleRecurrenceUtil.generateAgendaForSchedules(schedules);
    }

    public List<Schedule> getAgendaSchedulesByTrainer(String username) {
        List<Schedule> schedules = this.scheduleRepository.findAllByTrainerUsername(username);
        return this.scheduleRecurrenceUtil.generateAgendaForSchedules(schedules);
    }

    public List<Schedule> getAgendaSchedulesByToday() {
        List<Schedule> finalSchedules = new ArrayList<>();
        List<Schedule> todayNonRecurringSchedules = this.scheduleRepository.findAllByTodayNonRecurring();
        List<Schedule> todayRecurringSchedules = this.scheduleRepository.findAllRecurringByTodayDate();
        finalSchedules.addAll(this.scheduleRecurrenceUtil.generateAgendaForSchedules(todayNonRecurringSchedules));
        finalSchedules.addAll(this.scheduleRecurrenceUtil.generateAgendaForSchedules(todayRecurringSchedules));
        finalSchedules = finalSchedules.stream()
                .filter(schedule -> DateUtils.isSameDay(schedule.getStartTime(), new Date()))
                .collect(Collectors.toList());
        return finalSchedules;
    }
}
