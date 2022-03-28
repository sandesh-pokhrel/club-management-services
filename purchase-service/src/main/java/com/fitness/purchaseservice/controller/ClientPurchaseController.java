package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import com.fitness.purchaseservice.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client-purchases")
@AllArgsConstructor
public class ClientPurchaseController {

    private final ClientPurchaseService clientPurchaseService;
    private final ScheduleService scheduleService;

    @GetMapping
    public Map<String, Object> getAllClientPurchase(@RequestParam Map<String, String> paramMap,
                                                    @RequestHeader("Club-Id") Integer clubId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Long>> statList = new ArrayList<>();
        Page<ClientPurchase> purchases = this.clientPurchaseService.getAllPurchases(paramMap, clubId);
        List<ClientPurchase> clientPurchases = purchases.getContent();
        clientPurchases.forEach(clientPurchase -> statList.add(this.scheduleService.getTotalScheduled(clientPurchase.getId())));
        result.put("purchases", purchases);
        result.put("purchaseApptScheduledStats", statList);
        return result;
    }

    @GetMapping("/purchase/{id}")
    public ClientPurchase getClientPurchaseById(@PathVariable Integer id) {
        return this.clientPurchaseService.getPurchaseById(id);
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
