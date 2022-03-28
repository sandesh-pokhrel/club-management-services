package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.ClientAssessment;
import com.fitness.clientservice.service.ClientAssessmentService;
import com.fitness.clientservice.service.ClientService;
import com.fitness.sharedapp.common.MailType;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.util.MailUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/client-assessments")
@AllArgsConstructor
public class ClientAssessmentController {

    private final ClientAssessmentService clientAssessmentService;
    private final ClientService clientService;
    private final MailUtil mailUtil;

    @GetMapping("/{id}")
    public ClientAssessment getAssessmentById(@PathVariable Integer id) {
        return this.clientAssessmentService.getAssessmentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientAssessment saveClientAssessment(@RequestBody ClientAssessment clientAssessment,
                                                 @RequestParam String client,
                                                 @RequestParam String trainer) {
        return this.clientAssessmentService.saveClientAssessment(clientAssessment, client, trainer);
    }

    @DeleteMapping("/{id}")
    public void deleteAssessmentById(@PathVariable Integer id) {
        this.clientAssessmentService.deleteAssessmentById(id);
    }

    @PostMapping(path = "/mail/{username}")
    public void mailAssessmentPDF(@PathVariable String username, @RequestPart("pdf") MultipartFile multipartFile)
            throws MessagingException, IOException {
        Client client = this.clientService.getClientByUsername(username);
        if (Objects.isNull(client))
            throw new NotFoundException("Client not found!");
        mailUtil.sendMail(client.getEmail(), MailType.CLIENT_ASSESSMENT, null, multipartFile.getBytes());
    }
}
