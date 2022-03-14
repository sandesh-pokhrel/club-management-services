package com.fitness.clientservice.service;

import com.fitness.clientservice.model.Club;
import com.fitness.clientservice.repository.ClubRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

    public List<Club> getAll() {
        return this.clubRepository.findAll();
    }
}
