package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.ClientPurchaseInstallment;
import com.fitness.purchaseservice.repository.ClientPurchaseInstallmentRepository;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ClientPurchaseInstallmentService {

    private final ClientPurchaseInstallmentRepository clientPurchaseInstallmentRepository;

    public List<ClientPurchaseInstallment> getAllInstallmentsByPurchaseId(Integer purchaseId) {
        return this.clientPurchaseInstallmentRepository.findAllByClientPurchaseId(purchaseId);
    }

    public ClientPurchaseInstallment saveInstallment(ClientPurchaseInstallment clientPurchaseInstallment) {
        return this.clientPurchaseInstallmentRepository.save(clientPurchaseInstallment);
    }

    public void deleteInstallment(Integer installmentId) {
        ClientPurchaseInstallment clientPurchaseInstallment = this.clientPurchaseInstallmentRepository.findById(installmentId)
                .orElseThrow(() -> new NotFoundException("Purchase installment entry not found!"));
        if (clientPurchaseInstallment.getStatus().equalsIgnoreCase("paid"))
            throw new BadRequestException("Cannot delete already paid installment!");
        this.clientPurchaseInstallmentRepository.deleteById(installmentId);
    }

    public void savePendingInstallmentsDuringPurchase(ClientPurchase clientPurchase) {
        if (Objects.equals(clientPurchase.getInitialDownpayment(), clientPurchase.getPaymentAmount())) {
            ClientPurchaseInstallment clientPurchaseInstallment = ClientPurchaseInstallment.builder()
                    .clientPurchaseId(clientPurchase.getId())
                    .amount(clientPurchase.getPaymentAmount())
                    .expectedPayDate(new Date())
                    .actualPayDate(new Date())
                    .isPif(true)
                    .status("paid")
                    .paymentMethod(clientPurchase.getPaymentMethod())
                    .build();
            this.clientPurchaseInstallmentRepository.save(clientPurchaseInstallment);
        } else {
            Double remainingAmountToBePaid = clientPurchase.getPaymentAmount() - clientPurchase.getInitialDownpayment();
            Double amountInEachInstallment = remainingAmountToBePaid / clientPurchase.getNoOfPostdates();
            List<ClientPurchaseInstallment> installments = new ArrayList<>();
            ClientPurchaseInstallment clientPurchaseInstallment = null;
            for (int i=0; i<clientPurchase.getNoOfPostdates(); i++) {
                Date expectedDate;
                if (Objects.isNull(clientPurchaseInstallment)) {
                    expectedDate = clientPurchase.getFirstPostdate();
                } else {
                    switch (clientPurchase.getPaymentInterval()) {
                        case "Daily":
                            expectedDate = DateUtils.addDays(clientPurchaseInstallment.getExpectedPayDate(), 1);
                            break;
                        case "Weekly":
                            expectedDate = DateUtils.addDays(clientPurchaseInstallment.getExpectedPayDate(), 7);
                            break;
                        case "Yearly":
                            expectedDate = DateUtils.addDays(clientPurchaseInstallment.getExpectedPayDate(), 365);
                            break;
                        case "Monthly":
                        default:
                            expectedDate = DateUtils.addDays(clientPurchaseInstallment.getExpectedPayDate(), 30);
                    }

                }
                clientPurchaseInstallment = ClientPurchaseInstallment.builder()
                        .clientPurchaseId(clientPurchase.getId())
                        .amount(amountInEachInstallment)
                        .expectedPayDate(expectedDate)
                        .actualPayDate(null)
                        .isPif(false)
                        .status("pending")
                        .paymentMethod(clientPurchase.getPaymentMethod())
                        .build();
                installments.add(clientPurchaseInstallment);
            }
            this.clientPurchaseInstallmentRepository.saveAll(installments);
        }
    }
}
