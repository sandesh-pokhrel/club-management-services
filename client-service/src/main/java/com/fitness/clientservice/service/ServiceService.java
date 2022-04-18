package com.fitness.clientservice.service;

import com.fitness.clientservice.model.Service;
import com.fitness.clientservice.repository.ServiceRepository;
import com.fitness.sharedapp.exception.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<Service> getAll() {
        return this.serviceRepository.findAll();
    }

    public Service save(Service service) {
        return this.serviceRepository.save(service);
    }

    public void delete(Integer id) {
        try {
            Service service = this.serviceRepository.getById(id);
            this.serviceRepository.delete(service);
        } catch (Exception ex) {
            throw new BadRequestException("Association to service found!");
        }
    }
}
