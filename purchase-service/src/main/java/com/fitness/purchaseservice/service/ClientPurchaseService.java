package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.repository.ClientPurchaseRepository;
import com.fitness.sharedapp.exception.AlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientPurchaseService {

    private final ClientPurchaseRepository clientPurchaseRepository;

    public ClientPurchase saveClientPurchase(ClientPurchase clientPurchase) {
        if (this.clientPurchaseRepository
                .existsByClientUsernameAndPurchaseSubCategory(clientPurchase.getClientUsername(), clientPurchase.getPurchaseSubCategory()))
            throw new AlreadyExistsException("A client cannot have two active package of same type!");
        return this.clientPurchaseRepository.save(clientPurchase);
    }

    public List<ClientPurchase> getAllActivePurchasesForClient(String username) {
        return this.clientPurchaseRepository.findAllByClientUsernameAndApptScheduledNot(username, -1);
    }
}
