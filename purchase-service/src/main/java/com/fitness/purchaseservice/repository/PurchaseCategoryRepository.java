package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.PurchaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseCategoryRepository extends JpaRepository<PurchaseCategory, Integer> {
}
