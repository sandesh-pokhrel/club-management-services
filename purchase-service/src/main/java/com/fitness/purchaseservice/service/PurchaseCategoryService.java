package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.PurchaseCategory;
import com.fitness.purchaseservice.model.PurchaseSubCategory;
import com.fitness.purchaseservice.repository.PurchaseCategoryRepository;
import com.fitness.purchaseservice.repository.PurchaseSubCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseCategoryService {

    private final PurchaseCategoryRepository purchaseCategoryRepository;
    private final PurchaseSubCategoryRepository purchaseSubCategoryRepository;

    public List<PurchaseCategory> getAllPurchases() {
        return this.purchaseCategoryRepository.findAll();
    }

    public List<PurchaseSubCategory> getSubCategoryByIds(List<Integer> ids) {
        return this.purchaseSubCategoryRepository.findAllById(ids);
    }
}
