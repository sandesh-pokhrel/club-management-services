package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.ClientAssessment;
import com.fitness.clientservice.service.ClientAssessmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client-assessments")
@AllArgsConstructor
public class ClientAssessmentController {

    private final ClientAssessmentService clientAssessmentService;


    @GetMapping("/{username}")
    public List<ClientAssessment> getAssessmentsForClient(@PathVariable String username) {
        return this.clientAssessmentService.getAllAssessmentsForClient(username);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientAssessment saveClientAssessment(@RequestBody ClientAssessment clientAssessment,
                                                 @RequestParam String username) {


        return this.clientAssessmentService.saveClientAssessment(clientAssessment, username);
    }
}
