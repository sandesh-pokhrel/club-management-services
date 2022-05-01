package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.PurchaseCategory;
import com.fitness.purchaseservice.model.PurchaseSubCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseSubCategoryRepository extends JpaRepository<PurchaseSubCategory, Integer> {

    boolean existsById(@NotNull Integer id);

    List<PurchaseSubCategory> findAllByPurchaseCategory(PurchaseCategory purchaseCategory);
}
