package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.repository.ClientPurchaseRepository;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.AlreadyExistsException;
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

    public ClientPurchase saveClientPurchase(ClientPurchase clientPurchase) {
        if (this.clientPurchaseRepository
                .existsByClientUsernameAndPurchaseSubCategory(clientPurchase.getClientUsername(), clientPurchase.getPurchaseSubCategory()))
            throw new AlreadyExistsException("A client cannot have two active package of same type!");
        clientPurchase.setApptScheduled(0);
        clientPurchase.setPurchaseDate(new Date());
        return this.clientPurchaseRepository.save(clientPurchase);
    }

    public Page<ClientPurchase> getAllPurchases(Map<String, String> paramMap) {
        Integer page = getPageNumber(paramMap);
        String orderBy = getOrderBy(paramMap, "clientUsername");
        Sort.Direction order = getOrder(paramMap);
        String search = getSearch(paramMap);
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
                order, orderBy);
        if (Objects.nonNull(search))
            return this.clientPurchaseRepository.search(search, pageable);
        return this.clientPurchaseRepository.findAll(pageable);
    }

    public List<ClientPurchase> getAllPurchasesForClient(String username) {
        return this.clientPurchaseRepository.findAllByClientUsername(username);
    }

    public List<ClientPurchase> getAllActivePurchasesForClient(String username) {
        return this.clientPurchaseRepository.findAllByClientUsernameAndApptScheduledNot(username, -1);
    }
}
