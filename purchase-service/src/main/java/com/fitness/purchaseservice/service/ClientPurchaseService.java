package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.Schedule;
import com.fitness.purchaseservice.repository.ClientPurchaseRepository;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.AlreadyExistsException;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ClientPurchaseService extends GenericService {

    private final ClientPurchaseRepository clientPurchaseRepository;
    private final ClientPurchaseInstallmentService clientPurchaseInstallmentService;

    private void validatePurchaseScenario(ClientPurchase clientPurchase) {
        if (Objects.nonNull(clientPurchase.getInitialDownpayment())
                && clientPurchase.getInitialDownpayment() > clientPurchase.getPaymentAmount())
            throw new BadRequestException("Downpayment cannot be greater than amount!");
        else if (!Objects.equals(clientPurchase.getInitialDownpayment(), clientPurchase.getPaymentAmount())
            && (Objects.isNull(clientPurchase.getNoOfPostdates()) || clientPurchase.getNoOfPostdates() == 0))
            throw new BadRequestException("Purchase with partial payment must have no. of postdates specified!");
        else if (!Objects.equals(clientPurchase.getInitialDownpayment(), clientPurchase.getPaymentAmount())
                && (Objects.isNull(clientPurchase.getPaymentInterval())))
            throw new BadRequestException("Purchase with partial payment must have a payment interval!");
    }

    public ClientPurchase saveClientPurchase(ClientPurchase clientPurchase) {
        if (this.clientPurchaseRepository
                .existsByClientUsernameAndPurchaseSubCategory(clientPurchase.getClientUsername(), clientPurchase.getPurchaseSubCategory())
        && Objects.isNull(clientPurchase.getId()))
            throw new AlreadyExistsException("A client cannot have two active package of same type!");
        validatePurchaseScenario(clientPurchase);
        ClientPurchase purchase;
        if (Objects.isNull(clientPurchase.getId())) {
            clientPurchase.setApptScheduled(0);
            clientPurchase.setPurchaseDate(new Date());
            purchase = this.clientPurchaseRepository.save(clientPurchase);
            this.clientPurchaseInstallmentService.savePendingInstallmentsDuringPurchase(purchase);
        } else {
            purchase = this.clientPurchaseRepository.save(clientPurchase);
        }
        return purchase;
    }

    public ClientPurchase getPurchaseById(Integer id) {
        return this.clientPurchaseRepository.findById(id).orElseThrow(() -> new NotFoundException("Client purchase not found!"));
    }

    public Page<ClientPurchase> getAllPurchases(Map<String, String> paramMap, Integer clubId) {
        Integer page = getPageNumber(paramMap);
        String orderBy = getOrderBy(paramMap, "clientUsername");
        Sort.Direction order = getOrder(paramMap);
        String search = getSearch(paramMap);
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
                order, orderBy);
        if (Objects.nonNull(search))
            return this.clientPurchaseRepository.search(search, clubId, pageable);
        return this.clientPurchaseRepository.customFindAllByClubId(clubId, pageable);
    }

    public List<ClientPurchase> getAllPurchasesForClient(String username) {
        return this.clientPurchaseRepository.findAllByClientUsername(username);
    }

    public List<ClientPurchase> getAllActivePurchasesForClient(String username) {
        return this.clientPurchaseRepository.findAllByClientUsernameAndApptScheduledNot(username, -1);
    }

    public ClientPurchase getAllActivePurchasesForClientByPurchaseSubCategory(Schedule schedule) {
        return this.clientPurchaseRepository
                .findByClientUsernameAndPurchaseSubCategoryAndApptScheduledNot(schedule.getClientUsername(), schedule.getPurchaseSubCategory(), -1);
    }
}
