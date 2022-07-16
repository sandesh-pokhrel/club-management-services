package com.fitness.purchaseservice.util;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.model.ScheduleEditMode;
import com.fitness.purchaseservice.repository.ScheduleRepository;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.util.GeneralUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ScheduleRecurrenceUtil extends GeneralUtil {

    public static final Map<String, Integer> WEEKLY_SCHEDULE_POINT = new HashMap<>();

    static {
        WEEKLY_SCHEDULE_POINT.put("su", 1);
        WEEKLY_SCHEDULE_POINT.put("mo", 2);
        WEEKLY_SCHEDULE_POINT.put("tu", 3);
        WEEKLY_SCHEDULE_POINT.put("we", 4);
        WEEKLY_SCHEDULE_POINT.put("th", 5);
        WEEKLY_SCHEDULE_POINT.put("fr", 6);
        WEEKLY_SCHEDULE_POINT.put("sa", 7);
    }

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

    private int getIntervalFromRule(String rule) {
        Map<String, String> ruleMap = tokenizeRuleIntoMap(rule);
        // interval is stored with key 'interval'
        return Integer.parseInt(ruleMap.get("interval"));
    }

    private String getDaysForWeeklyFromRule(String rule) {
        Map<String, String> ruleMap = tokenizeRuleIntoMap(rule);
        // contains the value like SU, MO, TU etc. values coming from ejs scheduler
        return ruleMap.get("byday");
    }

    private String getWeekDayName(Date startDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDateTime);
        int weekDayNumber = calendar.get(Calendar.DAY_OF_WEEK);
        for (Map.Entry<String, Integer> entry : WEEKLY_SCHEDULE_POINT.entrySet()) {
            if (entry.getValue() == weekDayNumber) {
                // returns the key like su, mo matching the value from map to the start date time
                return entry.getKey();
            }
        }
        return null;
    }


    private int getInitialIncrementForWeeklySchedule(List<String> byDays, Date startDateTime, int interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDateTime);
        int weekDayNumber = calendar.get(Calendar.DAY_OF_WEEK);
        String weekDayName = null;
        for (Map.Entry<String, Integer> entry : WEEKLY_SCHEDULE_POINT.entrySet()) {
            if (entry.getValue() == weekDayNumber) {
                weekDayName = entry.getKey();
                break;
            }
        }
        if (Objects.nonNull(weekDayName) && byDays.contains(weekDayName)) return 0;
        Integer temp = null;
        for (String byDay : byDays) {
            int currentWeekNumber = WEEKLY_SCHEDULE_POINT.get(byDay);
            if (Objects.isNull(temp)) temp = currentWeekNumber;
            if (weekDayNumber - currentWeekNumber < 0) return currentWeekNumber - weekDayNumber;
        }
        if (temp == null)
            throw new RuntimeException("Weekly schedule initial increment fetch failed!");
        // eg: for wednesday to thursday = (7-4)+(7*(1-1))+5 .. here thursday is 5 and wednesday is 4 and interval is 1
        return (7 - weekDayNumber) + (7 * (interval - 1)) + temp;
    }

    private Map<String, Integer> calculateDaysToAddForWeeklySchedule(List<String> byDays, int interval) {
        Map<String, Integer> dayCostMap = new HashMap<>();
        if (byDays.isEmpty()) throw new RuntimeException("Invalid weekly recurrent rule!");
        // if only single day then just add 7 days + the interval specified
        else if (byDays.size() == 1) dayCostMap.put(byDays.get(0), 7 + (7 * (interval - 1)));
        for (String byDay : byDays) {
            if (byDays.indexOf(byDay) < (byDays.size() - 1)) {
                // if block in case of not the last element
                // get the next day
                String nextByDay = byDays.get(byDays.indexOf(byDay) + 1);
                int cost = Math.abs(WEEKLY_SCHEDULE_POINT.get(byDay.toLowerCase()) - WEEKLY_SCHEDULE_POINT.get(nextByDay.toLowerCase()));
                // set the difference between two days
                dayCostMap.put(byDay, cost);
            } else {
                // else block if the element is last one
                // calculate the remaining days eg. if wed then 7-4
                int lastItemRemainingCost = 7 - WEEKLY_SCHEDULE_POINT.get(byDay.toLowerCase());
                int firstItemCost = WEEKLY_SCHEDULE_POINT.get(byDays.get(0).toLowerCase());
                // set the cost to remaining + first item cost + the interval
                dayCostMap.put(byDay, lastItemRemainingCost + (7 * (interval - 1)) + firstItemCost);
            }
        }
        return dayCostMap;
    }

    public String getRecurrenceException(Date startDateTime) {
        DateFormat formatterUTC = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatterUTC.format(startDateTime);
    }


    // This is not used right now as daily agenda recurrence is disabled from front end
    private List<Schedule> getAgendaForDaily(Schedule parentSchedule, List<Schedule> childSchedules) {
        List<Schedule> finalSchedules = new ArrayList<>();
        long scheduleCount = getCountFromRule(parentSchedule.getRecurrenceRule(), RULE_EVALUATION);
        int interval = getIntervalFromRule(parentSchedule.getRecurrenceRule());
        List<String> parentRecurrenceExceptions = Objects.isNull(parentSchedule.getRecurrenceException()) ? Collections.singletonList("")
                : Arrays.asList(parentSchedule.getRecurrenceException().split(","));
        for (int i = 0; i < scheduleCount; i++) {
            Schedule resultSchedule;
            String recurrenceException = getRecurrenceException(DateUtils.addDays(parentSchedule.getStartTime(), interval * i));
            if (!parentRecurrenceExceptions.contains(recurrenceException)) {
                resultSchedule = new Schedule(parentSchedule);
                if (!(i == 0)) {
                    resultSchedule.setStartTime(DateUtils.addDays(parentSchedule.getStartTime(), interval * i));
                    resultSchedule.setEndTime(DateUtils.addDays(parentSchedule.getEndTime(), interval * i));
                }
            } else {
                Schedule modifiedSchedule = childSchedules.stream()
                        .filter(schedule -> schedule.getRecurrenceException().equals(recurrenceException))
                        .findFirst().orElse(null);
                if (Objects.isNull(modifiedSchedule)) continue;
                resultSchedule = new Schedule(modifiedSchedule);
            }
            finalSchedules.add(resultSchedule);
        }
        return finalSchedules;
    }

    private List<Schedule> getAgendaForWeekly(Schedule parentSchedule, List<Schedule> childSchedules) {
        List<Schedule> finalSchedules = new ArrayList<>();
        long scheduleCount = getCountFromRule(parentSchedule.getRecurrenceRule(), RULE_EVALUATION);
        int interval = getIntervalFromRule(parentSchedule.getRecurrenceRule());
        // get the days like su,mo etc... multiple days is disabled from front end as of now so only single day will be in this list
        List<String> byDays = Arrays.asList(getDaysForWeeklyFromRule(parentSchedule.getRecurrenceRule()).split(","));
        // get the initial increment, ie.. diff between two days like su, wed and the interval between them
        int initialIncrement = getInitialIncrementForWeeklySchedule(byDays, parentSchedule.getStartTime(), interval);
        // contains the cost to move around from one date to another
        Map<String, Integer> dayCostMap = calculateDaysToAddForWeeklySchedule(byDays, interval);
        // store the recurrence exceptions contained in the parent
        List<String> parentRecurrenceExceptions = Objects.isNull(parentSchedule.getRecurrenceException()) ? Collections.singletonList("")
                : Arrays.asList(parentSchedule.getRecurrenceException().split(","));
        parentSchedule.setStartTime(DateUtils.addDays(parentSchedule.getStartTime(), initialIncrement));
        parentSchedule.setEndTime(DateUtils.addDays(parentSchedule.getEndTime(), initialIncrement));
        Date startTimeTracked = parentSchedule.getStartTime();
        Date endTimeTracked = parentSchedule.getEndTime();
        for (int i = 0; i < scheduleCount; i++) {
            Schedule resultSchedule;
            String recurrenceException = getRecurrenceException(startTimeTracked);
            if (!parentRecurrenceExceptions.contains(recurrenceException)) {
                // if parent recurrence exceptions does not contain the new exception
                // then create new schedule and store it
                resultSchedule = new Schedule(parentSchedule);
                resultSchedule.setRecurrenceException(null);

                // setting seriesIdentifier just to find out the parent of the created schedule
                resultSchedule.setSeriesIdentifier(String.valueOf(parentSchedule.getId()));
                if (!(i == 0)) {

                    resultSchedule.setStartTime(startTimeTracked);
                    resultSchedule.setEndTime(endTimeTracked);
                }
                startTimeTracked = DateUtils.addDays(startTimeTracked, dayCostMap.get(getWeekDayName(startTimeTracked)));
                endTimeTracked = DateUtils.addDays(endTimeTracked, dayCostMap.get(getWeekDayName(startTimeTracked)));
            } else {
                // if parent recurrence exceptions does contains the exception
                // then no need to create the new schedule
                Schedule modifiedSchedule = childSchedules.stream()
                        .filter(schedule -> schedule.getRecurrenceException().equals(recurrenceException))
                        .findFirst().orElse(null);
                startTimeTracked = DateUtils.addDays(startTimeTracked, dayCostMap.get(getWeekDayName(startTimeTracked)));
                endTimeTracked = DateUtils.addDays(endTimeTracked, dayCostMap.get(getWeekDayName(startTimeTracked)));
                if (Objects.isNull(modifiedSchedule)) continue;
                // create the schedule from the edited instance of recurrent schedule
                resultSchedule = new Schedule(modifiedSchedule);
            }
            finalSchedules.add(resultSchedule);
        }
        return finalSchedules;
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
                .filter(schedule -> Objects.isNull(schedule.getRecurrenceRule()) &&
                        (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("COMPLETED")).count();
        long totalCompletedSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) &&
                        Objects.nonNull(schedule.getRecurrenceId())
                        && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("COMPLETED")).count();
        return totalCompletedNonSeriesAppts + totalCompletedSeriesAppts;
    }

    public Long getCancelledAppointmentsForSchedules(List<Schedule> schedules) {
        long totalCompletedNonSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.isNull(schedule.getRecurrenceRule()) &&
                        (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("CANCELLED")).count();
        long totalCompletedSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) &&
                        Objects.nonNull(schedule.getRecurrenceId())
                        && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("CANCELLED")).count();
        return totalCompletedNonSeriesAppts + totalCompletedSeriesAppts;
    }

    public Long getNoChargeAppointmentsForSchedules(List<Schedule> schedules) {
        long totalCompletedNonSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.isNull(schedule.getRecurrenceRule())
                        && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("NO_SHOW_NO_CHARGE")).count();
        long totalCompletedSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) &&
                        Objects.nonNull(schedule.getRecurrenceId())
                        && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("NO_SHOW_NO_CHARGE")).count();
        return totalCompletedNonSeriesAppts + totalCompletedSeriesAppts;
    }

    public Long getChargeAppointmentsForSchedules(List<Schedule> schedules) {
        long totalCompletedNonSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.isNull(schedule.getRecurrenceRule())
                        && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("NO_SHOW_CHARGE")).count();
        long totalCompletedSeriesAppts = schedules.stream()
                .filter(schedule -> Objects.nonNull(schedule.getRecurrenceRule()) &&
                        Objects.nonNull(schedule.getRecurrenceId())
                        && (Objects.nonNull(schedule.getIsReadOnly()) && schedule.getIsReadOnly()) && schedule.getStatus().equalsIgnoreCase("NO_SHOW_CHARGE")).count();
        return totalCompletedNonSeriesAppts + totalCompletedSeriesAppts;
    }

    public Long getScheduledAppointments(Schedule schedule, ScheduleEditMode scheduleEditMode) {
        Long totalNonSeriesAppts = this.scheduleRepository
                .countByClientUsernameAndPurchaseSubCategoryAndStatusNotInAndRecurrenceRuleIsNull(schedule.getClientUsername(),
                        schedule.getPurchaseSubCategory(), Arrays.asList(Constants.DELETED_ALIKE_SCHEDULE_STATUS));
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

    public List<Schedule> generateAgendaForSchedules(List<Schedule> schedules) {
        List<Schedule> finalSchedules = new ArrayList<>();
        schedules.forEach(schedule -> {
            if (Objects.isNull(schedule.getRecurrenceRule())) {
                finalSchedules.add(schedule);
            } else if (Objects.nonNull(schedule.getRecurrenceRule())
                    && Objects.isNull(schedule.getRecurrenceId()) && schedule.getRecurrenceRule().contains("DAILY")) {
                List<Schedule> childSchedules = schedules.stream().filter(sch -> Objects.equals(sch.getRecurrenceId(), schedule.getId()))
                        .collect(Collectors.toList());
                finalSchedules.addAll(getAgendaForDaily(schedule, childSchedules));
            } else if (Objects.nonNull(schedule.getRecurrenceRule())
                    && Objects.isNull(schedule.getRecurrenceId()) && schedule.getRecurrenceRule().contains("WEEKLY")) {
                List<Schedule> childSchedules = schedules.stream().filter(sch -> Objects.equals(sch.getRecurrenceId(), schedule.getId()))
                        .collect(Collectors.toList());
                finalSchedules.addAll(getAgendaForWeekly(schedule, childSchedules));
            }
        });

        return finalSchedules;
    }
}
