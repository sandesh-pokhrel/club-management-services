package com.fitness.purchaseservice.util;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.repository.ScheduleRepository;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.util.GeneralUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
@AllArgsConstructor
public class ScheduleRecurrenceUtil extends GeneralUtil {

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

    public Long getScheduledAppointments(Schedule schedule) {
        Long totalNonSeriesAppts = this.scheduleRepository
                .countByClientUsernameAndPurchaseSubCategoryAndRecurrenceRuleIsNull(schedule.getClientUsername(), schedule.getPurchaseSubCategory());
        List<Schedule> schedulesWithNonNullRecurrence = this.scheduleRepository
                .findAllByClientUsernameAndPurchaseSubCategoryAndRecurrenceRuleIsNotNull(schedule.getClientUsername(), schedule.getPurchaseSubCategory());
        Long totalAppts = totalNonSeriesAppts;
        for (Schedule scheduleWithNonNullRecurrence : schedulesWithNonNullRecurrence) {
            String rule = scheduleWithNonNullRecurrence.getRecurrenceRule();
            totalAppts += getCountFromRule(rule,
                    ruleMap -> (Objects.isNull(ruleMap) || !ruleMap.containsKey("count") || !NumberUtils.isCreatable(ruleMap.get("count"))));
        }
        return totalAppts;
    }

    public String recurrenceRuleWithCount(Schedule schedule) {
        Long totalAppts = getTotalAppointementsforSchedule(schedule);
        Long appointmentsScheduledTimes = getScheduledAppointments(schedule);
        long remainingAppointments = totalAppts - appointmentsScheduledTimes;
        if (remainingAppointments <= 0)
            throw new BadRequestException("Max (" + totalAppts + ") number of appointment exceeded!");
        long countInput;
        String recurrenceRule = schedule.getRecurrenceRule();
        if (recurrenceRule.contains("COUNT=")) {
            countInput = getCountFromRule(recurrenceRule,
                    ruleMap -> (Objects.isNull(ruleMap) || !ruleMap.containsKey("count") || !NumberUtils.isCreatable(ruleMap.get("count"))));
            if (countInput <= remainingAppointments)
                return recurrenceRule;
            else
                return recurrenceRule.substring(0,recurrenceRule.indexOf("COUNT="))
                        + "COUNT=" + remainingAppointments + ";";
        } else {
            return recurrenceRule + ("COUNT=" + remainingAppointments + ";");
        }
    }

    // This is not being used
    /*private List<Schedule> createSchedulesOfDaily(Map<String, String> ruleMap, Schedule schedule) {
        Long totalAppts = getTotalAppointementsforSchedule(schedule);
        Long appointmentsScheduledTimes = getScheduledAppointments(schedule);
        int interval = Integer.parseInt(ruleMap.get("interval"));
        String seriesIdentifier = getSerialNumber();
        // First schedule
        List<Schedule> schedules = new ArrayList<>();
        schedule.setSeriesIdentifier(seriesIdentifier);
        schedules.add(schedule);
        for (int i = 1; i < (totalAppts - appointmentsScheduledTimes); i++) {
            Schedule prevSchedule = schedules.get(i - 1);
            Schedule newSchedule = Schedule.scheduleFrom(prevSchedule);
            Date startTime = prevSchedule.getStartTime();
            Date endTime = prevSchedule.getEndTime();
            newSchedule.setStartTime(DateUtils.addDays(startTime, interval));
            newSchedule.setEndTime(DateUtils.addDays(endTime, interval));
            schedules.add(newSchedule);
        }

        return schedules;
    }*/

    // This is not being used
    /*public List<Schedule> parseRecurrenceRule(Schedule schedule) {
        String recurrenceRule = schedule.getRecurrenceRule();
        Map<String, String> ruleMap = tokenizeRuleIntoMap(recurrenceRule);
        if (Objects.isNull(ruleMap) || ruleMap.isEmpty()) return null;
        if (ruleMap.get("freq").equals("daily")) {
            return createSchedulesOfDaily(ruleMap, schedule);
        }
        return null;
    }*/
}
