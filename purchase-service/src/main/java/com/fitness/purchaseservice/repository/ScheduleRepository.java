package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.PurchaseSubCategory;
import com.fitness.purchaseservice.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Long countByClientUsernameAndPurchaseSubCategory(String username, PurchaseSubCategory purchaseSubCategory);

    Long countByClientUsernameAndPurchaseSubCategoryAndStatusIgnoreCase(String username, PurchaseSubCategory purchaseSubCategory, String status);

}
