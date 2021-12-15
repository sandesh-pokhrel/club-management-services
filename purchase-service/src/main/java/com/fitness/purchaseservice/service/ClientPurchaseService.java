package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.repository.ClientPurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientPurchaseService {

    private final ClientPurchaseRepository clientPurchaseRepository;

    public ClientPurchase saveClientPurchase(ClientPurchase clientPurchase) {
         return this.clientPurchaseRepository.save(clientPurchase);
    }
}
