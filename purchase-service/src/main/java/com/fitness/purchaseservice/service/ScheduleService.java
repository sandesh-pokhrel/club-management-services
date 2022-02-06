package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.model.ScheduleEditMode;
import com.fitness.purchaseservice.repository.ClientPurchaseRepository;
import com.fitness.purchaseservice.repository.ScheduleRepository;
import com.fitness.purchaseservice.util.ScheduleRecurrenceUtil;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
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
        schedule.setIsReadOnly(schedule.getStatus().equalsIgnoreCase("completed"));
        if (schedule.getStatus().equalsIgnoreCase("completed")) {
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

    public List<Schedule> getAllSchedules() {
        return this.scheduleRepository.findAll();
    }

    public Schedule saveSchedule(Schedule schedule, String mode, boolean isRecurrent) {
//        if (mode.equalsIgnoreCase("EDIT"))
//            schedule.setPurchaseSubCategory(this.scheduleRepository.findById(schedule.getId()).get().getPurchaseSubCategory());
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
                    if (schedule.getStatus().equalsIgnoreCase("completed"))
                        throw new BadRequestException("Cannot mark whole appointment series as completed");
                    // Schedule to edit is already a recurrence schedule
                    if (Objects.nonNull(scheduleFromDb.getRecurrenceRule())) {
                        List<Schedule> editedSchedules = checkForReadOnlyWhileEntireSeriesModification(schedule.getId());
                        if (editedSchedules.size() > 0)
                            this.scheduleRepository.deleteAllInBatch(editedSchedules);
                        schedule.setIsReadOnly(false);
                        schedule.setRecurrenceException(null);
                        schedule.setDeletedCount(0);
                        schedule.setRecurrenceId(null);
                    }
                } else {
                    String recurrenceException = schedule.getRecurrenceException();
                    String recurrenceExceptionDb = scheduleFromDb.getRecurrenceException();
                    if (Objects.nonNull(recurrenceExceptionDb) && !recurrenceExceptionDb.isEmpty()) {
                        AtomicBoolean recurrenceExists = new AtomicBoolean(false);
                        Stream.of(recurrenceExceptionDb.split(",")).forEach(rException -> {
                            if (rException.trim().equals(recurrenceException))
                                recurrenceExists.set(true);
                        });
                        if (!recurrenceExists.get())
                            scheduleFromDb.setRecurrenceException(recurrenceExceptionDb + "," + recurrenceException);
                    } else scheduleFromDb.setRecurrenceException(recurrenceException);
                    Schedule editedScheduleInDb = this.scheduleRepository
                            .findByRecurrenceExceptionAndRecurrenceId(recurrenceException, schedule.getRecurrenceId()).orElse(null);
                    setReadOnlyBehaviorOnEdit(schedule, clientPurchase);
                    if (Objects.isNull(editedScheduleInDb)) schedule.setId(null);
                    else schedule.setId(editedScheduleInDb.getId());
                    this.scheduleRepository.save(scheduleFromDb);
                }
            } else {
                setReadOnlyBehaviorOnEdit(schedule, clientPurchase);
            }
        }
        return this.scheduleRepository.save(schedule);
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
            schedule.setRecurrenceException(recurrenceExceptionDb + "," + recurrenceException);
            Integer deletedCount = schedule.getDeletedCount();
            if (Objects.isNull(deletedCount))
                deletedCount = 0;
            schedule.setDeletedCount(deletedCount + 1);
        } else {
            schedule.setRecurrenceException(recurrenceException);
            schedule.setDeletedCount(1);
        }
        this.scheduleRepository.save(schedule);
    }
}
