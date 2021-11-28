package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.Purchase;
import com.fitness.purchaseservice.service.PurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    public List<Purchase> getAllPurchases() {
        return this.purchaseService.getAllPurchases();
    }
}
