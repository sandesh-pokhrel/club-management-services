package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.Service;
import com.fitness.clientservice.service.ServiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@AllArgsConstructor
public class ServiceController {

    private final ServiceService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Service> getAll() {
        return this.service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Service save(@RequestBody Service service) {
        return this.service.save(service);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Integer id) {
        this.service.delete(id);
    }
}
