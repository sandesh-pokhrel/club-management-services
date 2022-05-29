package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.ClientQuestion;
import com.fitness.clientservice.service.ClientQuestionService;
import com.fitness.clientservice.service.ClientService;
import com.fitness.sharedapp.common.MailType;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.util.GeneralUtil;
import com.fitness.sharedapp.util.MailUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Objects;

@RestController
@RequestMapping("/client-extra-info")
@AllArgsConstructor
public class ClientQuestionController {

    private final ClientQuestionService clientQuestionService;
    private final GeneralUtil generalUtil;
    private final MailUtil mailUtil;
    private final ClientService clientService;

    @GetMapping("/send-questionnaire/{username}")
    public void sendQuestionnaireToClient(@PathVariable String username) throws MessagingException {
        //this.clientExtraInfoService.checkIfAlreadyQuestioned(username);
        Client client = this.clientService.getClientByUsername(username);
        if (Objects.isNull(client)) {
            throw new NotFoundException("User not found!");
        }
        String serial = generalUtil.getSerialNumber();
        ClientQuestion clientQuestion = new ClientQuestion(username, true, serial);
        this.mailUtil.sendMail(client.getEmail(), MailType.CLIENT_QUESTIONNAIRE, serial, null);
        this.clientQuestionService.save(clientQuestion);
    }

    @GetMapping("/check-questionnaire-serial/{serial}")
    @ResponseStatus(HttpStatus.OK)
    public void isSerialValid(@PathVariable String serial) {
        this.clientQuestionService.checkQuestionnaireSerialValidity(serial);
    }
}
