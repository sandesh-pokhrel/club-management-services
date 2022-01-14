package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.PurchaseCategory;
import com.fitness.purchaseservice.model.PurchaseSubCategory;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import com.fitness.purchaseservice.service.PurchaseCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/purchases")
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseCategoryService purchaseCategoryService;
    private final ClientPurchaseService clientPurchaseService;

    @GetMapping
    public List<PurchaseCategory> getAllPurchases() {
        return this.purchaseCategoryService.getAllPurchases();
    }

    @GetMapping("/categories/{username}")
    public List<PurchaseSubCategory> getAllSubCategories(@PathVariable String username) {
        List<ClientPurchase> allActivePurchasesForClient = this.clientPurchaseService.getAllActivePurchasesForClient(username);
        return allActivePurchasesForClient.stream()
                .map(ClientPurchase::getPurchaseSubCategory).collect(Collectors.toList());
    }
}
