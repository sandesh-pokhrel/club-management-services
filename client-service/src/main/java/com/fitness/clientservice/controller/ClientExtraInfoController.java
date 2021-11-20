package com.fitness.clientservice.controller;

import com.fitness.clientservice.common.MailType;
import com.fitness.clientservice.exception.NotFoundException;
import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.ClientExtraInfo;
import com.fitness.clientservice.service.ClientExtraInfoService;
import com.fitness.clientservice.service.ClientService;
import com.fitness.clientservice.util.GeneralUtil;
import com.fitness.clientservice.util.MailUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Objects;

@RestController
@RequestMapping("/client-extra-info")
@AllArgsConstructor
public class ClientExtraInfoController {

    private final ClientExtraInfoService clientExtraInfoService;
    private final GeneralUtil generalUtil;
    private final MailUtil mailUtil;
    private final ClientService clientService;

    @GetMapping("/send-questionnaire/{username}")
    public void sendQuestionnaireToClient(@PathVariable String username) throws MessagingException {
        this.clientExtraInfoService.checkIfAlreadyQuestioned(username);
        Client client = this.clientService.getClientByUsername(username);
        if (Objects.isNull(client)) {
            throw new NotFoundException("User not found!");
        }
        String serial = generalUtil.getSerialNumber();
        ClientExtraInfo clientExtraInfo = new ClientExtraInfo(username, true, serial);
        this.mailUtil.sendMail(client.getEmail(), MailType.CLIENT_QUESTIONNAIRE, serial);
        this.clientExtraInfoService.save(clientExtraInfo);
    }

    @GetMapping("/check-questionnaire-serial/{serial}")
    @ResponseStatus(HttpStatus.OK)
    public void isSerialValid(@PathVariable String serial) {
        this.clientExtraInfoService.checkQuestionnaireSerialValidity(serial);
    }
}
