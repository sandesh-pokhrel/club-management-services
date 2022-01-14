package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client-purchases")
@AllArgsConstructor
public class ClientPurchaseController {

    private final ClientPurchaseService clientPurchaseService;

    @GetMapping
    public Page<ClientPurchase> getAllClientPurchase(@RequestParam Map<String, String> paramMap) {
        return this.clientPurchaseService.getAllPurchases(paramMap);
    }

    @GetMapping("/{username}")
    public List<ClientPurchase> getAllClientPurchaseForClient(@PathVariable String username) {
        return this.clientPurchaseService.getAllPurchasesForClient(username);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientPurchase saveClientPurchase(@RequestBody ClientPurchase clientPurchase) {
        return this.clientPurchaseService.saveClientPurchase(clientPurchase);
    }
}
