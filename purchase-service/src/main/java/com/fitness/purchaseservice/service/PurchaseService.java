package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.Purchase;
import com.fitness.purchaseservice.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public List<Purchase> getAllPurchases() {
        return this.purchaseRepository.findAll();
    }
}
