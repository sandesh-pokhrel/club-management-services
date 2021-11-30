package com.fitness.clientservice.service;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.repository.ClientRepository;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.AlreadyExistsException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
                order, orderBy);
        if (Objects.nonNull(search))
            return this.clientRepository.search(search, pageable);
        return this.clientRepository.findAll(pageable);
    }

    public List<String> getAllClientUsernames() {
        return this.clientRepository.getAllClientUsernames();
    }

    public Client getClientByUsername(String username) {
        return this.clientRepository.findById(username).orElse(null);
    }

    public Client saveClient(Client client) {
        String username = client.getUsername();
        if (this.clientRepository.existsById(username))
            throw new AlreadyExistsException(String.format("Username (%s) already exists!", username));
        else if (this.clientRepository.existsByCellPhone(client.getCellPhone()))
            throw new AlreadyExistsException("Cellphone already exists!");
        else if (this.clientRepository.existsByEmail(client.getEmail()))
            throw new AlreadyExistsException("Email already exists!");
        return this.clientRepository.save(client);
    }

    public Client updateClient(Client client, String username) {
        if (!this.clientRepository.existsById(username))
            throw new NotFoundException(String.format("Cannot update. Username (%s) not found!", username));
        else if (this.clientRepository.existsByCellPhoneAndUsernameNot(client.getCellPhone(), username))
            throw new AlreadyExistsException("Cellphone already exists!");
        else if (this.clientRepository.existsByEmailAndUsernameNot(client.getEmail(), username))
            throw new AlreadyExistsException("Email already exists!");
        return this.clientRepository.save(client);
    }
}
