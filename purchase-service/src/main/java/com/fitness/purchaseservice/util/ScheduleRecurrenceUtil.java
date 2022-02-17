package com.fitness.purchaseservice.util;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.model.ScheduleEditMode;
import com.fitness.purchaseservice.repository.ScheduleRepository;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.util.GeneralUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@Component
@AllArgsConstructor
public class ScheduleRecurrenceUtil extends GeneralUtil {

    private static final Predicate<Map<String, String>> RULE_EVALUATION =
            ruleMap -> (Objects.isNull(ruleMap) || !ruleMap.containsKey("count") || !NumberUtils.isCreatable(ruleMap.get("count")));

    private final ScheduleRepository scheduleRepository;
    private final ClientPurchaseService clientPurchaseService;

    private Map<String, String> tokenizeRuleIntoMap(String recurrenceRule) {
        if (Objects.isNull(recurrenceRule) || recurrenceRule.length() < 1) return null;
        String[] ruleContents = recurrenceRule.split(";");
        if (ruleContents.length < 1) return null;
        Map<String, String> ruleMap = new HashMap<>();
        for (String ruleContent : ruleContents) {
            String[] pairSplit = ruleContent.split("=");
            if (pairSplit.length > 1) {
                ruleMap.put(pairSplit[0].toLowerCase(), pairSplit[1].toLowerCase());
            }
        }
        return ruleMap;
    }

    private Long getCountFromRule(String rule, Predicate<Map<String, String>> evaluate) {
        Map<String, String> ruleMap = tokenizeRuleIntoMap(rule);
        if (evaluate.test(ruleMap))
            throw new BadRequestException("Invalid recurrence rule found!");
        return Long.parseLong(ruleMap.get("count"));
    }

    private Long getTotalAppointementsforSchedule(Schedule schedule) {
        ClientPurchase clientPurchase = clientPurchaseService.getAllActivePurchasesForClientByPurchaseSubCategory(schedule);
        if (Objects.isNull(clientPurchase))
            throw new NotFoundException("No purchase found. Unable to set the schedule!");
        return Long.valueOf(clientPurchase.getAppts());
    }

    public Long getScheduledAppointmentsForSchedules(List<Schedule> schedules) {
        long totalNonSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.isNull(schedule.getRecurrenceRule())).count();
        AtomicLong totalRecurrent = new AtomicLong(0);
        schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) && Objects.isNull(schedule.getRecurrenceId()))
                .forEach(schedule -> {
                    totalRecurrent.set(totalRecurrent.get() + getCountFromRule(schedule.getRecurrenceRule(), RULE_EVALUATION));
                    if (Objects.nonNull(schedule.getDeletedCount()))
                        totalRecurrent.set(totalRecurrent.get() - schedule.getDeletedCount());
                });
        return totalNonSeriesAppts + totalRecurrent.get();
    }

    public Long getCompletedAppointmentsForSchedules(List<Schedule> schedules) {
        long totalCompletedNonSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.isNull(schedule.getRecurrenceRule()) && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly())).count();
        long totalCompletedSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) &&
                        Objects.nonNull(schedule.getRecurrenceId()) && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly())).count();
        return totalCompletedNonSeriesAppts + totalCompletedSeriesAppts;
    }

    public Long getScheduledAppointments(Schedule schedule, ScheduleEditMode scheduleEditMode) {
        Long totalNonSeriesAppts = this.scheduleRepository
                .countByClientUsernameAndPurchaseSubCategoryAndRecurrenceRuleIsNull(schedule.getClientUsername(), schedule.getPurchaseSubCategory());
        List<Schedule> schedulesWithNonNullRecurrence = this.scheduleRepository
                .findAllByClientUsernameAndPurchaseSubCategoryAndRecurrenceRuleIsNotNullAndRecurrenceIdIsNull(schedule.getClientUsername(), schedule.getPurchaseSubCategory());
        Long totalAppts = totalNonSeriesAppts;
        for (Schedule scheduleWithNonNullRecurrence : schedulesWithNonNullRecurrence) {
            Integer deletedCount = scheduleWithNonNullRecurrence.getDeletedCount();
            String rule = scheduleWithNonNullRecurrence.getRecurrenceRule();
            totalAppts += getCountFromRule(rule, ScheduleRecurrenceUtil.RULE_EVALUATION);
            if (Objects.nonNull(deletedCount) && deletedCount > 0)
                totalAppts -= deletedCount;
        }
        if (scheduleEditMode == ScheduleEditMode.SINGLE_TO_SERIES)
            --totalAppts;
        else if (scheduleEditMode == ScheduleEditMode.SERIES_TO_SERIES) {
            Schedule scheduleFromDb = this.scheduleRepository.getById(schedule.getId());
            Long countFromRule = this.getCountFromRule(scheduleFromDb.getRecurrenceRule(),
                    ScheduleRecurrenceUtil.RULE_EVALUATION);
            totalAppts -= countFromRule;
            totalAppts += Objects.isNull(scheduleFromDb.getDeletedCount()) ? 0 : scheduleFromDb.getDeletedCount();
        }

        return totalAppts;
    }

    public String recurrenceRuleWithCount(Schedule schedule, ScheduleEditMode scheduleEditMode) {
        Long totalAppts = getTotalAppointementsforSchedule(schedule);
        Long appointmentsScheduledTimes = getScheduledAppointments(schedule, scheduleEditMode);
        long remainingAppointments = totalAppts - appointmentsScheduledTimes;
        if (remainingAppointments <= 0)
            throw new BadRequestException("Max (" + totalAppts + ") number of appointment exceeded!");
        long countInput;
        String recurrenceRule = schedule.getRecurrenceRule();
        if (recurrenceRule.contains("COUNT=")) {
            countInput = getCountFromRule(recurrenceRule, ScheduleRecurrenceUtil.RULE_EVALUATION);
            if (countInput <= remainingAppointments)
                return recurrenceRule;
            else
                return recurrenceRule.substring(0, recurrenceRule.indexOf("COUNT="))
                        + "COUNT=" + remainingAppointments + ";";
        } else {
            return recurrenceRule + ("COUNT=" + remainingAppointments + ";");
        }
    }
}
