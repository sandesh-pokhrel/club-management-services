package com.fitness.clientservice;

import com.fitness.clientservice.model.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ClientServiceApplication.class)
class ClientServiceApplicationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void getClientByUsername_found() {
        String usernameToTest = "rimesh";
        ResponseEntity<Client> userResponseEntity = testRestTemplate.getForEntity("/clients/{username}", Client.class, usernameToTest);
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(userResponseEntity.getBody()).getLastName()).isEqualTo("don");
    }
}
