package com.fitness.clientservice.service;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.repository.ClientGoalRepository;
import com.fitness.clientservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientGoalRepository clientGoalRepository;

    @BeforeEach
    void setup() {
        clientService = new ClientService(clientRepository, clientGoalRepository, null);
    }

    @Test
    void getClientByUsername() {

        // given
        Client client = Client.builder().lastName("don").build();
        given(clientRepository.findById(anyString())).willReturn(Optional.ofNullable(client));

        // when
        Client actualClient = clientService.getClientByUsername("sandesh");
        assert client != null;
        assertThat(actualClient.getLastName()).isEqualTo(client.getLastName());
        verify(clientRepository).findById(anyString());
    }
}
