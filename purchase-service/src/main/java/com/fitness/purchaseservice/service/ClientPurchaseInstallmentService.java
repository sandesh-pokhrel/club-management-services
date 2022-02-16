package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.ClientPurchaseInstallment;
import com.fitness.purchaseservice.repository.ClientPurchaseInstallmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientPurchaseInstallmentService {

    private final ClientPurchaseInstallmentRepository clientPurchaseInstallmentRepository;

    public List<ClientPurchaseInstallment> getAllInstallmentsByPurchaseId(Integer purchaseId) {
        return this.clientPurchaseInstallmentRepository.findAllByClientPurchaseId(purchaseId);
    }

    public void savePendingInstallmentsDuringPurchase(ClientPurchase clientPurchase) {

    }
}
