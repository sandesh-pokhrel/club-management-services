package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.PurchaseCategory;
import com.fitness.purchaseservice.repository.PurchaseCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseCategoryService {

    private final PurchaseCategoryRepository purchaseCategoryRepository;

    public List<PurchaseCategory> getAllPurchases() {
        return this.purchaseCategoryRepository.findAll();
    }
}
