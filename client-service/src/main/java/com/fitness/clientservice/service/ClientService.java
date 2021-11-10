package com.fitness.clientservice.service;

import com.fitness.clientservice.exception.AlreadyExistsException;
import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ClientService extends GenericService {

    private final ClientRepository clientRepository;

    public Page<Client> getAllClients(Map<String, String> paramMap) {
        Integer page = getPageNumber(paramMap);
        String orderBy = getOrderBy(paramMap, "username");
        Sort.Direction order = getOrder(paramMap);
        String search = getSearch(paramMap);
        Pageable pageable = PageRequest.of(page, 3,
                order, orderBy);
        if (Objects.nonNull(search))
            return this.clientRepository.search(search, pageable);
        return this.clientRepository.findAll(pageable);
    }

    public Client getClientByUsername(String username) {
        return this.clientRepository.findById(username).orElse(Client.builder().lastName("don").build());
    }

    // TODO: Check the uniqueness of cell number and email as well
    public Client saveClient(Client client) {
        String username = client.getUsername();
        if (this.clientRepository.findById(username).isPresent())
            throw new AlreadyExistsException(String.format("User with username %s already exists!", username));
        return this.clientRepository.findById(username).orElse(null);
    }
}
