package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.ClientPurchaseInstallment;
import com.fitness.purchaseservice.service.ClientPurchaseInstallmentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/purchase-installments")
public class ClientPurchaseInstallmentController {

    private final ClientPurchaseInstallmentService clientPurchaseInstallmentService;

    @GetMapping("/{purchaseId}")
    public List<ClientPurchaseInstallment> getAllInstallmentsForPurchase(@PathVariable Integer purchaseId) {
       return this.clientPurchaseInstallmentService.getAllInstallmentsByPurchaseId(purchaseId);
    }
}
