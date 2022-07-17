package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.ClientPurchaseInstallment;
import com.fitness.purchaseservice.repository.ClientPurchaseInstallmentRepository;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.service.GenericService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class ClientPurchaseInstallmentService extends GenericService {

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
            for (int i = 0; i < clientPurchase.getNoOfPostdates(); i++) {
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

    public Page<ClientPurchaseInstallment> getAllInstallments(Map<String, String> paramMap, Integer clubId) throws ParseException {

        if (Objects.isNull(clubId)) {
            throw new BadRequestException("Club id is not passed!");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Integer page = getPageNumber(paramMap);
        String orderBy = getOrderBy(paramMap, "expected_pay_date");
        Sort.Direction order = getOrder(paramMap);
        String search = Objects.nonNull(getSearch(paramMap)) ? getSearch(paramMap) : "";
        Date fromDate = Objects.nonNull(paramMap.get("fromDate"))
                ? dateFormat.parse(paramMap.get("fromDate")) : calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date toDate = Objects.nonNull(paramMap.get("toDate")) && Objects.nonNull(paramMap.get("fromDate"))
                ? dateFormat.parse(paramMap.get("toDate")) : calendar.getTime();
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
                order, orderBy);
        return this.clientPurchaseInstallmentRepository.search(fromDate, toDate, search, clubId, pageable);
    }
}
