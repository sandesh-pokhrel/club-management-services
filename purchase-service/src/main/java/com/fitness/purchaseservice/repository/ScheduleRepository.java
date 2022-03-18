package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.PurchaseSubCategory;
import com.fitness.purchaseservice.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Long countByClientUsernameAndPurchaseSubCategoryAndStatusNotInAndRecurrenceRuleIsNull(String username,
                                                                                          PurchaseSubCategory purchaseSubCategory,
                                                                                          List<String> seriesStatus);

    List<Schedule> findAllByClientUsernameAndPurchaseSubCategoryAndRecurrenceRuleIsNotNullAndRecurrenceIdIsNull(String username,
                                                                                           PurchaseSubCategory purchaseSubCategory);

    Long countByClientUsernameAndPurchaseSubCategoryAndStatusIgnoreCase(String username, PurchaseSubCategory purchaseSubCategory, String status);

    List<Schedule> findAllByRecurrenceId(Integer id);

    Optional<Schedule> findByRecurrenceExceptionAndRecurrenceId(String recurrenceException, Integer recurrenceId);

    List<Schedule> findAllByPurchaseId(Integer id);

    List<Schedule> findAllByClientUsername(String username);

    List<Schedule> findAllByTrainerUsername(String username);

    @Query(value = "select * from schedule s join club_management_clients.client cc " +
            "on s.client_username = cc.username where cc.club_id = :clubId", nativeQuery = true)
    List<Schedule> customFindAllByClubId(Integer clubId);
}
