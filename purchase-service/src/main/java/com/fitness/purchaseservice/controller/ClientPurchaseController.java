package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client-purchases")
@AllArgsConstructor
public class ClientPurchaseController {

    private final ClientPurchaseService clientPurchaseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientPurchase saveClientPurchase(@RequestBody ClientPurchase clientPurchase) {
        return this.clientPurchaseService.saveClientPurchase(clientPurchase);
    }
}
