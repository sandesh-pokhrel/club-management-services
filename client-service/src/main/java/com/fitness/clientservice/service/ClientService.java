package com.fitness.clientservice.service;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.ClientGoal;
import com.fitness.clientservice.model.Club;
import com.fitness.clientservice.repository.ClientGoalRepository;
import com.fitness.clientservice.repository.ClientRepository;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.AlreadyExistsException;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
@AllArgsConstructor
public class ClientService extends GenericService {

    private final ClientRepository clientRepository;
    private final ClientGoalRepository clientGoalRepository;
    private final ClubService clubService;

    public Page<Client> getAllClients(Map<String, String> paramMap, Integer clubId) {
        if (Objects.isNull(clubId)) {
            throw new BadRequestException("Club id is not passed!");
        }
        Club club = this.clubService.getById(clubId);
        Integer page = getPageNumber(paramMap);
        String orderBy = getOrderBy(paramMap, "username");
        Sort.Direction order = getOrder(paramMap);
        String search = getSearch(paramMap);
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
                order, orderBy);
        if (Objects.nonNull(search))
            return this.clientRepository.search(search, club, pageable);
        return this.clientRepository.findAllByClub(club, pageable);
    }

    public List<String> getAllClientUsernames() {
        return this.clientRepository.getAllClientUsernames();
    }

    public String getClientUsernameConcatFullNameByUsername(String username) {
        return this.clientRepository.getClientUsernameConcatFullNameByUsername(username);
    }

    public Client getClientByUsername(String username) {
        return this.clientRepository.findById(username).orElse(null);
    }

    public Client saveClient(Client client) {
        String username = client.getUsername();
        if (this.clientRepository.existsById(username)) {
            int randomNumber = new Random().nextInt(900) + 100;
            client.setUsername(client.getUsername()+randomNumber);
        } else if (this.clientRepository.existsByCellPhone(client.getCellPhone()))
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

    public void deleteClient(String username) {
        try {
            this.clientRepository.deleteById(username);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Client has other associations. Could not delete the client!");
        }
    }

    public ClientGoal saveClientGoal(ClientGoal clientGoal) {
        return this.clientGoalRepository.save(clientGoal);
    }
}
