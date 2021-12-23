package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.PurchaseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseSubCategoryRepository extends JpaRepository<PurchaseSubCategory, Integer> {
}
