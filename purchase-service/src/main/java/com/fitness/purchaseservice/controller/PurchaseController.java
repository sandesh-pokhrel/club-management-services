package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.PurchaseCategory;
import com.fitness.purchaseservice.service.PurchaseCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseCategoryService purchaseCategoryService;

    @GetMapping
    public List<PurchaseCategory> getAllPurchases() {
        return this.purchaseCategoryService.getAllPurchases();
    }
}
