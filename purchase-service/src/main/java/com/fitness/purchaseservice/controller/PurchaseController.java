package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.PurchaseCategory;
import com.fitness.purchaseservice.model.PurchaseSubCategory;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import com.fitness.purchaseservice.service.PurchaseCategoryService;
import com.fitness.sharedapp.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @PostMapping
    public PurchaseCategory savePurchaseCategory(@RequestBody Map<String, String> purchaseCategoryMap) {
        if (Objects.isNull(purchaseCategoryMap) || purchaseCategoryMap.get("name").isEmpty())
            throw new BadRequestException("Provide valid name for purchase category");
        String purchaseCategoryName = purchaseCategoryMap.get("name");
        PurchaseCategory purchaseCategory = PurchaseCategory.builder().categoryName(purchaseCategoryName).build();
        return this.purchaseCategoryService.savePurchaseCategory(purchaseCategory);
    }

}
