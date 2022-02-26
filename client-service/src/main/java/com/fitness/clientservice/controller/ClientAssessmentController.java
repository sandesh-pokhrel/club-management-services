package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.ClientAssessment;
import com.fitness.clientservice.service.ClientAssessmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client-assessments")
@AllArgsConstructor
public class ClientAssessmentController {

    private final ClientAssessmentService clientAssessmentService;

    @GetMapping("/{id}")
    public ClientAssessment getAssessmentById(@PathVariable Integer id) {
        return this.clientAssessmentService.getAssessmentById(id);
    }

//    @GetMapping("/{username}")
//    public List<ClientAssessment> getAssessmentsForClient(@PathVariable String username) {
//        return this.clientAssessmentService.getAllAssessmentsForClient(username);
//    }

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
}
