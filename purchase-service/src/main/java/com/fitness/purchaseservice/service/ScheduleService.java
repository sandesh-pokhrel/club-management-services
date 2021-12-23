package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.repository.ClientPurchaseRepository;
import com.fitness.purchaseservice.repository.ScheduleRepository;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClientPurchaseRepository clientPurchaseRepository;

    public List<Schedule> getAllSchedules() {
        return this.scheduleRepository.findAll();
    }

    public Schedule saveSchedule(Schedule schedule, String mode) {
        if (mode.equalsIgnoreCase("EDIT"))
            schedule.setPurchaseSubCategory(this.scheduleRepository.findById(schedule.getId()).get().getPurchaseSubCategory());
        Long appointmentsScheduledTimes = this.scheduleRepository
                .countByClientUsernameAndPurchaseSubCategory(schedule.getClientUsername(), schedule.getPurchaseSubCategory());
        ClientPurchase clientPurchase = this.clientPurchaseRepository
                .findByClientUsernameAndPurchaseSubCategoryAndApptScheduledNot(schedule.getClientUsername(), schedule.getPurchaseSubCategory(), -1);
        if (mode.equalsIgnoreCase("CREATE")) {
            if (Objects.isNull(clientPurchase)) throw new NotFoundException("No active purchase found!");
            if (appointmentsScheduledTimes >= clientPurchase.getAppts())
                throw new BadRequestException("Max (" + clientPurchase.getAppts() + ") number of appointment exceeded!");
        } else {
            schedule.setIsReadOnly(schedule.getStatus().equalsIgnoreCase("completed"));
            if (schedule.getStatus().equalsIgnoreCase("completed")) {
                Long totalCompletedSchedule = this.scheduleRepository.
                        countByClientUsernameAndPurchaseSubCategoryAndStatusIgnoreCase(schedule.getClientUsername(), schedule.getPurchaseSubCategory(), "COMPLETED");
                if (++totalCompletedSchedule == clientPurchase.getAppts().longValue())
                    this.clientPurchaseRepository.updateApptScheduledToCompleted(schedule.getClientUsername(), schedule.getPurchaseSubCategory());
            }
        }
        return this.scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Integer id) {
        if (!this.scheduleRepository.existsById(id))
            throw new NotFoundException("Schedule does not exist!");
        this.scheduleRepository.deleteById(id);
    }
}
