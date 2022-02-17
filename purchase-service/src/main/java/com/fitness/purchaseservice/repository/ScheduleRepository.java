package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.PurchaseSubCategory;
import com.fitness.purchaseservice.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Long countByClientUsernameAndPurchaseSubCategory(String username, PurchaseSubCategory purchaseSubCategory);

    Long countByClientUsernameAndPurchaseSubCategoryAndRecurrenceRuleIsNull(String username, PurchaseSubCategory purchaseSubCategory);

    List<Schedule> findAllByClientUsernameAndPurchaseSubCategoryAndRecurrenceRuleIsNotNullAndRecurrenceIdIsNull(String username,
                                                                                           PurchaseSubCategory purchaseSubCategory);

    Long countByClientUsernameAndPurchaseSubCategoryAndStatusIgnoreCase(String username, PurchaseSubCategory purchaseSubCategory, String status);

    List<Schedule> findAllByRecurrenceId(Integer id);

    Optional<Schedule> findByRecurrenceExceptionAndRecurrenceId(String recurrenceException, Integer recurrenceId);

    List<Schedule> findAllByPurchaseId(Integer id);
}
