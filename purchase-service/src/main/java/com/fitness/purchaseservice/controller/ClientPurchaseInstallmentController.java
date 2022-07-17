package com.fitness.purchaseservice.controller;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.ClientPurchaseInstallment;
import com.fitness.purchaseservice.service.ClientPurchaseInstallmentService;
import com.fitness.purchaseservice.service.ClientPurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/purchase-installments")
public class ClientPurchaseInstallmentController {

    private final ClientPurchaseInstallmentService clientPurchaseInstallmentService;
    private final ClientPurchaseService clientPurchaseService;

    @GetMapping
    public Page<ClientPurchaseInstallment> getInstallments(@RequestParam Map<String, String> paramMap,
                                                           @RequestHeader("Club-Id") Integer clubId) throws ParseException {
        Page<ClientPurchaseInstallment> pageInstallment = this.clientPurchaseInstallmentService.getAllInstallments(paramMap, clubId);
        Map<Integer, ClientPurchase> idPurchaseMap = new HashMap<>();
        // set client purchase object in the installment
        pageInstallment.getContent().forEach(installment -> {
            if (Objects.isNull(idPurchaseMap.get(installment.getClientPurchaseId()))) {
                ClientPurchase purchase = this.clientPurchaseService.getPurchaseById(installment.getClientPurchaseId());
                idPurchaseMap.put(installment.getClientPurchaseId(), purchase);
            }
            installment.setClientPurchase(idPurchaseMap.get(installment.getClientPurchaseId()));
        });
        return pageInstallment;
    }

    @GetMapping("/{purchaseId}")
    public List<ClientPurchaseInstallment> getAllInstallmentsForPurchase(@PathVariable Integer purchaseId) {
        return this.clientPurchaseInstallmentService.getAllInstallmentsByPurchaseId(purchaseId);
    }

    @PostMapping
    public ClientPurchaseInstallment savePurchaseInstallment(@RequestBody ClientPurchaseInstallment clientPurchaseInstallment) {
        return this.clientPurchaseInstallmentService.saveInstallment(clientPurchaseInstallment);
    }

    @DeleteMapping("/{installmentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePurchaseInstallment(@PathVariable Integer installmentId) {
        this.clientPurchaseInstallmentService.deleteInstallment(installmentId);
    }
}
